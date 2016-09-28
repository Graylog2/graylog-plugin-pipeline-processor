package org.graylog.plugins.pipelineprocessor.db;

import org.graylog2.database.NotFoundException;

import java.util.Collection;

public interface PipelineService {
    PipelineDao save(PipelineDao pipeline);

    PipelineDao load(String id) throws NotFoundException;

    Collection<PipelineDao> loadAll();

    void delete(String id);
}
