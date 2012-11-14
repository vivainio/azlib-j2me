package com.nokia.example;
/*
 * Copyright � 2011 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */ 
import java.io.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;

// This class displays a selected image centered in the screen
class ImageCanvas
        extends Canvas implements CommandListener {

    private final ImageViewerApp midlet;
    private Image currentImage = null;
    int imageWidth;
    int imageHeight;
    private Command backCommand = new Command("Back", Command.BACK, 0);
    private Ticker ticker = new Ticker("Image Viewer");
    private int mouseDownX;
    private int mouseDownY;
    private int deltaX;
    private int deltaY;
    private int posX;
    private int posY;

    ImageCanvas(ImageViewerApp midlet) {
        setTitle("Image Viewer");
        this.midlet = midlet;
        this.addCommand(backCommand);
        this.setCommandListener(this);
        this.setTicker(ticker);
    }

    public boolean displayImage(String imgName) {
        try {
            FileConnection fileConn =
                    (FileConnection) Connector.open(imgName, Connector.READ);

            InputStream fis = fileConn.openInputStream();

            currentImage = Image.createImage(fis);
            fis.close();
            fileConn.close();
            ticker.setString("Image Viewer:" + imgName);
            
            imageWidth = currentImage.getWidth();
            imageHeight = currentImage.getHeight();
            repaint();
        } catch (IOException e) {
            midlet.showError(e);
            return false;
        } catch (Exception e) {
            midlet.showError(e);
            return false;
        } catch (Error e) {
            if (e instanceof OutOfMemoryError) {
                midlet.showError("File is too large to display");
            } else {
                midlet.showError("Failed to display this file. " + e.getMessage());
            }
            return false;
        }
        return true;
    }

    protected void paint(Graphics g) {
        int w = getWidth();
        int h = getHeight();

        // Set background color to black
        g.setColor(0x00000000);
        g.fillRect(0, 0, w, h);

        setImagePlacementPoint();

        if (currentImage != null) {
            g.drawImage(currentImage,
                    posX,
                    posY,
                    Graphics.HCENTER | Graphics.VCENTER);
        } else {
            // If no image is available display a message
            g.setColor(0x00FFFFFF);
            g.drawString("No image",
                    posX,
                    posY,
                    Graphics.HCENTER | Graphics.BASELINE);
        }
    }

    protected void keyReleased(int keyCode) {
        // Exit with any key
        midlet.displayFileBrowser();
    }

    public void commandAction(Command command, Displayable displayable) {
        if (command == backCommand) {
            currentImage = null;
            midlet.displayFileBrowser();
        }
    }

    protected void pointerPressed(int x, int y) {
        mouseDownX = x;
        mouseDownY = y;
    }

    protected void pointerReleased(int x, int y) {
        deltaX = 0;
        deltaY = 0;
    }

    protected void pointerDragged(int x, int y) {
        deltaX = x - mouseDownX;
        deltaY = y - mouseDownY;
        mouseDownX = x;
        mouseDownY = y;
        repaint();
    }

    void setImagePlacementPoint() {

        // This needs to be taken each time, since the values will be chaged when
        // user tilt the phone to potrait mode to landscape mode.
        int canvasWidth = getWidth();
        int canvasHeight = getHeight();

        if (imageWidth > canvasWidth && deltaX != 0) {
            posX += deltaX;
            if (posX < (canvasWidth - imageWidth / 2)) {
                posX = canvasWidth - imageWidth / 2;
            } else if (posX > (imageWidth / 2)) {
                posX = (imageWidth / 2);
            }
        } else {
            posX = canvasWidth / 2;
        }

        if (imageHeight > canvasHeight && deltaY != 0) {
            posY += deltaY;
            if (posY < (canvasHeight - imageHeight / 2)) {
                posY = canvasHeight - imageHeight / 2;
            } else if (posY > (imageHeight / 2)) {
                posY = (imageHeight / 2);
            }
        } else {
            posY = canvasHeight / 2;
        }
    }
}
