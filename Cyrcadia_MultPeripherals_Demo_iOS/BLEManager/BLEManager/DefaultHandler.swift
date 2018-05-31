//
//  AutoSetter.swift
//  iTBra
//
//  Created by Perry on 2018/3/23.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

import Foundation
import RxSwift
import RxCocoa
import SwifterSwift

class DefaultHandler: NSObject {
    
    static let shared = DefaultHandler()
    
    // MARK: - private properties
    private let bag = DisposeBag()
    private var deviceStatus: BehaviorRelay<DeviceStatus>!
    
    override init() {
        super.init()
        
        deviceStatus = BehaviorRelay(value: DeviceStatus(isSensorsConnected: false, isSensorsInRange: false, measureStatus: .inProgress, patchConnection: nil, controlFlag: nil))
        
        BLEManager.shared.discoveredCharacteristics.flatMap { _ in
            return Communicator.shared.send(.deviceStatusRetrieve)
            }.subscribe(onNext: { [unowned self] (result) in
                switch result {
                case .success(let t):
                    if t.command == .deviceStatusRetrieve {
                        self.deviceStatus.accept(t.data as! DeviceStatus)
                    }
                case .failure:
                    break
                }
            }).disposed(by: bag)
        
        deviceStatus.filter { (deviceStatus) -> Bool in
            // 只有处于warmUp才能设置时间
            return deviceStatus.measureStatus == .warmUp
            }.flatMap { _ in
                Communicator.shared.send(.systemTimeConfigure)
            }.subscribe(onNext: {(result) in
                print(result)
            }).disposed(by: bag)
        
        deviceStatus.filter { (deviceStatus) -> Bool in
            return deviceStatus.measureStatus == .warmUp
            }.flatMap { _ in
                Communicator.shared.send(.temperatureRangeRetrieve)
            }.subscribe(onNext: { [unowned self] (result) in
                print(result)
                switch result {
                case .success(let t):
                    if t.command == .temperatureRangeRetrieve {
                        let hexString = t.data as! HexString
                        self.handleTemperatureRangeRetrieve(hexString)
                    }
                case .failure:
                    break
                }
            }).disposed(by: bag)
        
        deviceStatus.filter { (deviceStatus) -> Bool in
            return deviceStatus.measureStatus == .warmUp
            }.flatMap { _ in
                Communicator.shared.send(.cycleAndSampleRateRetrieve)
            }.subscribe(onNext: { [unowned self] (result) in
                print(result)
                switch result {
                case .success(let t):
                    if t.command == .cycleAndSampleRateRetrieve {
                        let hexString = t.data as! HexString
                        self.handleCycleAndSampleRateRetrieve(hexString)
                    }
                case .failure:
                    break
                }
            }).disposed(by: bag)
    }
    
    // MARK: - helper
    
    func handleTemperatureRangeRetrieve(_ hex: HexString) {
        let highThreshold = hex.slicing(from: 0, length: 4)
        let lowThreshold = hex.slicing(from: 4, length: 4)
        if Converter.temperature(from: highThreshold!) != Default.temperatureHighThreshold ||
            Converter.temperature(from: lowThreshold!) != Default.temperatureLowThreshold {
            Communicator.shared.send(.temperatureRangeConfigure)
                .subscribe(onNext: { result in
                    print(result)
                }).disposed(by: bag)
        }
    }
    
    func handleCycleAndSampleRateRetrieve(_ hex: HexString) {
        let measurementCycle = hex.slicing(from: 0, length: 2) // minute
        let samplingRate = hex.slicing(from: 2, length: 2) // second
        if Converter.int(from: measurementCycle!) != Default.measurementCycle ||
            Converter.int(from: samplingRate!) != Default.sampleRate {
            Communicator.shared.send(.cycleAndSampleRateConfigure)
                .subscribe(onNext: { result in
                    print(result)
                }).disposed(by: bag)
        }
    }
}
