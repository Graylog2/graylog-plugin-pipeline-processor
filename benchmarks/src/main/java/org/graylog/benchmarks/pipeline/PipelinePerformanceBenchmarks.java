/**
 * This file is part of Graylog Pipeline Processor.
 *
 * Graylog Pipeline Processor is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Graylog Pipeline Processor is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Graylog Pipeline Processor.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.graylog.benchmarks.pipeline;

import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import org.graylog.plugins.pipelineprocessor.db.RuleService;
import org.graylog.plugins.pipelineprocessor.db.memory.InMemoryRuleService;
import org.graylog.plugins.pipelineprocessor.functions.ProcessorFunctionsModule;
import org.graylog.plugins.pipelineprocessor.processors.PipelineInterpreter;
import org.graylog2.database.NotFoundException;
import org.graylog2.grok.GrokPatternService;
import org.graylog2.grok.InMemoryGrokPatternService;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.Tools;
import org.graylog2.plugin.alarms.AlertCondition;
import org.graylog2.plugin.database.Persisted;
import org.graylog2.plugin.database.ValidationException;
import org.graylog2.plugin.database.validators.ValidationResult;
import org.graylog2.plugin.database.validators.Validator;
import org.graylog2.plugin.streams.Output;
import org.graylog2.plugin.streams.Stream;
import org.graylog2.plugin.streams.StreamRule;
import org.graylog2.rest.resources.streams.requests.CreateStreamRequest;
import org.graylog2.shared.bindings.SchedulerBindings;
import org.graylog2.shared.journal.Journal;
import org.graylog2.shared.journal.NoopJournal;
import org.graylog2.streams.StreamService;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static org.mockito.Mockito.withSettings;

public class PipelinePerformanceBenchmarks {

    private static final String BENCHMARKS_RESOURCE_DIRECTORY = "/benchmarks";
    private static final Message MESSAGE = new Message("hallo welt", "127.0.0.1", Tools.nowUTC());

    @State(Scope.Benchmark)
    public static class PipelineConfig {

        @Param({})
        private String directoryName;
        private PipelineInterpreter interpreter;

        @Setup
        public void setup() throws URISyntaxException, IOException {
            final Path path = getResourcePath();

            Files.list(path.resolve(directoryName))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(inputFile -> {
                        // TODO actually
                    });

            final Injector injector = Guice.createInjector(
                    new ProcessorFunctionsModule(),
                    new SchedulerBindings(),
                    new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(RuleService.class).to(InMemoryRuleService.class);
                        }
                    },
            new AbstractModule() {
                        @Override
                        protected void configure() {
                            bind(Journal.class).to(NoopJournal.class).asEagerSingleton();
                            bind(StreamService.class).toInstance(new StreamService() {
                                @Override
                                public Stream create(Map<String, Object> fields) {
                                    return null;
                                }

                                @Override
                                public Stream create(CreateStreamRequest request, String userId) {
                                    return null;
                                }

                                @Override
                                public Stream load(String id) throws NotFoundException {
                                    return null;
                                }

                                @Override
                                public void destroy(Stream stream) throws NotFoundException {

                                }

                                @Override
                                public List<Stream> loadAll() {
                                    return null;
                                }

                                @Override
                                public List<Stream> loadAllEnabled() {
                                    return null;
                                }

                                @Override
                                public long count() {
                                    return 0;
                                }

                                @Override
                                public void pause(Stream stream) throws ValidationException {

                                }

                                @Override
                                public void resume(Stream stream) throws ValidationException {

                                }

                                @Override
                                public List<StreamRule> getStreamRules(Stream stream) throws NotFoundException {
                                    return null;
                                }

                                @Override
                                public List<Stream> loadAllWithConfiguredAlertConditions() {
                                    return null;
                                }

                                @Override
                                public List<AlertCondition> getAlertConditions(Stream stream) {
                                    return null;
                                }

                                @Override
                                public AlertCondition getAlertCondition(Stream stream,
                                                                        String conditionId) throws NotFoundException {
                                    return null;
                                }

                                @Override
                                public void addAlertCondition(Stream stream,
                                                              AlertCondition condition) throws ValidationException {

                                }

                                @Override
                                public void updateAlertCondition(Stream stream,
                                                                 AlertCondition condition) throws ValidationException {

                                }

                                @Override
                                public void removeAlertCondition(Stream stream, String conditionId) {

                                }

                                @Override
                                public void addAlertReceiver(Stream stream, String type, String name) {

                                }

                                @Override
                                public void removeAlertReceiver(Stream stream, String type, String name) {

                                }

                                @Override
                                public void addOutput(Stream stream, Output output) {

                                }

                                @Override
                                public void removeOutput(Stream stream, Output output) {

                                }

                                @Override
                                public void removeOutputFromAllStreams(Output output) {

                                }

                                @Override
                                public <T extends Persisted> int destroy(T model) {
                                    return 0;
                                }

                                @Override
                                public <T extends Persisted> int destroyAll(Class<T> modelClass) {
                                    return 0;
                                }

                                @Override
                                public <T extends Persisted> String save(T model) throws ValidationException {
                                    return null;
                                }

                                @Override
                                public <T extends Persisted> String saveWithoutValidation(T model) {
                                    return null;
                                }

                                @Override
                                public <T extends Persisted> Map<String, List<ValidationResult>> validate(T model,
                                                                                                          Map<String, Object> fields) {
                                    return null;
                                }

                                @Override
                                public <T extends Persisted> Map<String, List<ValidationResult>> validate(T model) {
                                    return null;
                                }

                                @Override
                                public Map<String, List<ValidationResult>> validate(Map<String, Validator> validators,
                                                                                    Map<String, Object> fields) {
                                    return null;
                                }
                            });
                            bind(GrokPatternService.class).to(InMemoryGrokPatternService.class);
                        }
                    });

            interpreter = injector.getInstance(PipelineInterpreter.class);
        }

    }

    @Benchmark
    public void runPipeline(PipelineConfig config, Blackhole bh) {
        bh.consume(config.interpreter.process(MESSAGE));
    }

    public static void main(String[] args) throws RunnerException, URISyntaxException, IOException {
        final String[] values = loadBenchmarkNames().toArray(new String[]{});
        Options opt = new OptionsBuilder()
                .include(PipelinePerformanceBenchmarks.class.getSimpleName())
                .warmupIterations(3)
                .warmupTime(TimeValue.seconds(5))
                .measurementIterations(5)
                .measurementTime(TimeValue.seconds(20))
                .threads(1)
                .forks(1)
                .param("directoryName", values)
                .build();

        new Runner(opt).run();
    }


    private static List<String> loadBenchmarkNames() throws URISyntaxException, IOException {
        Path benchmarksPath = getResourcePath();

        return Files.list(benchmarksPath)
                .map(Path::getFileName)
                .map(Path::toString)
                .sorted()
                .collect(Collectors.toList());
    }

    private static Path getResourcePath() throws URISyntaxException, IOException {
        final URI benchmarks = PipelinePerformanceBenchmarks.class.getResource(BENCHMARKS_RESOURCE_DIRECTORY).toURI();

        Path benchmarksPath;
        if (benchmarks.getScheme().equals("jar")) {
            FileSystem fileSystem = FileSystems.newFileSystem(benchmarks, Collections.emptyMap());
            benchmarksPath = fileSystem.getPath(BENCHMARKS_RESOURCE_DIRECTORY);
            fileSystem.close();
        } else {
            benchmarksPath = Paths.get(benchmarks);
        }
        return benchmarksPath;
    }

    private static <T> T mock(Class<T> classToMock) {
        return Mockito.mock(classToMock, withSettings().stubOnly());
    }

}
