//
//  Notification.swift
//  iTBra
//
//  Created by Perry on 2018/3/22.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

import Foundation

struct DeviceStatusFromNotification {
    var isSensorsConnected: Bool
    var isSensorsInRange: Bool
    var measureStatus: MeasurementStatus?
}

extension DeviceStatusFromNotification: CustomStringConvertible {
    var description: String {
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
        return "Sensors are \(connection), \(inRange), status is \(status)"
    }
}

enum BatteryStatus: String {
    case enough = "00"
    case notEnough = "01"
}

enum PeripheralStatus: String {
    case leftPatchDetached = "00"
    case leftPathAttached = "01"
    case rifhtPatchDetached = "02"
    case rightPathAttached = "03"
    case leftPathUDIIncorrect = "04"
    case rightPathUDIIncorrect = "05"
    case recorderEEpromReadWriteIncorrect = "06"
}
