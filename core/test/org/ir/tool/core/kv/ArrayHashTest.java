package org.ir.tool.core.kv;

import org.ir.tool.core.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.util.HashSet;
import java.util.Set;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

/**
 * Created by ekamolid on 12/18/2016.
 */
public class ArrayHashTest extends BaseTest {
    ArrayHash arrayHash;

    @Before
    public void setUp() throws Exception {
        arrayHash = new ArrayHash(1 << 21);
    }

    @After
    public void tearDown() throws Exception {
        arrayHash.clear();
    }

    @Test
    public void addBytes() throws Exception {
        for (String s : stringSet) {
            boolean b = arrayHash.addBytes(s.getBytes(), 0, s.getBytes().length, s.getBytes());
            assertTrue(b);
        }
        assertEquals(arrayHash.getCount(), stringSet.size());
    }

    @Test
    public void addBytes1() throws Exception {
        Set<String> stringSet1 = new HashSet<>();
        for (String s : stringSet) {
            if (s.length() > 5 && stringSet1.contains(s.substring(0, s.length() - 1))) {
                stringSet1.add(s);
            }
        }
        for (String s : stringSet1) {
            boolean b = arrayHash.addBytes(s.getBytes(), 0, s.getBytes().length, s.getBytes());
            assertTrue(b);
            b = arrayHash.addBytes(s.getBytes(), 1, s.getBytes().length - 1, new byte[]{100});
            assertTrue(b);
        }
        for (String s : stringSet1) {
            assertArrayEquals(s.getBytes(), arrayHash.lookUp(s.getBytes(), 0, s.getBytes().length).toBytes());
            assertArrayEquals(new byte[]{100}, arrayHash.lookUp(s.getBytes(), 1, s.getBytes().length - 1).toBytes());
        }
    }

    @Test
    public void lookUp() throws Exception {
        for (String s : stringSet) {
            boolean b = arrayHash.addBytes(s.getBytes(), 0, s.getBytes().length, s.getBytes());
            assertTrue(b);
        }

        for (String s : stringSet) {
            assertArrayEquals(s.getBytes(), arrayHash.lookUp(s.getBytes(), 0, s.getBytes().length).toBytes());
        }
    }

    @Test
    public void clear() throws Exception {
        arrayHash.clear();
        arrayHash.addBytes(new byte[]{1}, 0, 1, new byte[]{1});
        assertEquals(arrayHash.getCount(), 1);
        arrayHash.clear();
        assertEquals(arrayHash.getCount(), 0);
    }

    @Override
    protected int getNumberOfRandomStrings() {
        return 1000_000;
    }

    @Test
    public void iteration() throws Exception {

        for (String s : stringSet) {
            arrayHash.addBytes(s.getBytes(), 0, s.getBytes().length, s.getBytes());
        }

        ArrayHash.Iterator iterator = arrayHash.getIterator();
        int ctn = 0;
        while (iterator.next()) {
            ctn++;
            assertArrayEquals(iterator.key.toBytes(), iterator.value.toBytes());
        }
        assertEquals(ctn, arrayHash.getCount());
    }
}