package org.graylog.plugins.pipelineprocessor.db.mongodb;

import org.graylog.plugins.pipelineprocessor.db.PipelineService;
import org.graylog.plugins.pipelineprocessor.db.PipelineStreamConnectionsService;
import org.graylog.plugins.pipelineprocessor.db.RuleService;
import org.graylog2.plugin.PluginModule;

public class MongoDbServicesModule extends PluginModule {
    @Override
    protected void configure() {
        bind(PipelineService.class).to(MongoDbPipelineService.class);
        bind(RuleService.class).to(MongoDbRuleService.class);
        bind(PipelineStreamConnectionsService.class).to(MongoDbPipelineStreamConnectionsService.class);
    }
}
