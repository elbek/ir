package org.ir.tool.benchmark;

import java.io.IOException;

/**
 * Created by ekamolid on 12/9/2016.
 */
public class Test {
    public static void main(String[] args) throws IOException, InterruptedException {
        int bucketSize = 1024; //2^10
        System.out.println(Integer.toBinaryString(bucketSize));
        for (int i = 150; i < 1000_000; i++) {
            int bucket = i >> 10;
            if (bucket != (i / bucketSize)) {
                System.out.println("fail bucket " + i);
            }
            int cell = i & 0x3FF;
            if (cell != (i % bucketSize)) {
                System.out.println("fail cell " + i);
            }
        }
    }
}