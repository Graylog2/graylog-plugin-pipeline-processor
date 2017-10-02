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
import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.functions.AbstractFunction;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionArgs;
import org.graylog.plugins.pipelineprocessor.ast.functions.FunctionDescriptor;
import org.graylog.plugins.pipelineprocessor.ast.functions.ParameterDescriptor;

import java.util.Collections;
import java.util.List;

import static com.google.common.collect.ImmutableList.of;

public class ListRetainAll extends AbstractFunction<List> {

    public static final String NAME = "list_retain_all";
    private final ParameterDescriptor<Object, List> listParam;
    private final ParameterDescriptor<Object, List> elementsParam;

    public ListRetainAll() {
        listParam = ParameterDescriptor.object("list", List.class).description("The list to modify").build();
        elementsParam = ParameterDescriptor.object("elements", List.class).description("The elements to retain").build();
    }

    @Override
    public List evaluate(FunctionArgs args, EvaluationContext context) {
        @SuppressWarnings("unchecked")
        final List<? extends Object> list = (List<? extends Object>) listParam.required(args, context);
        final List elements = elementsParam.required(args, context);

        if (list == null) {
            return null;
        }

        if(elements == null || elements.isEmpty()) {
            return Collections.emptyList();
        }

        return list.stream()
                .filter(x -> x != null && elements.contains(x))
                .collect(ImmutableList.toImmutableList());
    }

    @Override
    public FunctionDescriptor<List> descriptor() {
        return FunctionDescriptor.<List>builder()
                .name(NAME)
                .returnType(List.class)
                .params(of(listParam, elementsParam))
                .description("Retain elements in a list")
                .build();
    }
}