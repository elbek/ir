package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.util.UTF8Util;
import org.ir.tool.core.util.StringsReadWrite;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ekamolid on 12/9/2016.
 */
public class TestUTFUtil {
    static int testSize = 50_000_000;
    static List<String> arrayList = new ArrayList<>(testSize);

    public static void main(String[] args) throws IOException, InterruptedException {
        StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/bigfile.txt", 1024 * 1024, true);
        bigTextGenerator.read(testSize, new Worker() {
            @Override
            public void doWork(byte[] bytes, int offset, int count) {
                byte[] b = new byte[count];
                System.arraycopy(bytes, 0, b, 0, count);
                arrayList.add(new String(b));
            }
        });
        bigTextGenerator.close();

        Timer timer = new Timer();
        testBuiltIn(timer);
        System.out.println("2nd one");
        testCustomer(timer);
        timer.print();
    }

    static void testCustomer(Timer timer) throws IOException, InterruptedException {
        byte bytes[] = new byte[20];
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 10000; j++) {
                UTF8Util.stringToUTF8Bytes(arrayList.get(j), 0, 9, bytes, 0);
            }
        }

        timer.start("custom");
        for (String s : arrayList) {
            UTF8Util.stringToUTF8Bytes(s, 0, 9);
        }
        timer.stop();
    }

    static void testBuiltIn(Timer timer) throws IOException, InterruptedException {
        for (int i = 0; i < 1000; i++) {
            for (int j = 0; j < 10000; j++) {
                byte[] bytes = arrayList.get(j).substring(0, 9).getBytes(StandardCharsets.UTF_8);
            }
        }

        timer.start("build-in");
        int i = 0;
        for (String s : arrayList) {
            byte[] bytes = s.substring(0, 9).getBytes(StandardCharsets.UTF_8);
            i += bytes.length;
        }
        System.out.println(i);
        timer.stop();
    }
}