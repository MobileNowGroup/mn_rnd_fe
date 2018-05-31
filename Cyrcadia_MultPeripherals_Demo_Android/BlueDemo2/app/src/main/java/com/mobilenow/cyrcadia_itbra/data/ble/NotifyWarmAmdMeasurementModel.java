package com.mobilenow.cyrcadia_itbra.data.ble;


public class NotifyWarmAmdMeasurementModel {
    private String sensorsValidation;
    private String sensorsReadingInRange;
    private DeviceStatusModel.StatusByte statusByte;

    public String getSensorsValidation() {
        return sensorsValidation;
    }

    public void setSensorsValidation(String sensorsValidation) {
        this.sensorsValidation = sensorsValidation;
    }

    public String getSensorsReadingInRange() {
        return sensorsReadingInRange;
    }

    public void setSensorsReadingInRange(String sensorsReadingInRange) {
        this.sensorsReadingInRange = sensorsReadingInRange;
    }

    public DeviceStatusModel.StatusByte getStatusByte() {
        return statusByte;
    }

    public void setStatusByte(DeviceStatusModel.StatusByte statusByte) {
        this.statusByte = statusByte;
    }
}
