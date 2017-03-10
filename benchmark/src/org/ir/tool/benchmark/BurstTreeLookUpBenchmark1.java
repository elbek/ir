package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.tree.ArrayHashContainer;
import org.ir.tool.core.tree.BurstTree;
import org.ir.tool.core.tree.Container;
import org.ir.tool.core.util.StringsReadWrite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekamolid on 12/6/2016.
 */
public class BurstTreeLookUpBenchmark1 {

    static byte[] sm = new byte[]{1};

    public static void main(String[] args) throws IOException, InterruptedException {
        doSetup();
        System.gc();
        System.gc();
        System.gc();
        System.out.println("sleeping for 2 seconds");
        Timer timer = new Timer();
        Thread.sleep(2000);
        timer.start("look up");
        testRandom();
        timer.stop();
        timer.print();
    }


    static List<String> strings;

    static BurstTree burstTreeR = new BurstTree() {
        @Override
        public Container newContainer(BurstTree burstTree) {
            return new ArrayHashContainer(burstTree);
        }
    };

    public static void doSetup() {
        try {

            StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/file/ASCII_NOT_NUMBER_LOWER.txt", 1024 * 1024, true);
            int testSize = 20_000_000;
            strings = new ArrayList<>(testSize);

            bigTextGenerator.read(testSize, new Worker() {
                @Override
                public void doWork(byte[] bytes, int offset, int count) {
                    String str = new String(bytes, offset, count, StandardCharsets.UTF_8);
                    strings.add(str);
                }
            });
            bigTextGenerator.close();
            for (String string : strings) {
                burstTreeR.insert(string, 0, string.length(), sm);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void doTearDown() {
        burstTreeR.clear();
    }


    public static void testRandom() throws IOException {
        for (String string : strings) {
            burstTreeR.lookup(string, 0, string.length());
        }
    }
}