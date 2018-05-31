package com.mobilenow.cyrcadia_itbra.data.ble;

public class DeviceStatusModel {
    public enum PatchConnectStatus {
        BOTH, LEFT, RIGHT
    }

    public enum SensorsValidation {
        OK, LOSE
    }

    public enum SensorsReadingInRange {
        INRANGE, OVERTHERANGE
    }

    public enum ControlFlag {
        CONFIGURED, CONFIGURING
    }

    public enum StatusByte {
        WARMUP, MEASUREMENTCYCLEINPROGRESS, MEASUREMENTCYCLEABORTED, MEASUREMENTCYCLECOMPLETED
    }

    private PatchConnectStatus patchConnectStatus;
    private SensorsValidation sensorsValidation;
    private SensorsReadingInRange isInRange;
    private ControlFlag isConfigure;
    private StatusByte statusByte;

    public PatchConnectStatus getPatchConnectStatus() {
        return patchConnectStatus;
    }

    public void setPatchConnectStatus(PatchConnectStatus patchConnectStatus) {
        this.patchConnectStatus = patchConnectStatus;
    }

    public SensorsValidation getSensorsValidation() {
        return sensorsValidation;
    }

    public void setSensorsValidation(SensorsValidation sensorsValidation) {
        this.sensorsValidation = sensorsValidation;
    }

    public SensorsReadingInRange getIsInRange() {
        return isInRange;
    }

    public void setIsInRange(SensorsReadingInRange isInRange) {
        this.isInRange = isInRange;
    }

    public ControlFlag getIsConfigure() {
        return isConfigure;
    }

    public void setIsConfigure(ControlFlag isConfigure) {
        this.isConfigure = isConfigure;
    }

    public StatusByte getStatusByte() {
        return statusByte;
    }

    public void setStatusByte(StatusByte statusByte) {
        this.statusByte = statusByte;
    }

    public boolean isDeviceOK() {
        return patchConnectStatus == PatchConnectStatus.BOTH && sensorsValidation ==
                SensorsValidation.OK && isConfigure == ControlFlag.CONFIGURED && isInRange ==
                SensorsReadingInRange.INRANGE;
    }
}
