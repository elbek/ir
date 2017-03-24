package org.ir.tool.benchmark;

import org.ir.tool.core.Worker;
import org.ir.tool.core.kv.ArrayHash;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * Created by ekamolid on 12/20/2016.
 */
public class HashSetBenchMark {
    static byte[] sm = new byte[]{1};

    public static void main(String[] args) throws URISyntaxException, IOException, InterruptedException, RunnerException {
        Options opt = new OptionsBuilder()
                .include(HashSetBenchMark.class.getSimpleName())
                .forks(1)
                .shouldDoGC(true)
                .warmupIterations(5)
                .measurementIterations(5)
                .build();
        new Runner(opt).run();
    }

    @State(Scope.Benchmark)
    public static class MyState {
        ArrayHash arrayHash = new ArrayHash(1 << 25);
        Map<String, byte[]> set = new HashMap<>(30_000_000);

        List<byte[]> strings;
        List<String> strings1;

        public MyState() {
            try {
                StringsReadWrite bigTextGenerator = new StringsReadWrite("C:/tmp/file/NON_ASCII.txt", 1024 * 1024, true);
                int testSize = 20_000_000;
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

        @Setup(Level.Iteration)
        public void doSetup() {
            for (byte[] b : strings) {
                arrayHash.addBytes(b, 0, b.length, sm);
                set.put(new String(b), sm);
            }
        }

        @TearDown(Level.Iteration)
        public void doTearDown() {
            arrayHash.clear();
            set.clear();
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testArray(MyState myState) throws IOException {
        for (byte[] bytes : myState.strings) {
            myState.arrayHash.lookUp(bytes, 0, bytes.length);
        }
    }

    @Benchmark
    @BenchmarkMode(Mode.SampleTime)
    @OutputTimeUnit(TimeUnit.MILLISECONDS)
    public void testSet(MyState myState) throws IOException {
        for (String str : myState.strings1) {
            myState.set.get(str);
        }
    }
}