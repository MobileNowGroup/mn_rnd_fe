//
//  BLEManager.swift
//  iTBra
//
//  Created by Perry on 05/02/2018.
//  Copyright © 2018 Cyrcadia. All rights reserved.
//

import CoreBluetooth
import RxSwift

let advertisementServiceCBUUID = CBUUID(string: "8EC90001-F315-4F60-9FB8-838830DAEA50")
let UARTServiceCBUUID = CBUUID(string: "6E400001-B5A3-F393-E0A9-E50E24DCCA9E")
let RXCharacteristicCBUUID = CBUUID(string: "6E400002-B5A3-F393-E0A9-E50E24DCCA9E")
let TXCharacteristicCBUUID = CBUUID(string: "6E400003-B5A3-F393-E0A9-E50E24DCCA9E")
let savedPeripharalUUIDKey = "savedPeripharalUUIDKey"

class BLEManager: NSObject {
    
    static let shared = BLEManager()
    
    // MARK: - public properties
    var updatedValue: Observable<Result<Data>> {
        return updatedValueSubject.asObservable()
    }
    var discoveredCharacteristics: Observable<([CBCharacteristic], Error?)> {
        return discoveredCharacteristicsSubject.asObservable()
    }
    var updatedState: Observable<CBManagerState> {
        return updatedStateSubject.asObservable()
    }
    var connectedPeripheral: Observable<CBPeripheral> {
        return connectedPeripheralSubject.asObservable()
    }
    var disconnectedPeripheral: Observable<(CBPeripheral, Error?)> {
        return disconnectedPeripheralSubject.asObservable()
    }
    var connectionStateOfPeripheral: Observable<(CBPeripheralState, CBPeripheral?, Error?)> {
        return connectionStateOfPeripheralSubject.asObservable()
    }
    
    // MARK: - private properties
    
    fileprivate var centralManager: CBCentralManager!
    fileprivate var peripheral: CBPeripheral!
    fileprivate var peripherals = [CBPeripheral]()
    fileprivate var rxCharacteristic: CBCharacteristic?
    fileprivate var rxCharacteristics = [(CBPeripheral, CBCharacteristic)]()
    
    fileprivate let updatedValueSubject = PublishSubject<Result<Data>>()
    fileprivate let discoveredCharacteristicsSubject = PublishSubject<([CBCharacteristic], Error?)>()
    fileprivate let updatedStateSubject = ReplaySubject<CBManagerState>.create(bufferSize: 1)
    fileprivate let connectedPeripheralSubject = PublishSubject<CBPeripheral>()
    fileprivate let disconnectedPeripheralSubject = PublishSubject<(CBPeripheral, Error?)>()
    fileprivate let connectionStateOfPeripheralSubject = BehaviorSubject<(CBPeripheralState, CBPeripheral?, Error?)>(value: (.disconnected, nil, nil))
    
    override init() {
        super.init()
        centralManager = CBCentralManager(delegate: self, queue: nil)
    }
    
    func writeValue(_ data: Data) {
        guard peripheral != nil else {
            self.updatedValueSubject
                .onNext(Result.failure(BlueToothError.peripheralNotDiscovered))
            return
        }
        guard rxCharacteristic != nil else {
            self.updatedValueSubject
                .onNext(Result.failure(BlueToothError.rxCharacteristicNotFound))
            return
        }

        switch peripheral.state {
        case .connected:
//            return
//            print("send data \(Converter.hexString(from: data))")
//            peripheral.writeValue(data, for: self.rxCharacteristic!, type: .withResponse)
            
            for (aPeripheral, arxCharacteristic) in rxCharacteristics {
                print("send data \(Converter.hexString(from: data))")
                aPeripheral.writeValue(data, for: arxCharacteristic, type: .withResponse)
            }
        default:
            self.updatedValueSubject
                .onNext(Result.failure(BlueToothError.peripheralNotConnected))
        }
    }
    
    fileprivate func scanForPeripharals() {
        centralManager.scanForPeripherals(withServices: [advertisementServiceCBUUID])
//        centralManager.scanForPeripherals(withServices: nil)
    }
}

extension BLEManager: CBCentralManagerDelegate {
    
    func centralManagerDidUpdateState(_ central: CBCentralManager) {
        switch central.state {
        case .unknown:
            print("central.state is .unknown")
        case .resetting:
            print("central.state is .resetting")
        case .unsupported:
            print("central.state is .unsupported")
        case .unauthorized:
            print("central.state is .unauthorized")
        case .poweredOff:
            print("central.state is .poweredOff")
        case .poweredOn:
            print("central.state is .poweredOn")
            scanForPeripharals()
        }
        updatedStateSubject.onNext(central.state)
    }
    
    func centralManager(_ central: CBCentralManager, didDiscover peripheral: CBPeripheral, advertisementData: [String: Any], rssi RSSI: NSNumber) {
        
        print(peripheral)
        self.peripheral = peripheral
        self.peripherals.append(peripheral)
        peripheral.delegate = self
        centralManager.connect(peripheral)
        return
        
        let savedPeripharalUUID = UserDefaults.standard.object(forKey: savedPeripharalUUIDKey) as? String
        if peripheral.identifier.uuidString == savedPeripharalUUID ||
            savedPeripharalUUID == nil {
            centralManager.stopScan()
            print(peripheral)
            self.peripheral = peripheral
            self.peripheral.delegate = self
            centralManager.connect(self.peripheral)
        }
    }
    
    func centralManager(_ central: CBCentralManager, didFailToConnect peripheral: CBPeripheral, error: Error?) {
        print("fail to connect")
        scanForPeripharals()

        connectionStateOfPeripheralSubject.onNext((peripheral.state, peripheral, error))
    }
    
    func centralManager(_ central: CBCentralManager, didConnect peripheral: CBPeripheral) {
        print("Connected!")
        UserDefaults.standard.set(peripheral.identifier.uuidString, forKey: savedPeripharalUUIDKey)
        peripheral.discoverServices([UARTServiceCBUUID])
        
        connectedPeripheralSubject.onNext(peripheral)
        connectionStateOfPeripheralSubject.onNext((peripheral.state, peripheral, nil))
    }
    
    func centralManager(_ central: CBCentralManager, didDisconnectPeripheral peripheral: CBPeripheral, error: Error?) {
        print("Disconnected!")
        scanForPeripharals()
        
        disconnectedPeripheralSubject.onNext((peripheral, error))
        connectionStateOfPeripheralSubject.onNext((peripheral.state, peripheral, error))
    }
}

extension BLEManager: CBPeripheralDelegate {
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverServices error: Error?) {
        guard let services = peripheral.services else { return }
        print(peripheral)
        for service in services {
            print(service)
            peripheral.discoverCharacteristics([RXCharacteristicCBUUID, TXCharacteristicCBUUID], for: service)
        }
    }
    
    func peripheral(_ peripheral: CBPeripheral, didDiscoverCharacteristicsFor service: CBService, error: Error?) {
        guard let characteristics = service.characteristics else { return }
        
        for characteristic in characteristics {
            print(characteristic)
            
            if characteristic.properties.contains(.write) {
                print("\(characteristic.uuid): properties contains .write")
                rxCharacteristic = characteristic
                let isContained =  rxCharacteristics.contains(where: { (aPeripheral, aCharacteristic) -> Bool in
                    return aPeripheral.identifier.uuidString == peripheral.identifier.uuidString
                })
                if !isContained {
                    rxCharacteristics.append((peripheral, characteristic))
                }
            }
            if characteristic.properties.contains(.notify) {
                print("\(characteristic.uuid): properties contains .notify")
                peripheral.setNotifyValue(true, for: characteristic)
            }
        }
        discoveredCharacteristicsSubject.onNext((characteristics, error))
    }
    
    func peripheral(_ peripheral: CBPeripheral, didUpdateValueFor characteristic: CBCharacteristic, error: Error?) {
        
        if let error = error {
            updatedValueSubject.onNext(Result.failure(error))
            return
        }
        
        switch characteristic.uuid {
        case TXCharacteristicCBUUID:
            guard let value = characteristic.value else { return }
            print("receive data \(Converter.hexString(from: value))")
            updatedValueSubject.onNext(Result.success(value))
        default:
            print("Unhandled Characteristic UUID: \(characteristic.uuid)")
        }
    }

}
