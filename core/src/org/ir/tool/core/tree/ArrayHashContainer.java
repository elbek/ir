package org.ir.tool.core.tree;

import org.ir.tool.core.BitArray;
import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.store.RAMDataReader;
import org.ir.tool.core.store.RAMDataWriter;
import org.ir.tool.core.util.UTF8Util;

import java.io.IOException;

/**
 * Created by ekamolid on 12/2/2016.
 */
public class ArrayHashContainer implements Container {
    private final static int slotSize = 1024;
    private byte[][] data;
    private int count = 0;
    private int sn;
    private int burstCount = 1024 * 2;
    private BurstTree burstTree;

    public ArrayHashContainer(BurstTree burstTree) {
        this(null, null, burstTree);
    }

    public ArrayHashContainer(Integer slotNumber, Integer burstCount, BurstTree burstTree) {
        if (slotNumber != null) {
            sn = slotNumber;
        } else {
            sn = slotSize;
        }
        if (burstCount != null) {
            this.burstCount = burstCount;
        }

        this.burstTree = burstTree;
        data = new byte[sn][];
    }

    public ArrayHashContainer() {
        this(null, null, null);
    }

    /**
     * supports up to 63 chars for key (which is (2^16-1)/4) 4 is 5 bytes in utf8
     * We should find a way of not supporting beyond 255 bytes for key
     *
     * @param str
     * @param start
     * @param strLen
     * @param payload
     * @param parentTreeNode
     * @return
     */
    @Override
    public boolean insert(String str, int start, int strLen, byte payload[], Container parentTreeNode) {
        byte[] strBytes = UTF8Util.stringToUTF8Bytes(str, start, strLen);
        assert new String(strBytes).equals(str.substring(start, start + strLen));
        int h = hash(str, start, strLen);
        int p = h & (sn - 1);
        byte[] slot = data[p];
        int payLoadLen = payload.length;
        if (slot == null) {
            slot = new byte[strBytes.length + 2 + payLoadLen];
            slot[0] = (byte) strBytes.length;
            slot[1] = (byte) payLoadLen;
            add(slot, strBytes, 2, 0);
            System.arraycopy(payload, 0, slot, strBytes.length + 2, payLoadLen);
            data[p] = slot;
        } else {
            if (contains(strBytes, slot)) {
                return false;
            }
            int len = slot.length;
            byte[] newSlot = new byte[len + strBytes.length + 2 + payLoadLen];
            System.arraycopy(slot, 0, newSlot, 0, len);
            newSlot[len] = (byte) strBytes.length;
            newSlot[len + 1] = (byte) payLoadLen;
            add(newSlot, strBytes, len + 2, 0);
            System.arraycopy(payload, 0, newSlot, len + strBytes.length + 2, payLoadLen);
            data[p] = newSlot;
        }
        count++;
        if (count >= burstCount) {
            if (burstTree == null) {
                burst((BurstTreeNode) parentTreeNode, str.charAt(start - 1));
            } else {
                burst(null, -1);
            }
        }
        return true;
    }

    private void burst(BurstTreeNode burstTreeNode, int data) {
        BurstTreeNode newContainer;
        newContainer = new BurstTreeNode();
        if (burstTreeNode != null) {
            burstTreeNode.set(data, newContainer);
        } else {
            assert burstTree != null;
            burstTree.root = newContainer;
        }
        visit(new Container.ContainerVisitor() {
            @Override
            public void visit(String s, byte[] data) {
                newContainer.insert(s, 0, s.length(), data, null);
            }
        });
    }

    private void add(byte[] slot, byte[] strBytes, int at, int start) {
        int strLen = strBytes.length;
        int i = start;
        while (i < strLen) {
            byte b = strBytes[i];
            slot[i + at] = b;
            i++;
        }
    }

    private ByteArraySlice lookUp(byte[] strBytes, byte[] slot) {
        assert slot != null;
        int i = 0;
        int slotLen = slot.length;
        int byteLen = strBytes.length;
        while (i < slotLen) {
            int ln = slot[i] & 0xFF;
            int payLoadLen = slot[i + 1] & 0xFF;
            if (byteLen != ln) {
                i += (ln + 2 + payLoadLen);
                continue;
            }
            int j = 0;
            int tmpI = i + 2;
            for (; j < ln; j++) {
                if (strBytes[j] != slot[tmpI + j]) {
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

    public void visit(ContainerVisitor visitor) {
        for (int i = 0; i < sn; i++) {
            if (data[i] != null) {
                byte[] slot = data[i];
                int t = 0;
                int slotLen = slot.length;
                while (t < slotLen) {
                    int ln = slot[t] & 0xFF;
                    int payLoadLen = slot[t + 1] & 0xFF;
                    String str = new String(slot, 2 + t, ln);
                    byte[] bytes = new byte[payLoadLen];
                    System.arraycopy(slot, 2 + t + ln, bytes, 0, payLoadLen);
                    visitor.visit(str, bytes);
                    t += 2 + ln + payLoadLen;
                }
            }
        }
    }

    private boolean contains(byte[] strBytes, byte[] slot) {
        int strLen = strBytes.length;
        int i = 0;
        int slotLen = slot.length;
        while (i < slotLen) {
            int ln = slot[i] & 0xFF;
            int payLoadLen = slot[i + 1] & 0xFF;
            if (strLen != ln) {
                i += (ln + 2 + payLoadLen);
                continue;
            }
            int j = 0;
            int tmpI = i + 1;
            for (; j < ln; j++) {
                if (strBytes[j] != slot[tmpI + j]) {
                    break;
                }
            }
            if (j == ln) { //found
                return true;
            }
            i += (ln + 2 + payLoadLen);
        }
        return false;
    }

    public ByteArraySlice lookup(String str, int start, int length) {
        byte[] strBytes = UTF8Util.stringToUTF8Bytes(str, start, length);
        int hash = hash(str, start, length);
        int p = hash & (sn - 1);
        byte[] slot = data[p];
        if (slot != null) {
            return lookUp(strBytes, slot);
        } else {
            return null;
        }
    }

    private int hash(byte[] bytes, int offset, int count) {
        int h = 0;
        int max = count + offset;
        for (int i = offset; i < max; i++) {
            h = 31 * h + bytes[i];
        }
        return h;
    }

    private int hash(String s, int start, int len) {
        int h = 0;
        int max = start + len;
        for (int i = start; i < max; i++) {
            h = 31 * h + s.charAt(i);
        }
        return h;
    }


    @Override
    public ByteArraySlice compile() throws IOException {
        RAMDataWriter dataWriter = new RAMDataWriter();
        dataWriter.writeVInt(data.length);
        BitArray bitArray = new BitArray(slotSize >> 3);
        for (int i = 0; i < data.length; i++) {
            byte[] bytes = data[i];
            if (bytes != null) {
                bitArray.set(i);
            }
        }
        dataWriter.writeBytes(bitArray.toBytes(), 0, bitArray.toBytes().length);
        for (byte[] bytes : data) {
            if (bytes != null) {
                dataWriter.writeVInt(bytes.length);
                dataWriter.writeBytes(bytes, 0, bytes.length);
            }
        }
//        System.out.println("Array hash: " + dataWriter.data().getLength());
        return dataWriter.data();
    }

    @Override
    public void restore(RAMDataReader reader) throws IOException {
        int pos = reader.position();
        int len = reader.readVInt();
        data = new byte[len][];
        BitArray bitArray = BitArray.fromBytes(reader.getData(), reader.position(), slotSize >> 3);
        reader.seek(reader.position() + (slotSize >> 3));
        for (int i = 0; i < slotSize; i++) {
            if (bitArray.get(i)) {
                int rowLen = reader.readVInt();
                assert rowLen > 0;
                byte[] bytes = new byte[rowLen];
                reader.readBytes(bytes, rowLen);
                data[i] = bytes;
            }
        }
//        System.out.println("Array Hash: " + (reader.position() - pos));
    }

    public void clear() {
        data = new byte[sn][];
    }

    public int getCount() {
        return count;
    }
}