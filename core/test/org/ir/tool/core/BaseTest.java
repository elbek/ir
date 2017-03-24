package org.ir.tool.core;

import org.ir.tool.core.util.RandomUtil;
import org.junit.After;
import org.junit.Before;

import java.util.HashSet;
import java.util.Random;
import java.util.Set;

/**
 * Created by ekamolid on 12/18/2016.
 */
public abstract class BaseTest {
    Random rnd = new Random();
    protected Set<String> stringSet = new HashSet<>();

    @Before
    public void s() throws Exception {
        stringSet.clear();
        while (stringSet.size() != getNumberOfRandomStrings()) {
            int n = rnd.nextInt(10);
            stringSet.add(RandomUtil.generateRandomString(n, getRandomStringEnum()));
        }
    }

    @After
    public void t() throws Exception {
        stringSet.clear();
    }

    protected int getNumberOfRandomStrings() {
        return 1000;
    }

    protected RandomUtil.RandomStringEnum getRandomStringEnum() {
        return RandomUtil.RandomStringEnum.ASCII;
    }
}
