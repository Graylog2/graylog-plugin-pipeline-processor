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
package org.graylog.plugins.pipelineprocessor.rest;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.google.auto.value.AutoValue;

import javax.annotation.Nullable;
import java.util.Map;

@AutoValue
@JsonAutoDetect
public abstract class SimulationRequest {
    @JsonProperty
    public abstract String streamId();

    @JsonProperty
    public abstract String message();

    @JsonProperty
    public abstract String remoteAddress();

    @JsonProperty
    public abstract String codec();

    @JsonProperty
    @Nullable
    public abstract Map<String, Object> configuration();

    public static Builder builder() {
        return new AutoValue_SimulationRequest.Builder();
    }

    @JsonCreator
    public static SimulationRequest create (@JsonProperty("stream_id") String streamId,
                                            @JsonProperty("message") String message,
                                            @JsonProperty("remote_address") String remoteAddress,
                                            @JsonProperty("codec") String codec,
                                            @JsonProperty("configuration") Map<String, Object> configuration) {
        return builder()
                .streamId(streamId)
                .message(message)
                .remoteAddress(remoteAddress)
                .codec(codec)
                .configuration(configuration)
                .build();
    }

    @AutoValue.Builder
    public abstract static class Builder {
        public abstract SimulationRequest build();

        public abstract Builder streamId(String streamId);

        public abstract Builder message(String message);

        public abstract Builder remoteAddress(String remoteAddress);

        public abstract Builder codec(String codec);

        public abstract Builder configuration(Map<String, Object> configuration);
    }
}
