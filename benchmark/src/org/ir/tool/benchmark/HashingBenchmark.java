package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.hash.MurmurHash3;
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
 * Created by ekamolid on 12/19/2016.
 */
public class HashingBenchmark {

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashingBenchmark.class.getSimpleName())
                .forks(1)
                .warmupIterations(5)
                .measurementIterations(5)
                .shouldDoGC(true)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class MyState {
        List<String> strings;

        @Setup(Level.Iteration)
        public void doSetup() {
            try {

                StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/file/NON_ASCII.txt", 1024 * 1024, true);
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

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            strings.clear();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testStandard(BurstTreeLookUpBenchmark.MyState myState) throws IOException {
        for (String string : myState.strings) {
            int h = 0;
            for (int i = 0; i < string.length(); i++) {
                h = 31 * h ^ string.charAt(i);
            }
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testCustom(BurstTreeLookUpBenchmark.MyState myState) throws IOException {
        for (String string : myState.strings) {
            MurmurHash3.murmurhash3_x86_32(string, 0, string.length(), 0);
        }
    }
}