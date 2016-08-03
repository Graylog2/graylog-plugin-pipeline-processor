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
package org.graylog.plugins.pipelineprocessor;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.inject.assistedinject.Assisted;
import org.graylog.plugins.pipelineprocessor.ast.Pipeline;
import org.graylog.plugins.pipelineprocessor.ast.Rule;
import org.graylog.plugins.pipelineprocessor.parser.PipelineRuleParser;
import org.graylog.plugins.pipelineprocessor.processors.PipelineInterpreter;
import org.graylog.plugins.pipelineprocessor.processors.listeners.NoopInterpreterListener;
import org.graylog2.decorators.Decorator;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.TextField;
import org.graylog2.plugin.decorators.SearchResponseDecorator;
import org.graylog2.rest.models.messages.responses.ResultMessageSummary;
import org.graylog2.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

public class SyslogLevelMapperDecorator implements SearchResponseDecorator {
    private static final String CK_FIELD_NAME = "fieldName";
    private static final String CK_PIPELINE_DEFINITION = "pipeline \"Uppercase decorator\"\nstage 0 match either\nrule \"Uppercase field\"\nend";

    private final List<Pipeline> pipelines;
    private final PipelineInterpreter pipelineInterpreter;
    private final Decorator decorator;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        SyslogLevelMapperDecorator create(Decorator decorator);

        @Override
        SyslogLevelMapperDecorator.Config getConfig();

        @Override
        SyslogLevelMapperDecorator.Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {
        @Inject
        public Config() {
        }

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            return new ConfigurationRequest() {{
                addField(new TextField(CK_FIELD_NAME, "Field Name", "level", "The Name of the field which includes the syslog level"));
            }};
        };
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Syslog Level Mapper", "http://docs.graylog.org/en/2.0/pages/pipelines.html", "Numeric Syslog levels to text");
        }
    }

    @Inject
    public SyslogLevelMapperDecorator(PipelineInterpreter pipelineInterpreter,
                              PipelineRuleParser pipelineRuleParser,
                              @Assisted Decorator decorator) {
        this.pipelineInterpreter = pipelineInterpreter;
        this.decorator = decorator;
        final String fieldName = (String)decorator.config().get(CK_FIELD_NAME);

        this.pipelines = pipelineRuleParser.parsePipelines(CK_PIPELINE_DEFINITION);
        final List<Rule> rules = ImmutableList.of(pipelineRuleParser.parseRule(getRuleForField(fieldName), true));
        this.pipelines.forEach(pipeline -> {
            pipeline.stages().forEach(stage -> stage.setRules(rules));
        });
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> results = new ArrayList<>();
        searchResponse.messages().forEach((inMessage) -> {
            final Map<String, Object> originalMessage = ImmutableMap.copyOf(inMessage.message());
            final Message message = new Message(inMessage.message());
            final List<Message> additionalCreatedMessages = pipelineInterpreter.processForResolvedPipelines(message,
                    message.getId(),
                    new HashSet<>(this.pipelines),
                    new NoopInterpreterListener());

            results.add(ResultMessageSummary.create(inMessage.highlightRanges(), message.getFields(), inMessage.index()));
            additionalCreatedMessages.forEach((additionalMessage) -> {
                // TODO: pass proper highlight ranges. Need to rebuild them for new messages.
                results.add(ResultMessageSummary.create(
                        ImmutableMultimap.of(),
                        additionalMessage.getFields(),
                        "[created from decorator]"
                ));
            });
        });

        return searchResponse.toBuilder().messages(results).build();
    }

    private String getRuleForField(String fieldName) {
        return "rule \"Map Syslog Levels\"\n"
        + "when\n"
        + "has_field(\""+ fieldName + "\")\n"
        + "then\n"
        + "let mapping = {`0`: \"Emergency (0)\",\n"
        + "         `1`: \"Alert (1)\",\n"
        + "         `2`: \"Critical (2)\",\n"
        + "         `3`: \"Error (3)\",\n"
        + "         `4`: \"Warning (4)\",\n"
        + "         `5`: \"Notice (5)\",\n"
        + "         `6`: \"Informational (6)\",\n"
        + "         `7`: \"Debug (7)\"};\n"
        + "set_field(\"" + fieldName + "\", mapping[to_string($message." + fieldName + ")]);\n"
        + "end\n";
    }
}
