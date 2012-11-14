package com.nokia.example;
/*
 * Copyright © 2011 Nokia Corporation. All rights reserved.
 * Nokia and Nokia Connecting People are registered trademarks of Nokia Corporation. 
 * Oracle and Java are trademarks or registered trademarks of Oracle and/or its
 * affiliates. Other product and company names mentioned herein may be trademarks
 * or trade names of their respective owners. 
 * See LICENSE.TXT for license information.
 */ 
import java.io.*;
import java.util.*;
import javax.microedition.io.*;
import javax.microedition.io.file.*;
import javax.microedition.lcdui.*;
import javax.microedition.midlet.MIDlet;

// Simple file selector class.
// It naviagtes the file system and shows images currently available
class FileSelector
        extends List
        implements CommandListener, FileSystemListener {

    private final static Image ROOT_IMAGE =
            ImageViewerApp.makeImage("/root_1.png");
    private final static Image FOLDER_IMAGE =
            ImageViewerApp.makeImage("/folder1.png");
    private final static Image FILE_IMAGE =
            ImageViewerApp.makeImage("/file1.png");
    private final OperationsQueue queue = new OperationsQueue();
    private final static String FILE_SEPARATOR =
            (System.getProperty("file.separator") != null) ? System.getProperty("file.separator") : "/";
    private final static String UPPER_DIR = "..";
    private final ImageViewerApp imageViewerMain;
    private final Command openCommand =
            new Command("Open", Command.ITEM, 1);
    private final Command createDirCommand =
            new Command("Create new directory", Command.ITEM, 2);
    private final Command deleteCommand =
            new Command("Delete", Command.ITEM, 3);
    private final Command renameCommand =
            new Command("Rename", Command.ITEM, 4);
    private final Command exitCommand =
            new Command("Exit", Command.EXIT, 1);
    private final static int RENAME_OP = 0;
    private final static int MKDIR_OP = 1;
    private final static int INIT_OP = 2;
    private final static int OPEN_OP = 3;
    private final static int DELETE_OP = 4;
    private Vector rootsList = new Vector();
    // Stores the current root, if null we are showing all the roots
    private FileConnection currentRoot = null;
    private Ticker ticker = new Ticker("Image Viewer");
	private MIDlet midlet;
    
    

    FileSelector(ImageViewerApp midlet, MIDlet mdlet) {
        super("Image Viewer", List.IMPLICIT);
        setTicker(ticker);
        this.imageViewerMain = midlet;
        this.midlet = mdlet;
        
        addCommand(openCommand);
        addCommand(createDirCommand);
        addCommand(deleteCommand);
        addCommand(renameCommand);
        addCommand(exitCommand);
        setSelectCommand(openCommand);
        setCommandListener(this);
    }

    Display getDisplay() {
    	return Display.getDisplay(midlet);
    	
    }
    void initialize() {
        queue.enqueueOperation(new ImageViewerOperations(INIT_OP));
        FileSystemRegistry.addFileSystemListener(FileSelector.this);
    }

    void stop() {
        queue.abort();
        FileSystemRegistry.removeFileSystemListener(this);
    }

    void inputReceived(String input, int code) {
        switch (code) {
            case RENAME_OP:
                queue.enqueueOperation(new ImageViewerOperations(
                        input,
                        RENAME_OP));
                break;
            case MKDIR_OP:
                queue.enqueueOperation(new ImageViewerOperations(
                        input,
                        MKDIR_OP));
                break;
        }
    }

    public void commandAction(Command c, Displayable d) {
        if (c == openCommand) {
            queue.enqueueOperation(new ImageViewerOperations(OPEN_OP));
        } else if (c == renameCommand) {
            queue.enqueueOperation(new ImageViewerOperations(RENAME_OP));
        } else if (c == deleteCommand) {
            queue.enqueueOperation(new ImageViewerOperations(DELETE_OP));
        } else if (c == createDirCommand) {
            queue.enqueueOperation(new ImageViewerOperations(MKDIR_OP));
        } else if (c == exitCommand) {
            imageViewerMain.fileSelectorExit();
        }
    }

    // Listen for changes in the roots
    public void rootChanged(int state, String rootName) {
        queue.enqueueOperation(new ImageViewerOperations(INIT_OP));
    }

    private void displayAllRoots() {
        ticker.setString("Image Viewer - [Roots]");
        deleteAll();
        Enumeration roots = rootsList.elements();
        while (roots.hasMoreElements()) {
            String root = (String) roots.nextElement();
            root = root.replace('/', FILE_SEPARATOR.charAt(0));
            append(root.substring(1), ROOT_IMAGE);
        }
        currentRoot = null;
    }

    private void createNewDir() {
        if (currentRoot == null) {
            imageViewerMain.showMsg("Is not possible to create a new root");
        } else {
            imageViewerMain.requestInput("New dir name", "", MKDIR_OP);
        }
    }

    private void createNewDir(String newDirURL) {
        if (currentRoot != null) {
            try {
                FileConnection newDir =
                        (FileConnection) Connector.open(
                        currentRoot.getURL() + newDirURL,
                        Connector.WRITE);
                newDir.mkdir();
            } catch (IOException e) {
                imageViewerMain.showError(e);
            }
            displayCurrentRoot();
        }
    }

    private void loadRoots() {
        if (!rootsList.isEmpty()) {
            rootsList.removeAllElements();
        }
        try {
            Enumeration roots = FileSystemRegistry.listRoots();
            while (roots.hasMoreElements()) {
                rootsList.addElement("/"
                        + (String) roots.nextElement());
            }
        } catch (Throwable e) {
            imageViewerMain.showMsg(e.getMessage());
        }

    }

    private void deleteCurrent() {
        if (currentRoot == null) {
            imageViewerMain.showMsg("Is not possible to delete a root");
        } else {
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedFile = getString(selectedIndex);
                if (selectedFile.equals(UPPER_DIR)) {
                    imageViewerMain.showMsg("Is not possible to delete an upper dir");
                } else {
                    try {
                        String tmp = selectedFile.replace(FILE_SEPARATOR.charAt(0), '/');
                        FileConnection fileToDelete =
                                (FileConnection) Connector.open(
                                currentRoot.getURL() + tmp,
                                Connector.READ_WRITE);
                        if (!fileToDelete.exists()) {
                            imageViewerMain.showMsg("File " + fileToDelete.getName() + " does not exists");
                        } else {
                            if (getConfirmation("Do you really want to delete " + tmp + "?")) {
                                fileToDelete.delete();
                                imageViewerMain.showMsg(tmp + " deleted.");
                            } else {
                                imageViewerMain.showMsg("Operation cancelled.");
                            }
                        }
                    } catch (IOException e) {
                        imageViewerMain.showError(e);
                    } catch (SecurityException e) {
                        imageViewerMain.showError(e);
                    }
                    displayCurrentRoot();
                }
            }
        }
    }

    private void renameCurrent() {
        if (currentRoot == null) {
            imageViewerMain.showMsg("Is not possible to rename a root");
        } else {
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedFile = getString(selectedIndex);
                if (selectedFile.equals(UPPER_DIR)) {
                    imageViewerMain.showMsg("Is not possible to rename the upper dir");
                } else {
                    imageViewerMain.requestInput("New name", selectedFile, RENAME_OP);
                }
            }
        }
    }

    private void renameCurrent(String newName) {
        if (currentRoot == null) {
            imageViewerMain.showMsg("Is not possible to rename a root");
        } else {
            int selectedIndex = getSelectedIndex();
            if (selectedIndex >= 0) {
                String selectedFile = getString(selectedIndex);
                if (selectedFile.equals(UPPER_DIR)) {
                    imageViewerMain.showMsg("Is not possible to rename the upper dir");
                } else {
                    try {
                        String tmp = selectedFile.replace(FILE_SEPARATOR.charAt(0), '/');
                        FileConnection fileToRename =
                                (FileConnection) Connector.open(
                                currentRoot.getURL() + tmp,
                                Connector.READ_WRITE);
                        if (fileToRename.exists()) {
                            newName = newName.replace(FILE_SEPARATOR.charAt(0), '/');
                            fileToRename.rename(newName);
                        } else {
                            imageViewerMain.showMsg("File " + fileToRename.getName() + " does not exists");
                        }
                    } catch (IOException e) {
                        imageViewerMain.showError(e);
                    } catch (SecurityException e) {
                        imageViewerMain.showError(e);
                    }
                    displayCurrentRoot();
                }
            }
        }
    }

    private void openSelected() {

        int selectedIndex = getSelectedIndex();
        if (selectedIndex >= 0) {
            String selectedFile = getString(selectedIndex);
            if (selectedFile.endsWith(FILE_SEPARATOR)) {
                try {
                    String tmp = selectedFile.replace(FILE_SEPARATOR.charAt(0), '/');
                    if (currentRoot == null) {
                        currentRoot = (FileConnection) Connector.open(
                                "file:///" + tmp, Connector.READ);
                    } else {
                        currentRoot.setFileConnection(tmp);
                    }
                    displayCurrentRoot();
                } catch (IOException e) {
                    imageViewerMain.showError(e);
                } catch (SecurityException e) {
                    imageViewerMain.showError(e);
                }
            } else if (selectedFile.equals(UPPER_DIR)) {
                if (rootsList.contains(currentRoot.getPath() + currentRoot.getName())) {
                    displayAllRoots();
                } else {
                    try {
                        currentRoot.setFileConnection(UPPER_DIR);
                        displayCurrentRoot();
                    } catch (IOException e) {
                        imageViewerMain.showError(e);
                    }
                }
            } else {
                String url = currentRoot.getURL() + selectedFile;
                imageViewerMain.displayImage(url);
            }
        }
    }

    private void displayCurrentRoot() {
        try {
            ticker.setString("Image Viewer - [" + currentRoot.getURL() + "]");
            // open the root
            deleteAll();
            append(UPPER_DIR, FOLDER_IMAGE);
            // list all dirs
            Enumeration listOfDirs = currentRoot.list("*", false);
            while (listOfDirs.hasMoreElements()) {
                String currentDir = (String) listOfDirs.nextElement();
                if (currentDir.endsWith("/")) {
                    String tmp = currentDir.replace('/', FILE_SEPARATOR.charAt(0));
                    append(tmp, FOLDER_IMAGE);                    // always display the platform specific seperator to the user

                }
            }
            // list all png files and dont show hidden files
            Enumeration listOfFiles = currentRoot.list("*.png", false);
            while (listOfFiles.hasMoreElements()) {
                String currentFile = (String) listOfFiles.nextElement();
                if (currentFile.endsWith(FILE_SEPARATOR)) {
                    append(currentFile, FOLDER_IMAGE);
                } else {
                    append(currentFile, FILE_IMAGE);
                }
            }
            listOfFiles = currentRoot.list("*.jpg", false);
            while (listOfFiles.hasMoreElements()) {
                String currentFile = (String) listOfFiles.nextElement();
                if (currentFile.endsWith(FILE_SEPARATOR)) {
                    append(currentFile, FOLDER_IMAGE);
                } else {
                    append(currentFile, FILE_IMAGE);
                }
            }
            listOfFiles = currentRoot.list("*.bmp", false);
            while (listOfFiles.hasMoreElements()) {
                String currentFile = (String) listOfFiles.nextElement();
                if (currentFile.endsWith(FILE_SEPARATOR)) {
                    append(currentFile, FOLDER_IMAGE);
                } else {
                    append(currentFile, FILE_IMAGE);
                }
            }
            //Making the top item visible.
            setSelectedIndex(0, true);
        } catch (IOException e) {
            imageViewerMain.showError(e);
        } catch (SecurityException e) {
            imageViewerMain.showError(e);
        }
    }

    
    boolean getConfirmation(String message) {
        Object lock = new Object();
        Confirm prompt = new Confirm(message, lock, this);
        synchronized (lock) {
            try {
                lock.wait();
            } catch (InterruptedException ex) {
            }
        }
        // set current displayable, when notification arrives
        getDisplay().setCurrent(this);
        return prompt.getResponse();
    }

    private class ImageViewerOperations implements Operation {

        private final String parameter;
        private final int operationCode;

        ImageViewerOperations(int operationCode) {
            this.parameter = null;
            this.operationCode = operationCode;
        }

        ImageViewerOperations(String parameter, int operationCode) {
            this.parameter = parameter;
            this.operationCode = operationCode;
        }

        public void execute() {
            switch (operationCode) {
                case INIT_OP:
                    String initDir = System.getProperty("fileconn.dir.photos");
                    loadRoots();
                    if (initDir != null) {
                        try {
                            currentRoot =
                                    (FileConnection) Connector.open(
                                    initDir,
                                    Connector.READ);
                            displayCurrentRoot();
                        } catch (Exception e) {
                            imageViewerMain.showError(e);
                            displayAllRoots();
                        }
                    } else {
                        displayAllRoots();
                    }
                    break;
                case OPEN_OP:
                    openSelected();
                    break;
                case DELETE_OP:
                    deleteCurrent();
                    break;
                case RENAME_OP:
                    if (parameter != null) {
                        renameCurrent(parameter);
                    } else {
                        renameCurrent();
                    }
                    break;
                case MKDIR_OP:
                    if (parameter != null) {
                        createNewDir(parameter);
                    } else {
                        createNewDir();
                    }
            }
        }
    }

    private class Confirm extends Alert implements CommandListener {

        private Command okCommand = new Command("Yes", Command.OK, 0);
        private Command nokCommand = new Command("No", Command.EXIT, 0);
        private boolean isOkResponse;
        private Object waitLock;

        Confirm(String message, Object lock, Displayable next) {
            super("Image Viewer", message, imageViewerMain.getLogo(), AlertType.CONFIRMATION);
            addCommand(okCommand);
            addCommand(nokCommand);
            setCommandListener(this);
            waitLock = lock;
            Display.getDisplay(midlet).setCurrent(this, next);
        }

        public void commandAction(Command command, Displayable display) {
            if (command == okCommand) {
                isOkResponse = true;
            } else if (command == nokCommand) {
                isOkResponse = false;
            }
            synchronized (waitLock) {
                waitLock.notifyAll();
            }
        }

        boolean getResponse() {
            return isOkResponse;
        }
    }
}
