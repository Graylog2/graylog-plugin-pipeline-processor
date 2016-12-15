package org.graylog.plugins.pipelineprocessor.codegen;

import org.graylog.plugins.pipelineprocessor.ast.Rule;
import org.junit.Test;

public class ByteCodeGeneratorTest {
    @Test
    public void generateCompiledRule() throws Exception {
        final ByteCodeGenerator generator = new ByteCodeGenerator();

        final PipelineClassloader cl = new PipelineClassloader();
        final Rule build = Rule.alwaysFalse("test").withId("1");
        final CompiledRule compiledRule = generator.getByteCode(build);

    }

}