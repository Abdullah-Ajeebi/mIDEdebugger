package mlogdebugger;

import mindustry.world.blocks.logic.LogicBlock.LogicBuild;
import java.util.*;

public class ProcessorSession {
    public final LogicBuild logicBuild;
    public ExecutorInspector inspector;

    public ProcessorSession(Object buildObj) {
        this.logicBuild = (LogicBuild) buildObj;
        this.inspector = new ExecutorInspector(this.logicBuild.executor);
    }

    public String getCode() {
        return (logicBuild.code == null) ? "" : logicBuild.code;
    }

    public void setCode(String newCode) {
        logicBuild.configure(newCode); // Formally updates the processor code in engine!
    }

    public void setVariable(String name, double value) {
        Object lvar = inspector.findVariable(name);
        if (lvar != null) inspector.setVariable(lvar, value);
    }

    public List<ExecutorInspector.VariableInfo> getAllVariables() {
        List<ExecutorInspector.VariableInfo> result = new ArrayList<>();
        Object[] vars = inspector.getVariables();
        if(vars != null) {
            for (Object var : vars) {
                result.add(inspector.getVariableInfo(var));
            }
        }
        return result;
    }
}