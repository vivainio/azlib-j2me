package com.nokia.example;
/*
 * Copyright © 2011 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */ 

// Defines the interface for a single operation executed by the commands queue
interface Operation {
    // Implement here the operation to be executed

    void execute();
}
