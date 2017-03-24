package org.ir.tool.core.kv;

import org.ir.tool.core.ByteArraySlice;

/**
 * Created by ekamolid on 12/2/2016.
 */
public class ArrayHash {
    private byte[][] data;
    private int slots = 1 << 15;
    private int count;

    public ArrayHash(Integer slotNumber) {
        if (slotNumber != null) {
            slots = slotNumber;
        }
        data = new byte[slots][];
    }

    public boolean addBytes(byte[] key, int offset, int len, byte value[]) {
        int h = hash(key, offset, len);
        int p = h & (slots - 1);
        byte[] slot = data[p];
        byte valueLen = (byte) value.length;
        if (slot == null) {
            slot = new byte[len + 2 + valueLen];
            slot[0] = (byte) len;
            slot[1] = valueLen;
            addBytes(slot, key, 2, offset, len);
            System.arraycopy(value, 0, slot, len + 2, valueLen);
            data[p] = slot;
        } else {
            if (contains(key, slot, offset, len)) {
                return false;
            }
            int length = slot.length;
            int req = length + len + 2 + valueLen;
            byte[] newSlot = new byte[req];
            System.arraycopy(slot, 0, newSlot, 0, length);
            newSlot[length] = (byte) len;
            newSlot[length + 1] = valueLen;
            addBytes(newSlot, key, length + 2, offset, len);
            System.arraycopy(value, 0, newSlot, length + len + 2, valueLen);
            data[p] = newSlot;
        }
        count++;
        return true;
    }

    private void addBytes(byte[] slot, byte[] bytes, int at, int offset, int len) {
        int i = 0;
        int max = len + offset;
        while (i < max) {
            byte b = bytes[i + offset];
            slot[i + at] = b;
            i++;
        }
    }

    private ByteArraySlice lookUp(byte[] key, byte[] slot, int offset, int len) {
        int i = 0;
        int slotLen = slot.length;
        while (i < slotLen) {
            int ln = slot[i] & 0xFF;
            int payLoadLen = slot[i + 1] & 0xFF;
            if (len != ln) {
                i += (ln + 2 + payLoadLen);
                continue;
            }
            int j = 0;
            int tmpI = i + 2;
            for (; j < ln; j++) {
                if (key[j + offset] != slot[tmpI + j]) {
                    break;
                }
            }
            if (j == ln) { //found
                return new ByteArraySlice(slot, tmpI + j, payLoadLen);
            }
            i += (ln + 2 + payLoadLen);
        }
        return null;
    }

    private boolean contains(byte[] key, byte[] slot, int offset, int len) {
        int i = 0;
        int slotLen = slot.length;
        while (i < slotLen) {
            int ln = slot[i] & 0xFF;
            int valueLen = slot[i + 1] & 0xFF;
            if (len != ln) {
                i += (ln + 2 + valueLen);
                continue;
            }
            int j = 0;
            int tmpI = i + 2;
            for (; j < ln; j++) {
                if (key[j + offset] != slot[tmpI + j]) {
                    break;
                }
            }
            if (j == ln) { //found
                return true;
            }
            i += (ln + 2 + valueLen);
        }
        return false;

    }

    private void intoToBytes(int value, byte[] bytes, int start) { //todo, improve
        bytes[start++] = (byte) (value >>> 24);
        bytes[start++] = (byte) (value >>> 16);
        bytes[start++] = (byte) (value >>> 8);
        bytes[start] = (byte) value;
    }

    private int bytesToInt(byte[] bytes, int start) {
        int i = bytes[start] << 24;
        i |= bytes[start + 1] << 16;
        i |= bytes[start + 2] << 8;
        i |= bytes[start + 3];
        return i;
    }

    public ByteArraySlice lookUp(byte[] bytes, int offset, int len) {
        int h = hash(bytes, offset, len);
        int p = h & (slots - 1);
        byte[] slot = data[p];
        if (slot != null) {
            return lookUp(bytes, slot, offset, len);
        } else {
            return null;
        }
    }

    public void clear() {
        count = 0;
        data = new byte[slots][];
    }

    private int hash(byte[] bytes, int offset, int len) {
        int h = 0;
        int max = len + offset;
        for (int i = offset; i < max; i++) {
            h = 31 * h + bytes[i];
        }
        return h;
//        return MurmurHash3.murmurhash3_x86_32(bytes, offset, count, 0);
    }

    public int getCount() {
        return count;
    }

    public Iterator getIterator() {
        return new Iterator();
    }

    public class Iterator {
        private int slt = 0;
        private int current = 0;
        public ByteArraySlice key = new ByteArraySlice(null, 0, 0);
        public ByteArraySlice value = new ByteArraySlice(null, 0, 0);
        ArrayHash arrayHash = ArrayHash.this;

        public boolean next() {
            if (slt >= arrayHash.slots) {
                return false;
            }

            while (arrayHash.data[slt] == null) {
                slt++;
                if (slt >= arrayHash.slots) {
                    return false;
                }
            }
            byte[] slot = arrayHash.data[slt];
            int len = slot[current] & 0xFF;
            key.reset(slot, current + 2, len);
            int valueLen = slot[current + 1] & 0xFF;
            value.reset(slot, current + 2 + len, valueLen);
            current += 2 + len + valueLen;
            if (current == slot.length) {
                slt++;
                current = 0;
            }
            return true;
        }
    }
}