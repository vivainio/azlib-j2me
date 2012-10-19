/*
 * Copyright © 2012 Nokia Corporation. All rights reserved. Nokia and Nokia
 * Connecting People are registered trademarks of Nokia Corporation. Oracle and
 * Java are trademarks or registered trademarks of Oracle and/or its affiliates.
 * Other product and company names mentioned herein may be trademarks or trade
 * names of their respective owners. See LICENSE.TXT for license information.
 */
package com.nokia.example.utils;

import javax.microedition.lcdui.Alert;
import javax.microedition.lcdui.CommandListener;

public class SaveChangesAlert
    extends Alert {

    public SaveChangesAlert(CommandListener listener) {
        super(Compatibility.toLowerCaseIfFT("Save changes"));
        setString("Save changes made?");
        addCommand(Commands.ALERT_SAVE_YES);
        addCommand(Commands.ALERT_SAVE_NO);
        addCommand(Commands.ALERT_SAVE_BACK);
        setTimeout(Alert.FOREVER);
        setCommandListener(listener);
    }
}
