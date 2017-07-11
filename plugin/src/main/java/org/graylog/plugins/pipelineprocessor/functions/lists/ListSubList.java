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

public class ListSubList extends AbstractFunction<List> {

    public static final String NAME = "sub_list";
    private final ParameterDescriptor<Object, List> listParam;
    private final ParameterDescriptor<Long, Long> startParam;
    private final ParameterDescriptor<Long, Long> endParam;

    public ListSubList() {
        listParam = ParameterDescriptor.object("list", List.class).description("The list to create a sublist from").build();
        startParam = ParameterDescriptor.integer("from").description("Start index (inclusive)").build();
        endParam = ParameterDescriptor.integer("to").description("End index (exclusive)").build();
    }

    @Override
    public List evaluate(FunctionArgs args, EvaluationContext context) {
        @SuppressWarnings("unchecked") final List<? extends Object> list = (List<? extends Object>) listParam.required(args, context);
        final Long startLong = startParam.required(args, context);
        final Long endLong = endParam.required(args, context);

        if (list == null) {
            return null;
        }

        if (startLong == null || endLong == null) {
            return list;
        }

        final int start = startLong.intValue();
        final int end = endLong.intValue();
        if (start < 0 || end < 0 || end < start || start >= list.size() || end > list.size()) {
            return list;
        }

        return list.subList(start, end);
    }

    @Override
    public FunctionDescriptor<List> descriptor() {
        return FunctionDescriptor.<List>builder()
                .name(NAME)
                .returnType(List.class)
                .params(of(listParam, startParam, endParam))
                .description("Create a sublist")
                .build();
    }
}
