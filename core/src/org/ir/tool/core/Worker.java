package org.ir.tool.core;

/**
 * Created by ekamolid on 12/5/2016.
 */
public interface Worker {
    void doWork(byte[] bytes, int offset, int count);
}
