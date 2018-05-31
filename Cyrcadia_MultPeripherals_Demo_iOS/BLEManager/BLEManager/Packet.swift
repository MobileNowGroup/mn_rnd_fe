//
//  ProtocolConstants.swift
//  iTBra
//
//  Created by Perry on 2018/3/1.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

struct Packet {
    
    static let header = "AA55"
    
    enum Command: String {
        // configure
        case systemTimeConfigure = "10"
        case temperatureRangeConfigure = "11"
        case cycleAndSampleRateConfigure = "12"
        case factoryResetConfigure = "14"
        case measurementStartConfigure = "16"
        case warmUpStartConfigure = "17"
        case clearMemoryConfigure = "18"
        // retrieve
        case deviceUIDRetrieve = "30"
        case devieceInformationRetrieve = "31"
        case temperatureRangeRetrieve = "32"
        case cycleAndSampleRateRetrieve = "33"
        case deviceStatusRetrieve = "35"
        case temperatureRetrieve = "36"
        case batteryStatusRetrieve = "37"
        case temperatureRecordStatusRetrieve = "38"
        // notification
        case errorMessageNotification = "50"
        case lowBatteryNotification = "51"
        case peripheralErrorNotification = "52"
    }
    
    enum Status: String {
        case Success = "81"
        case Failure = "80"
    }
    
    static let temperatureRetrieveEndingTag = "FFFF"
    
    static func dataSize(of command: Packet.Command) -> String {
        var dataSize: String
        switch command {
        case .systemTimeConfigure:
            dataSize = "0007"
        case .temperatureRangeConfigure:
            dataSize = "0008"
        case .cycleAndSampleRateConfigure:
            dataSize = "0002"
        default:
            dataSize = "0000"
        }
        return dataSize
    }
}
