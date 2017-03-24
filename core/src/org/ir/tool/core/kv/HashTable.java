package org.ir.tool.core.kv;

import org.ir.tool.core.hash.MurmurHash3;
import org.ir.tool.core.util.Util;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class HashTable {
    /**
     * has to be 2s power
     */
    int slotSize;
    Node[] nodes;
    int count = 0;
    int seed;

    public HashTable(int slotSize) {
        this.slotSize = slotSize;
        nodes = new Node[slotSize];
        seed = (int) System.currentTimeMillis();
    }

    private static class Node {
        byte[] key;
        byte[] value;

        public Node(byte[] key, byte[] value) {
            this.key = key;
            this.value = value;
        }

        Node next;
    }

    public byte[] add(byte[] key, byte[] value) {
        int hash = MurmurHash3.murmurhash3_x86_32(key, 0, key.length, seed);
        int p = hash & (slotSize - 1);
        if (nodes[p] == null) {
            count++;
            nodes[p] = new Node(key, value);
            return null;
        } else {
            Node node = nodes[p];
            while (true) {
                if (Util.isEqualBytes(node.key, key)) {
                    return node.value;
                }
                if (node.next == null) {
                    break;
                } else {
                    node = node.next;
                }
            }
            count++;
            node.next = new Node(key, value);
        }
        return null;
    }

    public byte[] get(byte[] key) {
        int hash = MurmurHash3.murmurhash3_x86_32(key, 0, key.length, seed);
        int p = hash & (slotSize - 1);
        if (nodes[p] == null) {
            return null;
        } else {
            Node node = nodes[p];
            while (node != null) {
                if (Util.isEqualBytes(node.key, key)) {
                    return node.value;
                }
                node = node.next;
            }
        }
        return null;
    }
}