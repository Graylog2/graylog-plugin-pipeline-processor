package org.graylog.plugins.pipelineprocessor.codegen;

import org.graylog.plugins.pipelineprocessor.ast.Rule;

public interface CodeGenerator {

    Class<? extends GeneratedRule> generateCompiledRule(Rule rule, PipelineClassloader ruleClassloader);

}
