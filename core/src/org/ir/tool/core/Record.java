package org.ir.tool.core;

import java.util.ArrayList;
import java.util.List;

/**
 * A record to be indexed
 * Created by ekamolid on 12/23/2016.
 */
public class Record {
    List<Field> fields;

    public enum FieldType {
        STRING,
        INTEGER,
        LONG,
        SHORT,
        BYTE,
        BYTES,
    }

    public Record() {
        fields = new ArrayList<>(5);
    }

    public void addField(String name, String data, Index index, Store store) {
        fields.add(new Field(FieldType.STRING, name, data, index, store));
    }

    public void addField(String name, int data, Index index, Store store) {

    }

    public void addField(String name, long data, Index index, Store store) {

    }

    public void addField(String name, byte data, Index index, Store store) {

    }

    /**
     * store only
     *
     * @param name
     * @param data
     */
    public void addField(String name, byte[] data) {

    }

    class Field {
        FieldType fieldType;
        String name;
        Object object;
        Index index;
        Store store;

        public Field(FieldType fieldType, String name, Object object, Index index, Store store) {
            this.fieldType = fieldType;
            this.name = name;
            this.object = object;
            this.index = index;
            this.store = store;
        }
    }
}
