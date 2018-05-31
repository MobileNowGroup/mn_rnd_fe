//
//  TableViewController.swift
//  BLEManager
//
//  Created by Perry on 2018/5/28.
//  Copyright © 2018 Mobilenow. All rights reserved.
//

import UIKit
import RxSwift
import CoreBluetooth

class TableViewController: UITableViewController {
    
    let bag = DisposeBag()
    var peripherals = [CBPeripheral]()

    override func viewDidLoad() {
        super.viewDidLoad()
        
        BLEManager.shared.connectedPeripheral.subscribe(onNext: { [unowned self] (peripheral) in
            print(peripheral)
            if !self.peripherals.contains(peripheral) {
                self.peripherals.append(peripheral)
            }
            self.tableView.reloadData()
        }).disposed(by: bag)
        
        BLEManager.shared.disconnectedPeripheral.subscribe(onNext: { (peripheral, err) in
            print(peripheral)
            if !self.peripherals.contains(peripheral) {
                self.peripherals.append(peripheral)
            }
            self.tableView.reloadData()
        }).disposed(by: bag)
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }

    // MARK: - Table view data source

    override func tableView(_ tableView: UITableView, numberOfRowsInSection section: Int) -> Int {
        return peripherals.count
    }

    override func tableView(_ tableView: UITableView, cellForRowAt indexPath: IndexPath) -> UITableViewCell {
        let cell = tableView.dequeueReusableCell(withIdentifier: "reuseIdentifier", for: indexPath) as! TableViewCell

        // Configure the cell...
        let peripheral = peripherals[indexPath.row]
        cell.titleLabel.text = peripheral.name
        cell.detailLabel.text = peripheral.identifier.uuidString
        var state: String
        switch peripheral.state {
        case .connected:
            state = "Connected"
        case .disconnected:
            state = "Disconnected"
        case .connecting:
            state = "Connecting"
        case .disconnecting:
            state = "Disconnecting"
        }
        cell.statusLabel.text = state
        return cell
    }

    /*
    // Override to support conditional editing of the table view.
    override func tableView(_ tableView: UITableView, canEditRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the specified item to be editable.
        return true
    }
    */

    /*
    // Override to support editing the table view.
    override func tableView(_ tableView: UITableView, commit editingStyle: UITableViewCellEditingStyle, forRowAt indexPath: IndexPath) {
        if editingStyle == .delete {
            // Delete the row from the data source
            tableView.deleteRows(at: [indexPath], with: .fade)
        } else if editingStyle == .insert {
            // Create a new instance of the appropriate class, insert it into the array, and add a new row to the table view
        }    
    }
    */

    /*
    // Override to support rearranging the table view.
    override func tableView(_ tableView: UITableView, moveRowAt fromIndexPath: IndexPath, to: IndexPath) {

    }
    */

    /*
    // Override to support conditional rearranging of the table view.
    override func tableView(_ tableView: UITableView, canMoveRowAt indexPath: IndexPath) -> Bool {
        // Return false if you do not want the item to be re-orderable.
        return true
    }
    */

    /*
    // MARK: - Navigation

    // In a storyboard-based application, you will often want to do a little preparation before navigation
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        // Get the new view controller using segue.destinationViewController.
        // Pass the selected object to the new view controller.
    }
    */

}
