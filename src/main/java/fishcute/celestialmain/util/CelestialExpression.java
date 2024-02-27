package fishcute.celestialmain.util;

import celestialexpressions.Expression;
import celestialexpressions.ExpressionCompiler;
import celestialexpressions.ExpressionContext;
import fishcute.celestialmain.sky.CelestialSky;

public class CelestialExpression {
    public Expression expression;
    public final String localLocation;
    public CelestialExpression(String input, String location) {
        this.localLocation = location;
        try {
            var context = new ExpressionContext();
            context.addModule(CelestialModuleKt.getModule());
            context.addModule(CelestialSky.variableModule);
            this.expression = ExpressionCompiler.compile(input, context);
        } catch (Exception e) {
            Util.sendCompilationError(e.getMessage(), this.localLocation);
            this.expression = () -> 0.0;
        }
    }

    public CelestialExpression(String localLocation) {
        this.localLocation = localLocation;
    }
    public CelestialExpression(double d) {
        this.expression = () -> d;
        this.localLocation = "";
    }

    public float invoke() {
        try {
            return this.expression.invoke().floatValue();
        } catch (Exception e) {
            Util.sendError(e.getMessage(), this.localLocation);
            return 0.0f;
        }
    }
    public int invokeInt() {
        try {
            return this.expression.invoke().intValue();
        } catch (Exception e) {
            Util.sendError(e.getMessage(), this.localLocation);
            return 0;
        }
    }
}
