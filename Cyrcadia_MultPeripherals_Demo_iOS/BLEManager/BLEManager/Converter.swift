//
//  Converter.swift
//  iTBra
//
//  Created by Perry on 2018/3/1.
//  Copyright © 2018年 Cyrcadia. All rights reserved.
//

import Foundation
import SwifterSwift

enum ConverterError: Error {
    case lengthIncorrect
}

typealias HexString = String
typealias TemperatureFloat = Float

class Converter {
    
    // MARK: - data ~ hex string
    class func data(from hex: HexString) -> Data {
        var hex = hex
        var data = Data()
        while hex.count > 0 {
            let subIndex = hex.index(hex.startIndex, offsetBy: 2)
            let c = String(hex[..<subIndex])
            hex = String(hex[subIndex...])
            var ch: UInt32 = 0
            Scanner(string: c).scanHexInt32(&ch)
            var char = UInt8(ch)
            data.append(&char, count: 1)
        }
        return data
    }
    
    struct HexEncodingOptions: OptionSet {
        let rawValue: Int
        static let upperCase = HexEncodingOptions(rawValue: 1 << 0)
    }
    
    class func hexString(from data: Data) -> HexString {
        let hexDigits = Array("0123456789ABCDEF".utf16)
        var chars: [unichar] = []
        chars.reserveCapacity(2 * data.count)
        for byte in data {
            chars.append(hexDigits[Int(byte / 16)])
            chars.append(hexDigits[Int(byte % 16)])
        }
        return String(utf16CodeUnits: chars, count: chars.count)
    }
    
    // MARK: - hex string ~ text
    
    class func text(from hex: HexString) -> String {
        
        let regex = try! NSRegularExpression(pattern: "(0x)?([0-9A-Fa-f]{2})", options: .caseInsensitive)
        let textNS = hex as NSString
        let matchesArray = regex.matches(in: textNS as String, options: [], range: NSMakeRange(0, textNS.length))
        let characters = matchesArray.map {
            Character(UnicodeScalar(UInt32(textNS.substring(with: $0.range(at: 2)), radix: 16)!)!)
        }
        
        return String(characters)
    }
    
    // MARK: - hex string ~ int
    
    class func hexString(from int: Int) -> HexString {
        let hexString = String(int, radix: 16)
        let complement = Int(pow(2, ceil(Double(hexString.count)/2.0)))
        return String(format: "%0\(complement)X", arguments: [int])
    }
    
    class func hexString(from int: Int, complement: Int) -> HexString {
        return String(format: "%0\(complement)X", arguments: [int])
    }
    
    class func int(from hex: HexString) -> Int {
        return Int(hex, radix: 16)!
    }
    
    // MARK: - date ~ hex string
    
    class func hexString(from date: Date) -> HexString {
        
        let calendar = Calendar.current
        let year = calendar.component(.year, from: date)
        let month = calendar.component(.month, from: date)
        let day = calendar.component(.day, from: date)
        let hour = calendar.component(.hour, from: date)
        let minute = calendar.component(.minute, from: date)
        let second = calendar.component(.second, from: date)
        
        let hexString = "\(self.hexString(from: year))\(self.hexString(from: month))\(self.hexString(from: day))\(self.hexString(from: hour))\(self.hexString(from: minute))\(self.hexString(from: second))"
        
        return hexString
    }
    
    class func date(from hex: HexString) -> Date {
        
        let year = int(from: hex.slicing(from: 0, length: 4)!)
        let month = int(from: hex.slicing(from: 4, length: 2)!)
        let day = int(from: hex.slicing(from: 6, length: 2)!)
        let hour = int(from: hex.slicing(from: 8, length: 2)!)
        let minute = int(from: hex.slicing(from: 10, length: 2)!)
        let second = int(from: hex.slicing(from: 12, length: 2)!)
        let dateString = "\(year)/\(month)/\(day) \(hour):\(minute):\(second)"
        
        let dateFormatter = DateFormatter()
        dateFormatter.dateFormat = "yyyy/MM/dd HH:mm:ss"
        return dateFormatter.date(from: dateString)!
    }
    
    // MARK: - temperature ~ hex
    
    class func temperature(from hex: HexString) -> TemperatureFloat {
        return TemperatureFloat(int(from: hex))/Float(100)
    }
    
    class func hexString(from temperature: TemperatureFloat) -> HexString {
        return hexString(from: Int(temperature*100), complement: 4)
    }
}
