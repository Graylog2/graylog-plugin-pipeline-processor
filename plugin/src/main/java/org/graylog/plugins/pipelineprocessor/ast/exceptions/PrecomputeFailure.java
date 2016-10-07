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
package org.graylog.plugins.pipelineprocessor.ast.exceptions;

public class PrecomputeFailure extends RuntimeException {
    private final String argumentName;

    public PrecomputeFailure(String argumentName, Exception cause) {
        super(cause);
        this.argumentName = argumentName;
    }

    public String getArgumentName() {
        return argumentName;
    }

    @Override
    public String getMessage() {
        return "Unable to pre-compute argument " + getArgumentName() + ": " + getCause().getMessage();
    }
}
