package org.graylog.benchmarks.pipeline;

import com.codahale.metrics.MetricRegistry;
import com.google.common.eventbus.EventBus;
import com.google.inject.AbstractModule;
import com.google.inject.Guice;
import com.google.inject.Injector;
import com.mongodb.DB;
import com.mongodb.Mongo;
import com.mongodb.client.MongoDatabase;
import org.graylog.plugins.pipelineprocessor.db.PipelineService;
import org.graylog.plugins.pipelineprocessor.db.PipelineStreamConnectionsService;
import org.graylog.plugins.pipelineprocessor.db.RuleService;
import org.graylog.plugins.pipelineprocessor.functions.ProcessorFunctionsModule;
import org.graylog.plugins.pipelineprocessor.parser.FunctionRegistry;
import org.graylog.plugins.pipelineprocessor.parser.PipelineRuleParser;
import org.graylog.plugins.pipelineprocessor.processors.PipelineInterpreter;
import org.graylog2.database.MongoConnection;
import org.graylog2.database.NotFoundException;
import org.graylog2.grok.GrokPattern;
import org.graylog2.grok.GrokPatternRegistry;
import org.graylog2.grok.GrokPatternService;
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
import org.graylog2.shared.journal.Journal;
import org.graylog2.streams.StreamService;
import org.mockito.Mockito;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Param;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.Setup;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

import static org.mockito.Mockito.withSettings;

public class PipelinePerformanceBenchmarks {

    private static final String BENCHMARKS_RESOURCE_DIRECTORY = "/benchmarks";

    @State(Scope.Benchmark)
    public static class PipelineConfig {

        @Param({})
        private String directoryName;
        private PipelineInterpreter interpreter;

        @Setup
        public void setup() throws URISyntaxException, IOException {
            final Path path = getResourcePath();

            final RuleService ruleService = mock(RuleService.class);
            final PipelineService pipelineService = mock(PipelineService.class);
            final PipelineStreamConnectionsService connectionsService = mock(PipelineStreamConnectionsService.class);

            Files.list(path.resolve(directoryName))
                    .map(Path::toFile)
                    .filter(File::isFile)
                    .forEach(inputFile -> {
                        // TODO actually
                    });

            final Injector injector = Guice.createInjector(new ProcessorFunctionsModule(), new AbstractModule() {
                @Override
                protected void configure() {
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
                    bind(GrokPatternRegistry.class).toInstance(new GrokPatternRegistry(new EventBus(),
                                                                                       new GrokPatternService() {
                                                                                           @Override
                                                                                           public GrokPattern load(
                                                                                                   String patternId) throws NotFoundException {
                                                                                               return null;
                                                                                           }

                                                                                           @Override
                                                                                           public Set<GrokPattern> loadAll() {
                                                                                               return null;
                                                                                           }

                                                                                           @Override
                                                                                           public GrokPattern save(
                                                                                                   GrokPattern pattern) throws ValidationException {
                                                                                               return null;
                                                                                           }

                                                                                           @Override
                                                                                           public List<GrokPattern> saveAll(Collection<GrokPattern> patterns,
                                                                                                                            boolean replace) throws ValidationException {
                                                                                               return null;
                                                                                           }

                                                                                           @Override
                                                                                           public boolean validate(
                                                                                                   GrokPattern pattern) {
                                                                                               return false;
                                                                                           }

                                                                                           @Override
                                                                                           public int delete(String patternId) {
                                                                                               return 0;
                                                                                           }

                                                                                           @Override
                                                                                           public int deleteAll() {
                                                                                               return 0;
                                                                                           }
                                                                                       }, Executors.newScheduledThreadPool(1)));
                    bind(MongoConnection.class).toInstance(new MongoConnection() {
                        @Override
                        public Mongo connect() {
                            return null;
                        }

                        @Override
                        public DB getDatabase() {
                            return null;
                        }

                        @Override
                        public MongoDatabase getMongoDatabase() {
                            return null;
                        }
                    });
                }
            });
            final PipelineRuleParser parser = new PipelineRuleParser(injector.getInstance(FunctionRegistry.class));

            interpreter = new PipelineInterpreter(
                    ruleService,
                    pipelineService,
                    connectionsService,
                    parser,
                    mock(Journal.class),
                    new MetricRegistry(),
                    Executors.newScheduledThreadPool(1),
                    mock(EventBus.class)

            );
        }
    }

    @Benchmark
    public void runPipeline(PipelineConfig config) {
        config.interpreter.process(new Message("hallo welt", "127.0.0.1", Tools.nowUTC()));
    }

    public static void main(String[] args) throws RunnerException, URISyntaxException, IOException {
        loadBenchmarkNames();
        final String[] values = loadBenchmarkNames().toArray(new String[]{});
        Options opt = new OptionsBuilder()
                .include(PipelinePerformanceBenchmarks.class.getSimpleName())
                .warmupIterations(2)
                .measurementIterations(5)
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
