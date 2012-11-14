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

// This class displays an input field in the screen
// and returns the value entered to the MIDlet
class InputScreen extends Form implements CommandListener {

    private final ImageViewerApp midlet;
    private final TextField inputField =
            new TextField("Input", "", 32, TextField.ANY);
    private final Command okCommand =
            new Command("OK", Command.OK, 1);
    private final Command cancelCommand =
            new Command("Cancel", Command.OK, 1);

    InputScreen(ImageViewerApp midlet) {
        super("Input");
        this.midlet = midlet;
        append(inputField);
        addCommand(okCommand);
        addCommand(cancelCommand);
        setCommandListener(this);
    }

    public void setQuestion(String question, String text) {
        inputField.setLabel(question);
        inputField.setString(text);
    }

    public String getInputText() {
        return inputField.getString();
    }

    public void commandAction(Command command, Displayable d) {
        if (command == okCommand) {
            midlet.input(inputField.getString());
        } else if (command == cancelCommand) {
            midlet.cancelInput();
        }
    }
}
