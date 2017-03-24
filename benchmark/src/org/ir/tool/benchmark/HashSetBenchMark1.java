package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.kv.HashTable;
import org.ir.tool.core.util.StringsReadWrite;
import org.openjdk.jmh.runner.RunnerException;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ekamolid on 12/20/2016.
 */
public class HashSetBenchMark1 {
    static byte[] sm = new byte[]{1};

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, RunnerException {
        MyState myState = new MyState();
        myState.doSetup();
        for (int i = 0; i < 1; i++) {
            testCustomHashTable(myState);
            testSet(myState);
        }

        Timer timer = new Timer();
        timer.start("custom", true);
        testCustomHashTable(myState);
        timer.stop();

        timer.start("testSet", true);
        testSet(myState);
        timer.stop();
        timer.print();
    }

    public static class MyState {
        HashTable hashTable = new HashTable(1 << 25);
        Map<String, byte[]> set = new HashMap<>(30_000_000);

        List<byte[]> strings;
        List<String> strings1;

        public MyState() {
            try {
                StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/file/ASCII_NOT_NUMBER_LOWER.txt", 1024 * 1024, true);
                int testSize = 30_000_000;
                strings = new ArrayList<>(testSize);
                strings1 = new ArrayList<>(testSize);
                bigTextGenerator.read(testSize, new Worker() {
                    @Override
                    public void doWork(byte[] bytes, int offset, int count) {
                        String str = new String(bytes, offset, count, StandardCharsets.UTF_8);
                        strings.add(str.getBytes());
                        strings1.add(str);
                    }
                });
                bigTextGenerator.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        public void doSetup() {
            for (byte[] b : strings) {
                hashTable.add(b.clone(), sm);
                set.put(new String(b), sm);
            }
        }
    }

    public static void testCustomHashTable(MyState myState) throws IOException {
        for (byte[] bytes : myState.strings) {
            myState.hashTable.get(bytes);
        }
    }

    public static void testSet(MyState myState) throws IOException {
        for (String str : myState.strings1) {
            myState.set.get(str);
        }
    }
}