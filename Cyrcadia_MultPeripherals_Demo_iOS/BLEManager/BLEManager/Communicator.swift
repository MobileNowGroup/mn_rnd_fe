//
//  Communicator.swift
//  iTBra
//
//  Created by Perry on 2018/3/1.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

import Foundation
import RxSwift
import SwifterSwift

class Communicator {
    
    static let shared = Communicator()
    
    // MARK: - public properties
    var notification: Observable<Result<(command: Packet.Command, data: Any)>> {
        return resultPublishSubject.asObservable().filter({ (result) -> Bool in
            switch result {
            case .success(let t):
                return (t.command == Packet.Command.errorMessageNotification) ||
                    (t.command == Packet.Command.lowBatteryNotification) ||
                    (t.command == Packet.Command.peripheralErrorNotification)
            case .failure:
                return false
            }
        })
    }
    var deviceStatus: Observable<Result<(command: Packet.Command, data: Any)>> {
        return resultPublishSubject.asObservable().filter({ (result) -> Bool in
            switch result {
            case .success(let t):
                return t.command == Packet.Command.deviceStatusRetrieve
            case .failure:
                return false
            }
        })
    }
    var temperature: Observable<Result<(command: Packet.Command, data: Any)>> {
        return resultPublishSubject.asObservable().filter({ (result) -> Bool in
            switch result {
            case .success(let t):
                return t.command == Packet.Command.temperatureRetrieve
            case .failure:
                return false
            }
        })
    }
    
    // MARK: - private properties
    private let bag = DisposeBag()
    private var result: Observable<Result<(command: Packet.Command, data: Any)>> {
        return resultPublishSubject.asObservable()
    }
    fileprivate let resultPublishSubject = PublishSubject<Result<(command: Packet.Command, data: Any)>>()
    
    func start() {
        _ = BLEManager.shared
        _ = DefaultHandler.shared
                
        BLEManager.shared.updatedValue.subscribe(onNext: { [unowned self] result in
            switch result {
            case .success(let data):
                self.resultPublishSubject.onNext(self.handle(data))
            case .failure(let error):
                self.resultPublishSubject.onNext(Result.failure(error))
            }
        }).disposed(by: bag)
    }
    
    func send(_ command: Packet.Command) -> Observable<Result<(command: Packet.Command, data: Any)>> {
        
        var packet = Packet.header
        packet.append(command.rawValue)
        packet.append(Packet.dataSize(of: command))
        
        switch command {
        case .systemTimeConfigure:
            packet.append(Converter.hexString(from: Date()))
        case .temperatureRangeConfigure:
            packet.append(Converter.hexString(from: Default.temperatureHighThreshold))
            packet.append(Converter.hexString(from: Default.temperatureLowThreshold))
            packet.append(Converter.hexString(from: Default.temperatureHighThreshold))
            packet.append(Converter.hexString(from: Default.temperatureLowThreshold))
        case .cycleAndSampleRateConfigure:
            packet.append(Converter.hexString(from: Default.measurementCycle))
            packet.append(Converter.hexString(from: Default.sampleRate))
        default:
            break
        }
        
        let data = Converter.data(from: packet)
        
        let observable = result.filter({ result -> Bool in
            switch result {
            case .success(let t):
                return t.command == command
            case .failure(let error):
                switch error {
                case ResultError.statusError(let command, _):
                    return command == command
                default:
                    return true
                }
            }
        }).take(1)
        
        DispatchQueue.main.asyncAfter(deadline: .now() + 0.1) {
            BLEManager.shared.writeValue(data)
        }
        
        return observable
    }
    
    // MARK: - helper methods
    
    private func handle(_ data: Data) -> Result<(command: Packet.Command, data: Any)> {
        var hexString = Converter.hexString(from: data)
        
        // header
        guard let header = hexString.slicing(from: 0, length: 4),
            header == Packet.header
        else {
            return Result.failure(ResultError.structureError)
        }
        hexString = hexString.slicing(at: 4)!
        
        // command
        guard let commandStr = hexString.slicing(from: 0, length: 2),
            let command = Packet.Command(rawValue: commandStr)
        else {
            return Result.failure(ResultError.structureError)
        }
        hexString = hexString.slicing(at: 2)!
        
        // data size
        guard hexString.slicing(from: 0, length: 4) != nil else {
            return Result.failure(ResultError.structureError)
        }
        hexString = hexString.slicing(at: 4)!
        
        if command !=  .lowBatteryNotification &&
            command != .errorMessageNotification &&
            command != .peripheralErrorNotification {
            // status: success or failure
            guard let statusStr = hexString.slicing(from: 0, length: 2),
                let status = Packet.Status(rawValue: statusStr) else {
                    return Result.failure(ResultError.structureError)
            }
            switch status {
            case .Success:
                break
            case .Failure:
                return Result.failure(ResultError.statusError((command: command, status: status)))
            }
            hexString = hexString.slicing(at: 2) ?? ""
        }

        switch command {
        case .errorMessageNotification:
            guard hexString.count == 2*5 else {
                return Result.failure(ResultError.structureError)
            }
            let isSensorsConnected = Converter.int(from: hexString.slicing(from: 0, length: 4)!) == 0
            let isSensorsInRange = Converter.int(from: hexString.slicing(from: 4, length: 4)!) == 0
            let status = hexString.slicing(from: 8, length: 2)
            let measurementStatus = MeasurementStatus(rawValue: status!)!
            let notificationDeviceStatus = DeviceStatusFromNotification(isSensorsConnected: isSensorsConnected, isSensorsInRange: isSensorsInRange, measureStatus: measurementStatus)
            return Result.success((command, notificationDeviceStatus))
            
        case .temperatureRangeRetrieve:
            guard hexString.count == 2*8 else {
                return Result.failure(ResultError.structureError)
            }
            return Result.success((command, hexString))
            
        case .cycleAndSampleRateRetrieve:
            guard hexString.count == 2*2 else {
                return Result.failure(ResultError.structureError)
            }
            return Result.success((command, hexString))
            
        case .deviceStatusRetrieve:
            guard hexString.count == 2*7 else {
                return Result.failure(ResultError.structureError)
            }
            let patchConnection = PatchConnection(rawValue: Converter.int(from: hexString.slicing(from: 0, length: 2)!))
            let isSensorsConnected = Converter.int(from: hexString.slicing(from: 2, length: 4)!) == 0
            let isSensorsInRange = Converter.int(from: hexString.slicing(from: 6, length: 4)!) == 0
            let controlFlag: ControlFlag = Converter.int(from: hexString.slicing(from: 10, length: 2)!) == 0 ? .configureFinished : .configuring
            let status = hexString.slicing(from: 12, length: 2)
            let measurementStatus = MeasurementStatus(rawValue: status!)!
            let deviceStatus = DeviceStatus(isSensorsConnected: isSensorsConnected, isSensorsInRange: isSensorsInRange, measureStatus: measurementStatus, patchConnection: patchConnection!, controlFlag: controlFlag)
            return Result.success((command, deviceStatus))
            
        case .batteryStatusRetrieve,
             .lowBatteryNotification:
            guard hexString.count == 2 else {
                return Result.failure(ResultError.structureError)
            }
            let batteryStatus = BatteryStatus(rawValue: hexString)
            return Result.success((command, batteryStatus!))
            
        case .temperatureRetrieve:
            if hexString.slicing(from: 0, length: 4) == Packet.temperatureRetrieveEndingTag {
                // ending tag
                return Result.success((command, Packet.temperatureRetrieveEndingTag))
            }
            let cycleIndex = hexString.slicing(from: 0, length: 4)
            let patchConnection = PatchConnection(rawValue: Converter.int(from: hexString.slicing(from: 4, length: 2)!))
            let timeStamp = try! Converter.date(from: hexString.slicing(from: 6, length: 7*2)!)
            let temperatureData = hexString.slicing(at: 20)
            let temperature = Temperature(cycleIndex: cycleIndex!, patchConnection: patchConnection!, timeStamp: timeStamp, temperatureData: temperatureData!)
            return Result.success((command, temperature))
            
        case .peripheralErrorNotification:
            guard hexString.count == 2 else {
                return Result.failure(ResultError.structureError)
            }
            let periphralStatus = PeripheralStatus(rawValue: hexString)
            return Result.success((command, periphralStatus!))
            
        default:
            return Result.success((command, hexString))
        }
    }
}
