package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.tree.ArrayHashContainer;
import org.ir.tool.core.tree.BurstTree;
import org.ir.tool.core.tree.Container;
import org.ir.tool.core.util.StringsReadWrite;
import org.openjdk.jmh.annotations.*;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Created by ekamolid on 12/6/2016.
 */
public class BurstTreeBenchmark {

    static byte[] sm = new byte[]{1};

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(BurstTreeBenchmark.class.getSimpleName())
                .forks(1)
                .shouldDoGC(true)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class MyState {
        List<String> strings;

        BurstTree burstTreeR = new BurstTree() {
            @Override
            public Container newContainer(BurstTree burstTree) {
                return new ArrayHashContainer(burstTree);
            }
        };

        @Setup(Level.Iteration)
        public void doSetup() {
            try {

                StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/file/ASCII_NOT_NUMBER_LOWER.txt", 1024 * 1024, true);
                int testSize = 30_000_000;
                strings = new ArrayList<>(testSize);

                bigTextGenerator.read(testSize, new Worker() {
                    @Override
                    public void doWork(byte[] bytes, int offset, int count) {
                        String str = new String(bytes, offset, count, StandardCharsets.UTF_8);
                        strings.add(str);
                    }
                });
                bigTextGenerator.close();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            strings.clear();
            burstTreeR = new BurstTree() {
                @Override
                public Container newContainer(BurstTree burstTree) {
                    return new ArrayHashContainer(burstTree);
                }
            };
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testRandom(MyState myState) throws IOException {
        for (String string : myState.strings) {
            myState.burstTreeR.insert(string, 0, string.length(), sm);
        }
    }
}