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
package org.graylog.plugins.pipelineprocessor.functions.messages;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;
import org.graylog2.plugin.Message;

import java.util.Map;
import java.util.Optional;
import java.util.function.Function;

import static com.google.common.collect.ImmutableList.of;
import static org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.string;
import static org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.type;

public class SetFields extends AbstractFunction<Void> {

    public static final String NAME = "set_fields";

    private final ParameterDescriptor<Object, Map> fieldsParam;
    private final ParameterDescriptor<String, String> prefixParam;
    private final ParameterDescriptor<String, String> suffixParam;
    private final ParameterDescriptor<Message, Message> messageParam;

    public SetFields() {
        fieldsParam = type("fields", Object.class, Map.class)
                .description("The map of new fields to set")
                .transform(new MapTransformer())
                .build();
        prefixParam = string("prefix").optional().description("The prefix for the field names").build();
        suffixParam = string("suffix").optional().description("The suffix for the field names").build();
        messageParam = type("message", Message.class).optional().description("The message to use, defaults to '$message'").build();
    }

    @Override
    public Void evaluate(FunctionArgs args, EvaluationContext context) {
        @SuppressWarnings("unchecked") final Map<String, Object> fields = fieldsParam.required(args, context);
        final Message message = messageParam.optional(args, context).orElse(context.currentMessage());
        final Optional<String> prefix = prefixParam.optional(args, context);
        final Optional<String> suffix = suffixParam.optional(args, context);

        if (fields != null) {
            fields.forEach((field, value) -> {
                if (prefix.isPresent()) {
                    field = prefix.get() + field;
                }
                if (suffix.isPresent()) {
                    field = field + suffix.get();
                }
                message.addField(field, value);
            });
        }
        return null;
    }

    @Override
    public FunctionDescriptor<Void> descriptor() {
        return FunctionDescriptor.<Void>builder()
                .name(NAME)
                .returnType(Void.class)
                .params(of(fieldsParam, prefixParam, suffixParam, messageParam))
                .description("Sets new fields in a message")
                .build();
    }

    private static class MapTransformer implements Function<Object, Map> {
        private final ObjectMapper objectMapper;

        public MapTransformer(ObjectMapper objectMapper) {
            this.objectMapper = objectMapper;
        }

        public MapTransformer() {
            this(new ObjectMapper());
        }

        @Override
        public Map apply(Object o) {
            if (o instanceof Map) {
                return (Map) o;
            } else if (o instanceof JsonNode) {
                final JsonNode jsonNode = (JsonNode) o;
                return objectMapper.convertValue(jsonNode, Map.class);
            } else {
                return null;
            }
        }
    }
}
