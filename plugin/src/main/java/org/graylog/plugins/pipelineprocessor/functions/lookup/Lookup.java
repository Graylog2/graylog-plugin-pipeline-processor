package org.graylog.plugins.pipelineprocessor.functions.lookup;

import com.google.inject.Inject;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.graylog2.lookup.LookupTableService;

import static org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;
import static org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;

public class Lookup extends AbstractFunction<Object> {

    public static final String NAME = "lookup";

    private final ParameterDescriptor<String, LookupTableService.Function> lookupTableParam;
    private final ParameterDescriptor<Object, Object> keyParam;
    private final ParameterDescriptor<Object, Object> defaultParam;

    @Inject
    public Lookup(LookupTableService lookupTableService) {
        lookupTableParam = string("lookup_table", LookupTableService.Function.class)
                .transform(tableName -> lookupTableService.newBuilder().lookupTable(tableName).build())
                .build();
        keyParam = object("key").build();
        defaultParam = object("default").optional().build();
    }

    @Override
    public Object evaluate(FunctionArgs args, EvaluationContext context) {
        Object key = keyParam.required(args, context);
        if (key == null) {
            return defaultParam.optional(args, context);
        }
        LookupTableService.Function table = lookupTableParam.required(args, context);
        if (table == null) {
            return defaultParam.optional(args, context);
        }
        Object value = table.lookup(key);
        if (value == null) {
            return defaultParam.optional(args, context);
        }
        return value;
    }

    @Override
    public FunctionDescriptor<Object> descriptor() {
        return FunctionDescriptor.builder()
                .name(NAME)
                .description("Looks a value up in the named lookup table.")
                .params(lookupTableParam, keyParam)
                .returnType(Object.class)
                .build();
    }
}
