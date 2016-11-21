package org.graylog.benchmarks.pipeline;

import com.google.common.collect.ImmutableMap;

import com.floreysoft.jmte.Engine;
import com.floreysoft.jmte.template.Template;

import org.apache.commons.lang3.text.StrSubstitutor;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.util.Locale;
import java.util.Map;

/**
 * Benchmarks the concatenation of multiple constant strings and some (low) number of objects which need to be converted to strings.
 * Essentially a format string like: "Some wonderful {} has {} fields, and it contains {} different values." with three parameters, "message", 11L, 4.5d
 */
public class StringFormat {

    @State(Scope.Benchmark)
    public static class BState {
        public final Object objects[] = {
                "message", 11L, 4.5d
        };
        public final String constantStringParts[] = {
                "Some wonderful ", " has ", " fields, and it contains ", " different values."
        };

        public final Map<String, Object> namedArgs = ImmutableMap.<String, Object>builder()
                .put("string", "message")
                .put("long", 11L)
                .put("double", 4.5d)
                .build();

        public final Engine compilingEngine = Engine.createCompilingEngine();

        public final Template jmteTemplate = compilingEngine.getTemplate("Some wonderful ${string} has ${long} fields, and it contains ${double} different values.");

    }

    @Benchmark
    public void plusOperator(Blackhole bh, BState state) {
        bh.consume(
                state.constantStringParts[0] + String.valueOf(state.objects[0]) +
                state.constantStringParts[1] + String.valueOf(state.objects[1]) +
                state.constantStringParts[2] + String.valueOf(state.objects[2]) +
                        state.constantStringParts[3]
        );
    }

    @Benchmark
    public void stringBuilder(Blackhole bh, BState state) {
        final StringBuilder sb = new StringBuilder(state.constantStringParts[0]);
        sb.append(state.objects[0]);
        sb.append(state.constantStringParts[1]);
        sb.append(state.objects[1]);
        sb.append(state.constantStringParts[2]);
        sb.append(state.objects[2]);
        sb.append(state.constantStringParts[3]);
        bh.consume(sb.toString());
    }

    @Benchmark
    public void stringFormat(Blackhole bh, BState state) {
        bh.consume(
                String.format("Some wonderful %s has %d fields, and it contains %f different values.",state.objects)
        );
    }

    @Benchmark
    public void strSubstitutor(Blackhole bh, BState state) {
        bh.consume(StrSubstitutor.replace(
                "Some wonderful ${string} has ${long} fields, and it contains ${double} different values.",
                state.namedArgs
        ));
    }

    @Benchmark
    public void jmte(Blackhole bh, BState state) {

        bh.consume(
                state.jmteTemplate.transform(state.namedArgs, Locale.ENGLISH)
        );
    }

    public static void main(String[] args) throws RunnerException {
        Options opt = new OptionsBuilder()
                .include(StringFormat.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(5)
                .threads(1)
                .forks(1)
                .build();

        new Runner(opt).run();
    }

}
