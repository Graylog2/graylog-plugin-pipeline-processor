package org.graylog.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;

import javax.annotation.Nullable;

import static com.google.common.base.MoreObjects.firstNonNull;

public class SignedExpression extends UnaryExpression implements NumericExpression {
    private final boolean isPlus;

    public SignedExpression(Token start, Expression right, boolean isPlus) {
        super(start, right);
        this.isPlus = isPlus;
    }

    @Override
    public boolean isIntegral() {
        return getType().equals(Long.class);
    }

    @Override
    public long evaluateLong(EvaluationContext context) {
        return (long) firstNonNull(evaluateUnsafe(context), 0);
    }

    @Override
    public double evaluateDouble(EvaluationContext context) {
        return (double) firstNonNull(evaluateUnsafe(context), 0d);
    }

    @Nullable
    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final Object value = right.evaluateUnsafe(context);

        if (value instanceof Long) {
            long number = (long) value;
            return isPlus ? +number : -number;
        } else if (value instanceof Double) {
            double number = (double) value;
            return isPlus ? +number : -number;
        }
        // nothing we could handle, the type checker should've caught it
        throw new IllegalArgumentException("Value of '" + right.toString() + "' is not a number: " + value);
    }

    @Override
    public String toString() {
        return (isPlus ? " + " : " - ") + right.toString();
    }

}
