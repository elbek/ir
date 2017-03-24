package org.ir.tool.core.tree;

import org.ir.tool.core.BaseTest;
import org.ir.tool.core.ByteArraySlice;
import org.ir.tool.core.util.RandomUtil;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by ekamolid on 12/18/2016.
 */
public class BurstTreeTest extends BaseTest {
    private BurstTree burstTree;

    @Before
    public void setUp() throws Exception {
        burstTree = new BurstTree();
    }

    @Test
    public void insert() throws Exception {
        for (String s : stringSet) {
            boolean b = burstTree.insert(s, 0, s.length(), s.getBytes());
            assertTrue(b);
        }
        assertEquals(burstTree.getCount(), stringSet.size());
        burstTree.clear();
    }

    @Test
    public void lookup() throws Exception {
        for (String s : stringSet) {
            boolean b = burstTree.insert(s, 0, s.length(), s.getBytes());
            assertTrue(b);
        }

        for (String s : stringSet) {
            assertArrayEquals(burstTree.lookup(s, 0, s.length()).toBytes(), s.getBytes());
        }
    }

    @Test
    public void clear() throws Exception {
        burstTree.clear();
        burstTree.insert("a", 0, "a".length(), "a".getBytes());
        burstTree.clear();
        assertEquals(burstTree.getCount(), 0);
    }

    @Test
    public void compile() throws Exception {
        for (String s : stringSet) {
            boolean b = burstTree.insert(s, 0, s.length(), s.getBytes());
            assertTrue(b);
        }
        System.out.println(burstTree.compile().getLength());
    }

    @Test
    public void restore() throws Exception {
        for (String s : stringSet) {
            boolean b = burstTree.insert(s, 0, s.length(), s.getBytes());
            assertTrue(b);
        }
        long t = System.currentTimeMillis();
        ByteArraySlice byteArraySlice = burstTree.compile();
        BurstTree tree = BurstTree.restore(byteArraySlice.getData());
        System.out.println(byteArraySlice.getData().length);
        System.out.println(System.currentTimeMillis() - t);
        t = System.currentTimeMillis();
        for (String s : stringSet) {
            tree.lookup(s, 0, s.length());
        }
        System.out.println(System.currentTimeMillis() - t);
    }

    @Override
    protected int getNumberOfRandomStrings() {
        return 20_000_000;
    }

    @Override
    protected RandomUtil.RandomStringEnum getRandomStringEnum() {
        return RandomUtil.RandomStringEnum.NON_ASCII;
    }
}