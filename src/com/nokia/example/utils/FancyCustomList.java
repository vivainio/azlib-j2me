/*
 * Copyright © 2012 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */ 

package com.nokia.example.utils;

import com.nokia.mid.ui.DirectGraphics;
import com.nokia.mid.ui.DirectUtils;
import java.util.Vector;
import javax.microedition.lcdui.Font;
import javax.microedition.lcdui.Graphics;
import javax.microedition.lcdui.Image;
import javax.microedition.lcdui.List;


public class FancyCustomList extends CustomList {

    private final int COLOR_IMPORTANCE_HIGH = 0xd72525;
    private final int COLOR_IMPORTANCE_MEDIUM = 0xd7b325;
    private final int COLOR_IMPORTANCE_LOW = 0x254bd7;
    private final int COLOR_SEPARATOR = 0x777777;
    private Vector fancyElements = new Vector();
    
    public FancyCustomList(String title) {
        super(title, List.IMPLICIT);
    }
    
    public final int append(String titlePart, String contentPart,
            String timePart, Image imagePart, int importanceLevel) {
        fancyElements.addElement(new FancyElement(titlePart, contentPart,
                timePart, imagePart, importanceLevel));
        append(titlePart, imagePart);
        repaint();
        return fancyElements.size() - 1;
    }
    
    public final int appendSeparator() {
        fancyElements.addElement(new FancyElement(FancyElement.SEPARATOR));
        append("", null);
        repaint();
        return fancyElements.size() - 1;
    }
    
    protected int elementIndexAt(int x, int y) {
        int heightSoFar = 0;
        int heightNext = 0;

        // Go through all items (pretend that list item heights can vary).
        for (int i = 0; i < fancyElements.size(); i++) {
            heightSoFar = heightNext;
            FancyElement fancyElement = (FancyElement) fancyElements.elementAt(i);
            heightNext += (fancyElement).getHeight();

            if (y >= heightSoFar && y <= heightNext && !fancyElement.isSeparator()) {
                return i;
            }
        }

        return -1;
    }
    
    protected void drawElements(Graphics g, int yOffset, int width, int height,
            int focusedElementIndex) {
        
        int heightSoFar = 0;
        int heightNext = 0;
        
        for (int i = 0, size = fancyElements.size(); i < size; i++) {
            heightSoFar = heightNext;
            heightNext += ((FancyElement) fancyElements.elementAt(i)).getHeight();
            
            if (heightNext + yOffset < 0) {
                continue;
            }
            else if (heightSoFar + yOffset > height) {
                // Item would be drawn "under" the visible area 
                // -> stop drawing.
                break;
            }
            drawElement(g, i, heightSoFar + yOffset, i == focusedElementIndex);
        }
    }
    
    private void drawElement(final Graphics g, final int index, final int y,
            final boolean focused) {
        final FancyElement fancyElement = (FancyElement) fancyElements.elementAt(index);
        final int elementHeight = fancyElement.getHeight();
        
        // Ensure that element does not overlap outside allocated area.
        g.setClip(0, y, width, elementHeight);
        
        if (focused) {
            g.setColor(theme.backgroundColorFocused);
            g.fillRect(0, y + theme.backgroundMarginTopAndBottom,
                width, elementHeight - 2 * theme.backgroundMarginTopAndBottom);
        }
        
        if (theme.borderType == Theme.BORDER_TOUCH_AND_TYPE) {
            try {
                Class.forName("com.nokia.mid.ui.DirectUtils");
                Class.forName("com.nokia.mid.ui.DirectGraphics");
                DirectGraphics dg = DirectUtils.getDirectGraphics(g);
                int borderWidth = width - theme.scrollBarMarginRight
                    - theme.scrollBarWidth;
                dg.setARGBColor(theme.borderColorDark);
                g.fillRect(0, y + elementHeight - 2, borderWidth, 1);
                dg.setARGBColor(theme.borderColorLight);
                g.fillRect(0, y + elementHeight - 1, borderWidth, 1);
            }
            catch (ClassNotFoundException e) {
            }
        }
        
        int x0 = theme.textOnlyMarginLeft;
        
        if (fancyElement.isSeparator) {
            drawSeparator(g, x0, y, elementHeight);
        }
        else {
            drawFancyElement(fancyElement, g, x0, y, elementHeight, focused);
        }
    }
    
    private void drawSeparator(final Graphics g, int x0, int y0, int elementHeight) {
        g.setColor(COLOR_SEPARATOR);
        int separatorHeight = 4;
        g.fillRect(x0,
                    y0 + elementHeight / 2 - separatorHeight / 2,
                    width - theme.textOnlyMarginLeft * 2,
                    separatorHeight);
    }
    
    private void drawFancyElement(FancyElement fancyElement,
            final Graphics g, int x0, int y,
            int elementHeight, boolean focused) {
        int y0 = y;
        // Draw the image
        Image image = fancyElement.getImagePart();
        if (image != null) {
            x0 = theme.imageMarginLeft;
            g.drawImage(image, x0, y0 + elementHeight / 2,
                Graphics.LEFT | Graphics.VCENTER);
            x0 += image.getWidth() + theme.textMarginLeftAndRight;
        }

        // Draw title string
        g.setColor((focused) ? theme.textColorFocused : theme.textColor);
        g.setFont(fancyElement.getFont());
        final int lineHeight = fancyElement.getFont().getHeight();
        int padding = 0;//lineHeight / 2;
        y0 += lineHeight + padding;
        g.drawString(fancyElement.getTitlePart(),
            x0, y0, Graphics.LEFT | Graphics.BOTTOM);

        // Draw content string
        Font smallFont = fancyElement.getSmallFont();
        g.setFont(smallFont);
        final int smallLineHeight = smallFont.getHeight();
        y0 += elementHeight - smallLineHeight * 1.5 - padding;
        g.drawString(fancyElement.getContentPart(),
            x0, y0, Graphics.LEFT | Graphics.BOTTOM);

        // Draw time
        int timeWidth = smallFont.stringWidth(fancyElement.getTimePart());
        int timeX = width - timeWidth;
        int timeY = y + smallLineHeight + padding;
        g.drawString(fancyElement.getTimePart(),
            timeX, timeY, Graphics.LEFT | Graphics.BOTTOM);

        // Draw importance
        boolean drawImportance = true;
        switch (fancyElement.getImportanceLevel()) {
            case FancyElement.IMPORTANCE_HIGH:
                g.setColor(COLOR_IMPORTANCE_HIGH);
                break;
            case FancyElement.IMPORTANCE_MEDIUM:
                g.setColor(COLOR_IMPORTANCE_MEDIUM);
                break;
            case FancyElement.IMPORTANCE_LOW:
                g.setColor(COLOR_IMPORTANCE_LOW);
                break;
            default:
                // Invalid / no color, skip drawing importance
                drawImportance = false;
        }

        if (drawImportance) {
            int importanceIndicatorSize = smallFont.getHeight();
            g.fillRoundRect(timeX + timeWidth / 2 - importanceIndicatorSize / 2,
                            timeY + importanceIndicatorSize / 4,
                            importanceIndicatorSize,
                            importanceIndicatorSize,
                            importanceIndicatorSize, 
                            importanceIndicatorSize);
        }
    }
    
    protected class FancyElement implements IElement {
        
        public static final int IMPORTANCE_HIGH = 3;
        public static final int IMPORTANCE_MEDIUM = 2;
        public static final int IMPORTANCE_LOW = 1;
        public static final int IMPORTANCE_NONE = 0;
        
        public static final int SEPARATOR = 9;
        
        private String titlePart;
        private String contentPart;
        private String timePart;
        private Image imagePart;
        private int importanceLevel;
        private boolean isSeparator = false;
        
        public FancyElement(String titlePart,
                String contentPart,
                String timePart,
                Image mainImagePart,
                int importanceLevel) {
            this.titlePart = titlePart;
            this.contentPart = contentPart;
            this.timePart = timePart;
            this.imagePart = mainImagePart;
            this.importanceLevel = importanceLevel;
        }
        
        public FancyElement(int type) {
            isSeparator = true;
        }

        public Image getImagePart() {
            return imagePart;
        }
        
        public int getImportanceLevel() {
            return importanceLevel;
        }
        
        public String getStringPart() {
            return titlePart;
        }
        
        public String getTitlePart() {
            return titlePart;
        }
        
        public String getContentPart() {
            return contentPart;
        }
        
        public String getTimePart() {
            return timePart;
        }
        
        public boolean isSeparator() {
            return isSeparator;
        }
        
        public boolean isSelected() {
            return false;
        }

        public void setSelected(boolean selected) { }

        public int getHeight() {
            if (isSeparator) {
                return theme.font.getHeight();
            }
            else {
                return theme.font.getHeight() * 2;
            }
        }
        
        public Font getSmallFont() {
            return Font.getFont(Font.FACE_SYSTEM, Font.STYLE_PLAIN, Font.SIZE_SMALL);
        }
        
        public Font getFont() {
            return theme.font;
        }

        public String getTruncatedText() {
            return titlePart;
        }

        public String getTruncatedText(int textWidth) {
            return titlePart;
        }

        public Vector getWrappedText() {
            return null;
        }

        public void resetCaches() {
            
        }
    }
}
