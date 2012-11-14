package com.nokia.example;
/*
 * Copyright © 2011 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */ 
import javax.microedition.lcdui.*;

class ErrorScreen
        extends Alert {

    private static Image image;
    private static Display display;
    private static ErrorScreen instance = null;

    private ErrorScreen() {
        super("Error");
        setType(AlertType.ERROR);
        setTimeout(5000);
        setImage(image);
    }

    static void init(Image img, Display disp) {
        image = img;
        display = disp;
    }

    static void showError(String message, Displayable next) {
        if (instance == null) {
            instance = new ErrorScreen();
        }
        instance.setTitle("Error");
        instance.setString(message);
        display.setCurrent(instance, next);
    }
}
