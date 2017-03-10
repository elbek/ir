package org.ir.tool.core.tree;

import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.store.RAMDataReader;

import java.io.IOException;

/**
 * Created by ekamolid on 11/7/2016.
 */
public interface Container {

    ByteArraySlice lookup(String s, int start, int length);

    boolean insert(String s, int start, int length, byte[] bytes, Container parentTreeNode);

    int getCount();

    interface ContainerVisitor {
        void visit(String s, byte[] data);
    }

    ByteArraySlice compile() throws IOException;

    /**
     * @param reader
     * @return
     * @throws IOException
     */
    void restore(RAMDataReader reader) throws IOException;
}