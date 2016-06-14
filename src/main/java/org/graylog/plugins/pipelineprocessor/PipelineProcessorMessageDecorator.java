package org.graylog.plugins.pipelineprocessor;

import com.google.common.collect.ImmutableSet;
import org.graylog.plugins.pipelineprocessor.processors.PipelineInterpreter;
import org.graylog2.indexer.results.ResultMessage;
import org.graylog2.plugin.Message;
import org.graylog2.plugin.decorators.MessageDecorator;

import javax.inject.Inject;
import java.util.List;

public class PipelineProcessorMessageDecorator implements MessageDecorator {
    private final PipelineInterpreter pipelineInterpreter;

    @Inject
    public PipelineProcessorMessageDecorator(PipelineInterpreter pipelineInterpreter) {
        this.pipelineInterpreter = pipelineInterpreter;
    }

    @Override
    public ResultMessage apply(ResultMessage resultMessage) {
        final Message message = resultMessage.getMessage();
        final List<Message> messages = pipelineInterpreter.processForPipelines(message, message.getId(), ImmutableSet.of("575ea96d726bd1e03ebb63e7"));
        resultMessage.setMessage(message);
        return resultMessage;
    }
}
