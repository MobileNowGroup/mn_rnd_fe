package com.mobilenow.cyrcadia_itbra.ble.command;

import com.mobilenow.cyrcadia_itbra.ble.base.ITBraBleManager;


public abstract class NotificationCommand extends BaseCommand {
    @Override
    public boolean isNotify() {
        return true;
    }

    @Override
    public void sendCommand() {
        ITBraBleManager.getInstance().send(getCommandID() + Command.COMMAND_NOTIFY_REPLY_DATA);
    }
}
