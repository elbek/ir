package org.ir.tool.core.kv;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

/**
 * Created by ekamolid on 12/23/2016.
 */
public class HashTableTest {
    HashTable hashTable;

    @Before
    public void setUp() throws Exception {
        hashTable = new HashTable(1 << 5);
    }

    @After
    public void tearDown() throws Exception {
        hashTable = new HashTable(1 << 5);
    }


    @Test
    public void add() throws Exception {
        byte[] r = hashTable.add("a".getBytes(), "a".getBytes());
        Assert.assertNull(r);
        r = hashTable.add("b".getBytes(), "b".getBytes());
        Assert.assertNull(r);
        r = hashTable.add("c".getBytes(), "c".getBytes());
        Assert.assertNull(r);

        r = hashTable.add("a".getBytes(), "a".getBytes());
        Assert.assertNotNull(r);
    }

    @Test
    public void get() throws Exception {
        hashTable.add("a".getBytes(), "a".getBytes());
        hashTable.add("b".getBytes(), "b".getBytes());

        Assert.assertArrayEquals(hashTable.get("b".getBytes()), "b".getBytes());
        Assert.assertArrayEquals(hashTable.get("a".getBytes()), "a".getBytes());
        Assert.assertNull(hashTable.get("c".getBytes()));
    }

}