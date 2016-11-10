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
package org.graylog.plugins.pipelineprocessor.ast.expressions;

import com.google.common.collect.ImmutableList;

import org.antlr.v4.runtime.Token;

public abstract class BinaryExpression extends UnaryExpression {

    protected Expression left;

    public BinaryExpression(Token start, Expression left, Expression right) {
        super(start, right);
        this.left = left;
    }

    @Override
    public boolean isConstant() {
        return left.isConstant() && right.isConstant();
    }

    public Expression left() {
        return left;
    }

    public void left(Expression left) {
        this.left = left;
    }
    @Override
    public Iterable<Expression> children() {
        return ImmutableList.of(left, right);
    }
}
