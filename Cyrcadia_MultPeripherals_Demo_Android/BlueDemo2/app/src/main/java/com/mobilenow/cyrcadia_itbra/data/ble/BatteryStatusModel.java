package com.mobilenow.cyrcadia_itbra.data.ble;

public class BatteryStatusModel {
    public enum BatteryapacityResult {
        OVERTBD, BELOWTBD
    }

    private BatteryapacityResult batteryapacityResult;

    public BatteryapacityResult getBatteryapacityResult() {
        return batteryapacityResult;
    }

    public void setBatteryapacityResult(BatteryapacityResult batteryapacityResult) {
        this.batteryapacityResult = batteryapacityResult;
    }

    public boolean isBatteryOk() {
        return batteryapacityResult == BatteryapacityResult.OVERTBD;
    }
}
