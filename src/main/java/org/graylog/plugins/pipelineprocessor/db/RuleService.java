package org.graylog.plugins.pipelineprocessor.db;

import org.graylog2.database.NotFoundException;

import java.util.Collection;

public interface RuleService {
    RuleDao save(RuleDao rule);

    RuleDao load(String id) throws NotFoundException;

    Collection<RuleDao> loadAll();

    void delete(String id);

    Collection<RuleDao> loadNamed(Collection<String> ruleNames);
}
