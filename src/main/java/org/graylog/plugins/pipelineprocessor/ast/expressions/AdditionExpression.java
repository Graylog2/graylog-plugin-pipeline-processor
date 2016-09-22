package org.graylog.plugins.pipelineprocessor.ast.expressions;

import org.antlr.v4.runtime.Token;
import org.graylog.plugins.pipelineprocessor.EvaluationContext;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicReference;

import static com.google.common.base.MoreObjects.firstNonNull;

public class AdditionExpression extends BinaryExpression implements NumericExpression {
    private final boolean isPlus;
    private AtomicReference<Class> type = new AtomicReference<>();

    public AdditionExpression(Token start, Expression left, Expression right, boolean isPlus) {
        super(start, left, right);
        this.isPlus = isPlus;
    }

    @Override
    public boolean isIntegral() {
        return getType().equals(Long.class);
    }

    @Override
    public long evaluateLong(EvaluationContext context) {
        return (Long) firstNonNull(evaluateUnsafe(context), 0);
    }

    @Override
    public double evaluateDouble(EvaluationContext context) {
        return (Double) firstNonNull(evaluateUnsafe(context), 0);
    }

    @Nullable
    @Override
    public Object evaluateUnsafe(EvaluationContext context) {
        final NumericExpression left = (NumericExpression) this.left;
        final NumericExpression right = (NumericExpression) this.right;

        if (isIntegral()) {
            final long l = left.evaluateLong(context);
            final long r = right.evaluateLong(context);
            if (isPlus) {
                return l + r;
            } else {
                return l - r;
            }
        } else {
            final double l = left.evaluateDouble(context);
            final double r = right.evaluateDouble(context);
            if (isPlus) {
                return l + r;
            } else {
                return l - r;
            }
        }
    }

    @Override
    public Class getType() {
        final Class theType = type.get();
        if (theType != null) {
            return theType;
        }
        final NumericExpression left = (NumericExpression) this.left;
        final NumericExpression right = (NumericExpression) this.right;

        // double + double = double, long + long = long, the other cases are caught by the type checker
        if (left.isIntegral()) {
            if (right.isIntegral()) {
                type.set(Long.class);
            } else {
                type.set(Double.class);
            }
        } else {
            type.set(Double.class);
        }

        return type.get();
    }

    @Override
    public String toString() {
        return left.toString() + (isPlus ? " + " : " - ") + right.toString();
    }
}
