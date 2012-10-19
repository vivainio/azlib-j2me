/*
 * Copyright © 2012 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation.
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners.
 * See LICENSE.TXT for license information.
 */
package com.nokia.example.utils;

import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;

/**
 * A simple button class for custom button elements
 */
public class Button {

    public interface Listener {

        void clicked(Button button);
    }
    private Vector listeners = new Vector();
    private final int RADIUS = 5;
    private String label;
    private int x;
    private int y;
    private int width;
    private int height;
    private boolean toggleable = false;
    private boolean pressed = false;
    private boolean focused = false;
    private int foregroundColor = 0x0000FF;
    private int backgroundColor = 0x000050;

    public Button(String label) {
        this.label = label;
    }

    public Button(String label, int x, int y, int width, int height) {
        this.label = label;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public void setToggleable(boolean toggleable) {
        this.toggleable = toggleable;
    }

    public void setPressed(boolean pressed) {
        this.pressed = pressed;
    }

    public boolean isPressed() {
        return pressed;
    }

    public void setFocused(boolean focused) {
        this.focused = focused;
    }

    public boolean isFocused() {
        return focused;
    }
    
    public void setForegroundColor(int color) {
        foregroundColor = color;
    }

    public void setBackgroundColor(int color) {
        backgroundColor = color;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getX() {
        return this.x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public int getY() {
        return this.y;
    }

    public void setWidth(int width) {
        this.width = width;
    }

    public int getWidth() {
        return this.width;
    }

    public void setHeight(int height) {
        this.height = height;
    }

    public int getHeight() {
        return this.height;
    }

    public void touchDown(int x, int y) {
        if (contains(x, y)) {
            if (toggleable) {
                pressed = true;
            }
            notifyClicked();
        }
    }

    public void addListener(Listener listener) {
        if (!listeners.contains(this)) {
            listeners.addElement(listener);
        }
    }

    public void notifyClicked() {
        int length = listeners.size();
        for (int i = 0; i < length; i++) {
            ((Listener) listeners.elementAt(i)).clicked(this);
        }
    }

    public void render(Graphics g) {
        int oldColor = g.getColor();
        g.translate(x, y);

        if (toggleable) {
            if (pressed) {
                g.setColor(backgroundColor);
            }
            else {
                g.setColor(foregroundColor);
            }
        }
        else {
            g.setColor(backgroundColor);
        }

        g.fillRoundRect(0, 0, width, height, RADIUS, RADIUS);

        if (toggleable && !pressed) {
            g.setColor(backgroundColor);
        }
        else {
            g.setColor(foregroundColor);
        }
        if(focused) {
            g.setStrokeStyle(Graphics.SOLID);
            g.drawRoundRect(0, 0, width, height, RADIUS, RADIUS);
        }
        Font font = g.getFont();
        int fontHeight = font.getHeight();
        g.drawString(label, width / 2, fontHeight / 2, Graphics.TOP
            | Graphics.HCENTER);
        g.translate(-x, -y);
        g.setColor(oldColor);
    }

    public boolean contains(int hitX, int hitY) {
        return (hitX >= x && hitX <= x + width && hitY >= y && hitY <= y
            + height);
    }
}
