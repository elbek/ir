package org.ir.tool.core.tree;

import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.store.RAMDataReader;

import java.io.IOException;

/**
 * Created by ekamolid on 11/7/2016.
 */
public class BurstTree {
    Container root;

    public BurstTree() {
        root = newContainer(this);
    }

    public ByteArraySlice lookup(String s, int start, int length) {
        return root.lookup(s, start, length);
    }

    public boolean insert(String s, int start, int length, byte[] bytes) {
        return root.insert(s, start, length, bytes, root);
    }

    public Container newContainer(BurstTree burstTree) {
        return new ArrayHashContainer(burstTree);
    }

    public void clear() {
        root = newContainer(this);
    }

    public int getCount() {
        return root.getCount();
    }

    public ByteArraySlice compile() throws IOException {
        assert root != null;
        return root.compile();
    }

    public static BurstTree restore(byte[] data) throws IOException {
        BurstTree burstTree = new BurstTree();
        burstTree.root = new BurstTreeNode();
        RAMDataReader reader = new RAMDataReader(data);
        burstTree.root.restore(reader);
        return burstTree;
    }
}