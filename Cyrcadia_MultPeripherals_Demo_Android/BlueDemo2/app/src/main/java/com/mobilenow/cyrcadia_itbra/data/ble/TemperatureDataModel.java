package com.mobilenow.cyrcadia_itbra.data.ble;


public class TemperatureDataModel {

    public enum PatchConnection {
        BOTH, LEFT, RIGHT
    }

    private String cycleIndex;
    private PatchConnection patchConnection;
    private String timeStamp;
    private byte[] data;

    public String getCycleIndex() {
        return cycleIndex;
    }

    public void setCycleIndex(String cycleIndex) {
        this.cycleIndex = cycleIndex;
    }

    public PatchConnection getPatchConnection() {
        return patchConnection;
    }

    public void setPatchConnection(PatchConnection patchConnection) {
        this.patchConnection = patchConnection;
    }

    public String getTimeStamp() {
        return timeStamp;
    }

    public void setTimeStamp(String timeStamp) {
        this.timeStamp = timeStamp;
    }

    public byte[] getData() {
        return data;
    }

    public void setData(byte[] data) {
        this.data = data;
    }

    public boolean isEnd() {
        if (cycleIndex != null && cycleIndex.equalsIgnoreCase("FFFF")) {
            return true;
        }
        return false;
    }
}
