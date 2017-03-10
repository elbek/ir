package org.ir.tool.core.tree;

import org.ir.tool.core.BaseTest;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ekamolid on 12/8/2016.
 */
public class ArrayHashContainerTest extends BaseTest {
    ArrayHashContainer arrayHashContainer;

    @Before
    public void setUp() throws Exception {
        arrayHashContainer = new ArrayHashContainer(1 << 24, Integer.MAX_VALUE, null);
    }

    @After
    public void tearDown() throws Exception {
        arrayHashContainer.clear();
    }

    @Test
    public void insert() throws Exception {
        for (String s : stringSet) {
            boolean r = arrayHashContainer.insert(s, 0, s.length(), s.getBytes(), null);
            assertTrue(r);
        }
        assertEquals(stringSet.size(), arrayHashContainer.getCount());
    }

    @Test
    public void lookup() throws Exception {
        for (String s : stringSet) {
            arrayHashContainer.insert(s, 0, s.length(), s.getBytes(), null);
        }
        for (String s : stringSet) {
            assertArrayEquals(s.getBytes(), arrayHashContainer.lookup(s, 0, s.length()).toBytes());
        }
    }

    @Test
    public void nullCheck() throws Exception {
        boolean b = arrayHashContainer.insert("", 0, 0, new byte[]{1}, null);
        assertTrue(b);
        b = arrayHashContainer.insert("", 0, 0, new byte[]{1}, null);
        assertFalse(b);
        assertArrayEquals(new byte[]{1}, arrayHashContainer.lookup("", 0, 0).toBytes());
    }

    @Override
    protected int getNumberOfRandomStrings() {
        return 1000000;
    }
}