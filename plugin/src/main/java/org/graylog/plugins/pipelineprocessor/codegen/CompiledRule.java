package org.graylog.plugins.pipelineprocessor.codegen;

import com.google.auto.value.AutoValue;

@AutoValue
public abstract class CompiledRule {

    public abstract String name();

    public abstract byte[] byteCode();

    public static Builder builder() {
        return new AutoValue_CompiledRule.Builder();
    }

    @AutoValue.Builder
    public static abstract class Builder {

        public abstract Builder name(String name);

        public abstract Builder byteCode(byte[] byteCode);

        public abstract CompiledRule build();

    }
}
