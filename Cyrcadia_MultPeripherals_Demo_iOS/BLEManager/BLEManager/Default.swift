//
//  Default.swift
//  iTBra
//
//  Created by Perry on 2018/3/2.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

#if DEVELOPMENT
    struct Default {
        static let temperatureLowThreshold: TemperatureFloat = 1.00
        static let temperatureHighThreshold: TemperatureFloat = 45.00
        static let measurementCycle = 1 // minute, total time
        static let sampleRate = 10 // second
        static let pairingCode = "123456"
        static let warmUpDuration = 1 // minute
    }
#else
    struct Default {
        static let temperatureLowThreshold: TemperatureFloat = 1.00
        static let temperatureHighThreshold: TemperatureFloat = 45.00
        static let measurementCycle = 2*60 // minute, total time
        static let sampleRate = 10 // second
        static let pairingCode = "123456"
        static let warmUpDuration = 2 // minute
    }
#endif
