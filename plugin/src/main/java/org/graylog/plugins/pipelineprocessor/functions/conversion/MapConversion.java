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
package org.graylog.plugins.pipelineprocessor.functions.conversion;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Collections;
import java.util.Map;

import static com.google.common.collect.ImmutableList.of;
import static org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor.object;

public class MapConversion extends AbstractFunction<Map> {
    private static final ObjectMapper MAPPER = new ObjectMapper();

    public static final String NAME = "to_map";
    private static final String VALUE = "value";

    private final ParameterDescriptor<Object, Object> valueParam;


    public MapConversion() {
        this.valueParam = object(VALUE).description("Value to convert").build();
    }

    @Override
    public Map evaluate(FunctionArgs args, EvaluationContext context) {
        final Object value = valueParam.required(args, context);

        if (value == null) {
            return Collections.emptyMap();
        } else if (value instanceof Map) {
            return (Map) value;
        } else if (value instanceof JsonNode) {
            final JsonNode jsonNode = (JsonNode) value;
            return MAPPER.convertValue(jsonNode, Map.class);
        } else {
            return Collections.emptyMap();
        }
    }

    @Override
    public FunctionDescriptor<Map> descriptor() {
        return FunctionDescriptor.<Map>builder()
                .name(NAME)
                .returnType(Map.class)
                .params(of(valueParam))
                .description("Converts a value to a long value using its string representation")
                .build();
    }
}
