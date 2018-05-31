//
//  DeviceStatus.swift
//  iTBra
//
//  Created by Perry on 2018/3/6.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

enum PatchConnection: Int {
    case bothConnected = 0
    case leftConnected = 1
    case rightConnected = 2
}

enum SensorConnection: Int {
    case ok = 0
    case lose = 1
}

enum SensorInRange: Int {
    case inRange = 0
    case overTheRange = 1
}

enum ControlFlag: Int {
    case configureFinished = 0
    case configuring = 1
}

enum MeasurementStatus: String {
    case warmUp = "00"
    case inProgress = "01"
    case aborted = "02"
    case successful = "03"
}

struct DeviceStatus {
    var isSensorsConnected: Bool
    var isSensorsInRange: Bool
    var measureStatus: MeasurementStatus?
    var patchConnection: PatchConnection?
    var controlFlag: ControlFlag?
}

extension DeviceStatus: CustomStringConvertible {
    var description: String {
        var patchConnectionStr: String
        switch patchConnection! {
        case .bothConnected:
            patchConnectionStr = "both"
        case .leftConnected:
            patchConnectionStr = "left"
        case .rightConnected:
            patchConnectionStr = "right"
        }
        var controlFlagStr: String
        switch controlFlag! {
        case .configureFinished:
            controlFlagStr = "finished"
        case .configuring:
            controlFlagStr = "configuring"
        }
        let connection = isSensorsConnected ? "connected" : "not connected"
        let inRange = isSensorsInRange ? "in range" : "not in range"
        var status: String
        switch measureStatus! {
        case .warmUp:
            status = "warm up"
        case .inProgress:
            status = "in progress"
        case .aborted:
            status = "aborted"
        case .successful:
            status = "successful"
        }
        return "Sensors are \(connection), \(inRange), status is \(status), patchs connection is \(patchConnectionStr), control flag is \(controlFlagStr)"
    }
}
