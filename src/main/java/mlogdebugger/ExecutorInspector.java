package mlogdebugger;

import mindustry.logic.LExecutor;

public class ExecutorInspector {
    private final LExecutor executor;

    public ExecutorInspector(LExecutor executor) {
        this.executor = executor;
    }

    public Object[] getVariables() {
        return executor.vars;
    }

    public LExecutor.Var findVariable(String name) {
        if(executor.vars == null) return null;
        for(LExecutor.Var v : executor.vars) {
            if(v.name.equals(name)) return v;
        }
        return null;
    }

    public void setVariable(Object lvar, double value) {
        LExecutor.Var var = (LExecutor.Var) lvar;
        var.numval = value;
        var.isobj = false;
    }

    public VariableInfo getVariableInfo(Object obj) {
        LExecutor.Var lvar = (LExecutor.Var) obj;
        String type;
        Object displayValue;

        if (lvar.isobj) {
            displayValue = lvar.objval;
            type = (displayValue == null) ? "null" : displayValue.getClass().getSimpleName();
        } else {
            displayValue = lvar.numval;
            if (Double.isNaN(lvar.numval)) type = "NaN";
            else if (Double.isInfinite(lvar.numval)) type = "Infinity";
            else type = "number";
        }

        return new VariableInfo(lvar.name, displayValue, type, lvar.isobj, lvar.constant);
    }

    public static class VariableInfo {
        public final String name;
        public final Object value;
        public final String type;
        public final boolean isObject;
        public final boolean isConstant;

        public VariableInfo(String name, Object value, String type, boolean isObject, boolean isConstant) {
            this.name = name; this.value = value; this.type = type;
            this.isObject = isObject; this.isConstant = isConstant;
        }
    }
}