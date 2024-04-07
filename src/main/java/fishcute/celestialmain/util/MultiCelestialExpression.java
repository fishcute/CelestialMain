package fishcute.celestialmain.util;

import celestialexpressions.Module;
import celestialexpressions.*;
import fishcute.celestialmain.sky.CelestialSky;
import kotlin.jvm.functions.Function0;
import org.jetbrains.annotations.NotNull;

import java.util.HashMap;

public class MultiCelestialExpression extends CelestialExpression {
    public Expression expression;

    public int index = 0;

    public MultiCelestialExpression(String input, String location, Module... modules) {
        super(location);
        ExpressionContext context = new ExpressionContext();
        try {
            //TODO
            context.addModule(CelestialModuleKt.getModule());
            context.addModule(CelestialSky.variableModule);
            for (Module module : modules) {
                if (module != null) context.addModule(module);
            }
            this.expression = ExpressionCompiler.compile(input, context);
        } catch (Exception e) {
            Util.sendCompilationError(e.getMessage(), this.localLocation, e);

            this.expression = () -> 0.0;
        }
    }
    public float invoke() {
        try {
            return this.expression.invoke().floatValue();
        } catch (Exception e) {
            Util.sendError(e.getMessage(), this.localLocation, e);
            return 0.0f;
        }
    }

    public int invokeInt() {
        try {
            return this.expression.invoke().intValue();
        } catch (Exception e) {
            Util.sendError(e.getMessage(), this.localLocation, e);
            return 0;
        }
    }
    public static abstract class MultiDataModule extends Module {

        final protected IndexSupplier indexSupplier;
        
        public double getIndex() {
            return this.indexSupplier.getIndex();
        }

        protected MultiDataModule(@NotNull String name, @NotNull VariableList variables, @NotNull FunctionList functions, @NotNull IndexSupplier indexSupplier, HashMap<String, Function0<Double>> populateVars) {
            super(name, variables, functions);
            this.indexSupplier = indexSupplier;
        }

        public MultiDataModule(@NotNull String name, @NotNull HashMap<String, Function0<Double>> variables, IndexSupplier indexSupplier) {
            super(
                    name,
                    new VariableList(variables),
                    new FunctionList()
            );

            this.indexSupplier = indexSupplier;
        }

        @FunctionalInterface
        public interface IndexSupplier {
            Double getIndex();
        }

        @Override
        public boolean hasVariable(String name) {
            return this.getVariables().hasVariable(name);
        }

        @NotNull
        @Override
        public Function0<Double> getVariable(@NotNull String name) {
            return this.getVariables().getVariable(name);
        }
    }
}
