//
//  Error.swift
//  iTBra
//
//  Created by Perry on 2018/3/8.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

enum Result<T> {
    case success(T)
    case failure(Error)
}

enum BlueToothError: Error {
    case peripheralNotDiscovered
    case peripheralNotConnected
    case rxCharacteristicNotFound
}

enum ResultError: Error {
    
    case structureError
    
    // status error
    case statusError((command: Packet.Command, status: Packet.Status))
}
