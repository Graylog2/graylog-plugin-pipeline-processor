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

import com.google.common.collect.ImmutableList;
import com.google.common.primitives.Ints;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class ListRemoveAt extends AbstractFunction<List> {

    public static final String NAME = "list_remove_at";
    private final ParameterDescriptor<Object, List> listParam;
    private final ParameterDescriptor<Long, Long> indexParam;

    public ListRemoveAt() {
        listParam = ParameterDescriptor.object("list", List.class).description("The list to modify").build();
        indexParam = ParameterDescriptor.integer("index").description("The list index to remove").build();
    }

    @Override
    public List evaluate(FunctionArgs args, EvaluationContext context) {
        final List list = listParam.required(args, context);
        final Long index = indexParam.required(args, context);

        if (list == null || index == null) {
            return null;
        }

        final int listIndex = index.intValue();
        final ImmutableList.Builder<Object> listBuilder = ImmutableList.builder();
        for (int i = 0; i < list.size(); i++) {
            if (i != listIndex) {
                listBuilder.add(list.get(i));
            }
        }
        return listBuilder.build();
    }

    @Override
    public FunctionDescriptor<List> descriptor() {
        return FunctionDescriptor.<List>builder()
                .name(NAME)
                .returnType(List.class)
                .params(of(listParam, indexParam))
                .description("Remove a specific index from a list")
                .build();
    }
}
