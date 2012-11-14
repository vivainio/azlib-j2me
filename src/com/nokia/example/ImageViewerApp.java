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
import javax.microedition.midlet.*;

// Main class which inits the connection and create the screens
public class ImageViewerApp {

    private final Image logo;
    private final ImageCanvas imageCanvas;
    private FileSelector fileSelector;
    private final InputScreen inputScreen;
    private int operationCode = -1;
	private MIDlet midlet;

	
	private Display getDisplay() {
		return Display.getDisplay(midlet);
	}
    public ImageViewerApp(MIDlet realmidlet) {
    	
    	midlet = realmidlet;
        // init basic parameters
        logo = makeImage("/logo1.png");
        ErrorScreen.init(logo, getDisplay());
        imageCanvas = new ImageCanvas(this);
        fileSelector = new FileSelector(this, realmidlet);
        inputScreen = new InputScreen(this);
    }

    /*
    public void startApp() {
        Displayable current = Display.getDisplay(this).getCurrent();

        if (current == null) {
            // Checks whether the API is available
            boolean isAPIAvailable = System.getProperty(
                    "microedition.io.file.FileConnection.version") != null;
            // shows splash screen
            if (!isAPIAvailable) {
                String text = getAppProperty("MIDlet-Name") + "\n"
                        + getAppProperty("MIDlet-Vendor")
                        + "\nFile Connection API is not available";
                Alert splashScreen = new Alert(null,
                        text,
                        logo,
                        AlertType.INFO);
                Display.getDisplay(this).setCurrent(splashScreen);
            } else {
                try {
                    Display.getDisplay(this).setCurrent(fileSelector);
                    fileSelector.initialize();
                } catch (SecurityException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    showError(e);
                }
            }
        } else {
            Display.getDisplay(this).setCurrent(current);
        }
    }

    public void pauseApp() {
    }

    public void destroyApp(boolean unconditional) {
        // stop the commands queue thread
        fileSelector.stop();
        notifyDestroyed();
    }
    */

    void fileSelectorExit() {
        //destroyApp(false);
    }

    
    void cancelInput() {
        getDisplay().setCurrent(fileSelector);
    }

    void input(String input) {
        fileSelector.inputReceived(input, operationCode);
        getDisplay().setCurrent(fileSelector);
    }

    void displayImage(String imageName) {
        if (imageCanvas.displayImage(imageName) == true) {
            getDisplay().setCurrent(imageCanvas);
        }
    }

    void displayFileBrowser() {
        getDisplay().setCurrent(fileSelector);
    }

    void showError(String errMsg) {
        ErrorScreen.showError(errMsg, fileSelector);
    }

    void showError(Exception e) {
        ErrorScreen.showError(e.getMessage(), fileSelector);
    }

    void showMsg(String text) {
        Alert infoScreen = new Alert(null,
                text,
                logo,
                AlertType.INFO);
        infoScreen.setTimeout(3000);
        getDisplay().setCurrent(infoScreen, fileSelector);
    }

    Image getLogo() {
        return logo;
    }

    void requestInput(String text, String label, int operationCode) {
        inputScreen.setQuestion(text, label);
        this.operationCode = operationCode;
        getDisplay().setCurrent(inputScreen);
    }

    // loads a given image by name
    static Image makeImage(String filename) {
        Image image = null;

        try {
            image = Image.createImage(filename);
        } catch (Exception e) {
            // use a null image instead
        }

        return image;
    }
}
