package org.graylog.plugins.pipelineprocessor.codegen;

import com.google.common.base.Strings;
import com.google.common.collect.Lists;

import net.bytebuddy.ByteBuddy;
import net.bytebuddy.description.modifier.TypeManifestation;
import net.bytebuddy.description.modifier.Visibility;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.dynamic.DynamicType;
import net.bytebuddy.implementation.ExceptionMethod;
import net.bytebuddy.implementation.FixedValue;
import net.bytebuddy.implementation.Implementation;
import net.bytebuddy.implementation.bytecode.ByteCodeAppender;
import net.bytebuddy.implementation.bytecode.StackManipulation;
import net.bytebuddy.implementation.bytecode.constant.IntegerConstant;
import net.bytebuddy.implementation.bytecode.member.MethodReturn;

import org.graylog.plugins.pipelineprocessor.EvaluationContext;
import org.graylog.plugins.pipelineprocessor.ast.Rule;
import org.graylog.plugins.pipelineprocessor.ast.RuleAstBaseListener;
import org.graylog.plugins.pipelineprocessor.ast.RuleAstWalker;
import org.graylog.plugins.pipelineprocessor.ast.expressions.BooleanExpression;
import org.graylog.plugins.pipelineprocessor.ast.expressions.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.IdentityHashMap;
import java.util.List;

import static net.bytebuddy.matcher.ElementMatchers.named;
import static net.bytebuddy.matcher.ElementMatchers.returns;
import static net.bytebuddy.matcher.ElementMatchers.takesArguments;
import static org.graylog.plugins.pipelineprocessor.EvaluationContext.emptyContext;

public class ByteCodeGenerator implements CodeGenerator {

    public CompiledRule getByteCode(Rule rule) {
        final ByteBuddyVisitor visitor = new ByteBuddyVisitor();
        new RuleAstWalker().walk(visitor, rule);

        return CompiledRule.builder()
                .name(visitor.getClassName())
                .byteCode(visitor.getByteCode())
                .build();
    }

    @Override
    public Class<? extends GeneratedRule> generateCompiledRule(Rule rule, PipelineClassloader ruleClassloader) {
        final CompiledRule compiledRule = getByteCode(rule);

        ruleClassloader.defineClass(compiledRule.name(), compiledRule.byteCode());
        try {
            //noinspection unchecked
            return (Class<? extends GeneratedRule>) ruleClassloader.loadClass(compiledRule.name());
        } catch (ClassNotFoundException e) {
            return null;
        }
    }

    private static class ByteBuddyVisitor extends RuleAstBaseListener {
        private static final Logger LOG = LoggerFactory.getLogger(ByteBuddyVisitor.class);

        /**
         * The resulting byte code for the class.
         */
        private byte[] byteCode;

        /**
         * The name of the rule class.
         */
        private String name;

        private ByteBuddy bb = new ByteBuddy();

        private DynamicType.Builder<? extends GeneratedRule> classBuilder;

        private DynamicType.Builder.MethodDefinition.ImplementationDefinition<? extends GeneratedRule> when;

        private DynamicType.Builder.MethodDefinition.ImplementationDefinition<? extends GeneratedRule> then;

        /**
         * The list of currently generated statements for each method.
         */
        private List<ByteCodeAppender> code;

        private IdentityHashMap<Expression, ByteCodeAppender> blocks = new IdentityHashMap<>();

        /**
         * Only used for tracing tree visits (see {@link #enterEveryExpression(Expression)} and {@link #exitEveryExpression(Expression)})
         */
        private int level = 0;

        public byte[] getByteCode() {
            return byteCode;
        }

        public String getClassName() {
            return name;
        }

        @Override
        public void enterRule(Rule rule) {
            name = "rule$" + rule.id();
            classBuilder = bb.subclass(GeneratedRule.class)
                    .name("org.graylog.plugins.pipelineprocessor.$dynamic.rules." + name)
                    .modifiers(Visibility.PUBLIC, TypeManifestation.FINAL)
                    .method(named("name").and(returns(String.class)))
                    .intercept(FixedValue.value(rule.name()));
        }

        @Override
        public void exitRule(Rule rule) {
            final DynamicType.Unloaded<? extends GeneratedRule> unloaded = classBuilder.make();
            if (LOG.isTraceEnabled()) {
                try {
                    unloaded.saveIn(Paths.get("target").toFile().getAbsoluteFile());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            byteCode = unloaded.getBytes();
        }

        @Override
        public void enterWhen(Rule rule) {
            when = classBuilder.method(named("when")
                    .and(returns(boolean.class))
                    .and(takesArguments(EvaluationContext.class)));
            code = Lists.newArrayList();
        }

        @Override
        public void exitWhen(Rule rule) {
            final ByteCodeAppender whenCode = blocks.getOrDefault(rule.when(), new ByteCodeAppender.Simple(StackManipulation.Illegal.INSTANCE));
            code.add(new ByteCodeAppender.Compound(whenCode,
                    new ByteCodeAppender.Simple(MethodReturn.REFERENCE))
            );
            classBuilder = when.intercept(new Implementation.Simple(code.toArray(new ByteCodeAppender[0])));
        }

        @Override
        public void enterThen(Rule rule) {
            then = classBuilder.method(named("then")
                    .and(returns(TypeDescription.VOID))
                    .and(takesArguments(EvaluationContext.class)));
            code = Lists.newArrayList();
        }

        @Override
        public void exitThen(Rule rule) {
            classBuilder = then.intercept(ExceptionMethod.throwing(IllegalStateException.class, "Not yet implemented."));
        }

        @Override
        public void enterEveryExpression(Expression expr) {
            LOG.trace("IN  {}{}", Strings.padStart("|", level, '-') + ">", expr.nodeType());
            level++;
        }

        @Override
        public void exitEveryExpression(Expression expr) {
            level--;
            LOG.trace("OUT {}{}", Strings.padStart("|", level, '-') + ">", expr.nodeType());
        }

        @Override
        public void exitBoolean(BooleanExpression expr) {
            blocks.put(expr, new ByteCodeAppender.Simple(
                    IntegerConstant.forValue(expr.evaluateBool(emptyContext()))
            ));
        }
    }
}
