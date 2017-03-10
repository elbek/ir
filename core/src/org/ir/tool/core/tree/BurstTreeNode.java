package org.ir.tool.core.tree;

import org.ir.tool.core.BitArray;
import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.store.RAMDataReader;
import org.ir.tool.core.store.RAMDataWriter;

import java.io.IOException;

/**
 * Created by ekamolid on 11/7/2016.
 */
public class BurstTreeNode implements Container {
    Container[][] array;
    private static final int size = 1024;

    public BurstTreeNode() {
        array = new Container[1][];
    }

    @Override
    public ByteArraySlice lookup(String s, int start, int length) {
        if (length == 0) {
            return lookUpNull();
        }
        int r = s.charAt(start) >> 10; //size = 2^10
        int c = s.charAt(start) & 0x3FF; //size = 2^10 - 1
        if (array[r] == null || array[r][c] == null) {
            return null;
        }
        return array[r][c].lookup(s, start + 1, length - 1);
    }

    @Override
    public boolean insert(String s, int start, int length, byte[] bytes, Container parentTreeNode) {
        if (length == 0) {
            return insertNull(bytes);
        }
        int data = s.charAt(start);
        int slotN = data >> 10;
        int pN = data & 0x3FF;
        Container container = null;
        if (slotN < array.length && array[slotN] != null && array[slotN][pN] != null) {
            container = array[slotN][pN];
        }
        if (container == null) { //not found
            int len;
            if (slotN >= array.length) {
                len = array.length * array.length >> 1;
                if (len <= slotN) {
                    len = slotN + 2;
                }
                Container[][] newArray = new Container[len][];
                System.arraycopy(array, 0, newArray, 0, array.length);
                array = newArray;
            }

            if (array[slotN] == null) {
                array[slotN] = new Container[size];
            }
            Container[] nodeRefs = array[slotN];
            container = nodeRefs[pN] = newContainer();
        }
        return container.insert(s, start + 1, length - 1, bytes, this);
    }

    public Container newContainer() {
        return new ArrayHashContainer();
    }

    public void set(int data, Container container) {
        int slotN = data >> 10;
        assert slotN < array.length;
        int pN = data & 0x3FF;
        assert array[slotN] != null;
        array[slotN][pN] = container;
    }

    public boolean insertNull(byte[] bytes) {
        if (array[0] != null && array[0][0] != null) {
            return false;
        }
        if (array[0] == null) {
            array[0] = new Container[size];
        }

        if (array[0][0] == null) {
            array[0][0] = newContainer();
        }
        return array[0][0].insert("", 0, 0, bytes, this);
    }

    public ByteArraySlice lookUpNull() {
        if (array[0] == null || array[0][0] == null) {
            return null;
        }
        return array[0][0].lookup("", 0, 0);
    }

    public int getCount() {
        int count = 0;
        for (int i = 0; i < array.length; i++) {
            Container[] containers = array[i];
            if (containers != null) {
                for (int j = 0; j < containers.length; j++) {
                    Container container = containers[j];
                    if (container != null) {
                        count += container.getCount();
                    }
                }
            }
        }
        return count;
    }

    @Override
    public ByteArraySlice compile() throws IOException {
        RAMDataWriter dataWriter = new RAMDataWriter();
        dataWriter.writeVInt(array.length);
        BitArray bitArray = new BitArray((array.length * size) >> 3);
        for (int i = 0; i < array.length * size; i++) {
            int r = i >> 10;
            int c = i & 0x3FF;
            if (array[r] != null && array[r][c] != null) {
                bitArray.set(i);
            }
        }
        byte[] bytes = bitArray.toBytes();
        dataWriter.writeBytes(bytes, 0, bitArray.toBytes().length);
        for (Container[] containers : array) {
            if (containers != null) {
                for (Container container : containers) {
                    if (container != null) {
                        if (container instanceof BurstTreeNode) {
                            dataWriter.writeByte((byte) 0);
                        } else {
                            dataWriter.writeByte((byte) 1);
                        }
                        ByteArraySlice compiled = container.compile();
                        dataWriter.writeBytes(compiled.getData(), compiled.getOffset(), compiled.getLength());
                    }
                }
            }
        }
//        System.out.println("Burst tree: " + dataWriter.data().getLength());
        return dataWriter.data();
    }

    @Override
    public void restore(RAMDataReader reader) throws IOException {
        int pos = reader.position();
        int len = reader.readVInt();
        array = new Container[len][];
        BitArray bitArray = BitArray.fromBytes(reader.getData(), reader.position(), (len * size) >> 3);
        reader.seek(reader.position() + ((len * size) >> 3));
        for (int i = 0; i < len * size; i++) {
            if (bitArray.get(i)) {
                byte b = reader.readByte();
                Container container;
                if (b == 0) {
                    container = new BurstTreeNode();
                } else {
                    assert b == 1;
                    container = new ArrayHashContainer();
                }
                container.restore(reader);
                int slotN = i >> 10;
                int pN = i & 0x3FF;
                if (array[slotN] == null) {
                    array[slotN] = new Container[size];
                }
                array[slotN][pN] = container;
            }
        }
//        System.out.println("Burst tree node: " + (reader.position() - pos));
    }
}