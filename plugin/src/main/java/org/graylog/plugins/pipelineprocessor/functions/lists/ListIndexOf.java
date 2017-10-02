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
package org.graylog.plugins.pipelineprocessor.functions.lists;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class ListIndexOf extends AbstractFunction<Long> {

    public static final String NAME = "list_index_of";
    private final ParameterDescriptor<Object, List> listParam;
    private final ParameterDescriptor<Object, Object> elementParam;

    public ListIndexOf() {
        listParam = ParameterDescriptor.object("list", List.class).description("The list to check").build();
        elementParam = ParameterDescriptor.object("element").description("The element to find").build();
    }

    @Override
    public Long evaluate(FunctionArgs args, EvaluationContext context) {
        final List value = listParam.required(args, context);
        final Object search = elementParam.required(args, context);

        return value == null ? null : (long) value.indexOf(search);
    }

    @Override
    public FunctionDescriptor<Long> descriptor() {
        return FunctionDescriptor.<Long>builder()
                .name(NAME)
                .returnType(Long.class)
                .params(of(listParam, elementParam))
                .description("Returns the index of a specific element in a list")
                .build();
    }
}
