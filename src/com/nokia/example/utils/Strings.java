/*
 * Copyright © 2012 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */
package com.nokia.example.utils;

public class Strings {

    public static final int LISTS = 0;
    public static final int TEXT = 1;
    public static final int DIALOGS = 2;
    public static final int CANVAS = 3;
    public static final int FORM = 4;
    public static final int CATEGORYBAR = 5;
    public static final int MENUS = 6;
    public static final int MENUPOPUPLIST = 7;
    public static final int TICKER = 8;
    public static final int CONFIRMATION = 9;
    public static final int EMPTYCONTENT = 10;
    public static final int ZOOM = 11;
    public static final int MULTIPLEITEMS = 12;
    public static final int ADDNEW = 13;
    public static final int INTERDEPENDENT = 14;
    public static final int KEYPADS = 15;
    private static final String[] TITLES = {
        "Lists",
        "Text",
        "Dialogs",
        "Canvas",
        "Form",
        "Categories",
        "Menus",
        "Menu popup list",
        "Ticker",
        "Confirmation",
        "Empty content",
        "Zoom",
        "Multiple items",
        "Add new",
        "Interdependent",
        "Keypads"
    };

    public static String getTitle(int index) {
        return Compatibility.toLowerCaseIfFT(TITLES[index]);
    }
}
