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

import com.google.common.base.Strings;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.ImmutableSet;
import com.google.inject.assistedinject.Assisted;
import org.graylog.plugins.pipelineprocessor.db.PipelineDao;
import org.graylog.plugins.pipelineprocessor.db.PipelineService;
import org.graylog.plugins.pipelineprocessor.processors.PipelineInterpreter;
import org.graylog.plugins.pipelineprocessor.processors.listeners.NoopInterpreterListener;
import org.graylog2.decorators.Decorator;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.configuration.ConfigurationRequest;
import org.graylog2.plugin.configuration.fields.ConfigurationField;
import org.graylog2.plugin.configuration.fields.DropdownField;
import org.graylog2.plugin.decorators.SearchResponseDecorator;
import org.graylog2.rest.models.messages.responses.DecorationStats;
import org.graylog2.rest.models.messages.responses.ResultMessageSummary;
import org.graylog2.rest.resources.search.responses.SearchResponse;

import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PipelineProcessorMessageDecorator implements SearchResponseDecorator {
    private static final String CONFIG_FIELD_PIPELINE = "pipeline";

    private final PipelineInterpreter pipelineInterpreter;
    private final Decorator decorator;
    private final ImmutableSet<String> pipelines;

    public interface Factory extends SearchResponseDecorator.Factory {
        @Override
        PipelineProcessorMessageDecorator create(Decorator decorator);

        @Override
        Config getConfig();

        @Override
        Descriptor getDescriptor();
    }

    public static class Config implements SearchResponseDecorator.Config {
        private final PipelineService pipelineService;

        @Inject
        public Config(PipelineService pipelineService) {
            this.pipelineService = pipelineService;
        }

        @Override
        public ConfigurationRequest getRequestedConfiguration() {
            final Map<String, String> pipelineOptions = this.pipelineService.loadAll().stream()
                    .sorted((o1, o2) -> o1.title().compareTo(o2.title()))
                    .collect(Collectors.toMap(PipelineDao::id, PipelineDao::title));
            return new ConfigurationRequest() {{
                addField(new DropdownField(CONFIG_FIELD_PIPELINE,
                        "Pipeline",
                        "",
                        pipelineOptions,
                        "Which pipeline to use for message decoration",
                        ConfigurationField.Optional.NOT_OPTIONAL));
            }};
        };
    }

    public static class Descriptor extends SearchResponseDecorator.Descriptor {
        public Descriptor() {
            super("Pipeline Processor Decorator", "http://docs.graylog.org/en/2.0/pages/pipelines.html", "Pipeline Processor Decorator");
        }
    }

    @Inject
    public PipelineProcessorMessageDecorator(PipelineInterpreter pipelineInterpreter,
                                             @Assisted Decorator decorator) {
        this.pipelineInterpreter = pipelineInterpreter;
        this.decorator = decorator;
        final String pipelineId = (String)decorator.config().get(CONFIG_FIELD_PIPELINE);
        if (Strings.isNullOrEmpty(pipelineId)) {
            this.pipelines = ImmutableSet.of();
        } else {
            this.pipelines = ImmutableSet.of(pipelineId);
        }
    }

    @Override
    public SearchResponse apply(SearchResponse searchResponse) {
        final List<ResultMessageSummary> results = new ArrayList<>();
        if (pipelines.isEmpty()) {
            return searchResponse;
        }
        searchResponse.messages().forEach((inMessage) -> {
            final Map<String, Object> originalMessage = ImmutableMap.copyOf(inMessage.message());
            final Message message = new Message(inMessage.message());
            final List<Message> additionalCreatedMessages = pipelineInterpreter.processForPipelines(message,
                    message.getId(),
                    pipelines,
                    new NoopInterpreterListener());

            final Map<String, DecorationStats> decorationStats = new HashMap<>(inMessage.decorationStats());
            decorationStats.put(decorator.id(), DecorationStats.create(originalMessage, message.getFields()));

            results.add(ResultMessageSummary.create(inMessage.highlightRanges(), message.getFields(), inMessage.index(), decorationStats));
            additionalCreatedMessages.forEach((additionalMessage) -> {
                // TODO: pass proper highlight ranges. Need to rebuild them for new messages.
                results.add(ResultMessageSummary.create(
                        ImmutableMultimap.of(),
                        additionalMessage.getFields(),
                        "[created from decorator]",
                        ImmutableMap.of(decorator.id(), DecorationStats.create(Collections.emptyMap(), additionalMessage.getFields()))
                ));
            });
        });

        return searchResponse.toBuilder().messages(results).build();
    }
}
