package com.nokia.example.utils;

import javax.microedition.rms.RecordEnumeration;
import javax.microedition.rms.RecordStore;
import javax.microedition.rms.RecordStoreException;

public class RMSUtils {

    /**
     * Save data to RMS.
     * @param recordStoreName
     * @param data 
     */
    public static void save(String recordStoreName, byte[] data) {
        try {
            RecordStore store = RecordStore.openRecordStore(recordStoreName,
                true);
            if (store.getNumRecords() == 0) {
                store.addRecord(null, 0, 0);
            }
            store.setRecord(getRecordId(store), data, 0, data.length);
            store.closeRecordStore();
        }
        catch (Exception e) {
            try {
                RecordStore.deleteRecordStore(recordStoreName);
            }
            catch (RecordStoreException rse) {
            }
        }
    }

    private static int getRecordId(RecordStore store)
        throws RecordStoreException {
        RecordEnumeration e = store.enumerateRecords(null, null, false);
        try {
            return e.nextRecordId();
        }
        finally {
            e.destroy();
        }
    }

    /**
     * Load data from RMS.
     * @param recordStoreName
     * @return stored data
     */
    public static byte[] load(String recordStoreName) {
        byte[] data = null;
        try {
            RecordStore store = RecordStore.openRecordStore(recordStoreName,
                true);
            if (store.getNumRecords() > 0) {
                data = store.getRecord(getRecordId(store));
            }
        }
        catch (RecordStoreException e) {
        }
        return data;
    }
}
