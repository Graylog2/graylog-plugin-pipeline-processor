package org.graylog.plugins.pipelineprocessor.db;

import org.graylog.plugins.pipelineprocessor.rest.PipelineConnections;
import org.graylog2.database.NotFoundException;

import java.util.Set;

public interface PipelineStreamConnectionsService {
    PipelineConnections save(PipelineConnections connections);

    PipelineConnections load(String streamId) throws NotFoundException;

    Set<PipelineConnections> loadAll();
}
