package mlogdebugger;

import mindustry.Vars;
import mindustry.gen.Building;
import mindustry.gen.Groups;
import mindustry.world.blocks.logic.LogicBlock.LogicBuild;
import java.util.ArrayList;
import java.util.List;

public class ProcessorScanner {

    public static List<Object> getAllProcessors() {
        List<Object> result = new ArrayList<>();
        // Must be actively in a map/server for this to work
        if(Vars.state == null || !Vars.state.isPlaying() || Groups.build == null) {
            return result;
        }

        // Just ask the game engine for all buildings
        for (Building b : Groups.build) {
            if (b instanceof LogicBuild) {
                result.add(b);
            }
        }
        return result;
    }

    public static boolean isLogicBuild(Object obj) {
        return obj instanceof LogicBuild;
    }

    public static ProcessorInfo getProcessorInfo(Object obj) {
        if (!(obj instanceof LogicBuild)) {
            return new ProcessorInfo(0, 0, false, "unknown", 0);
        }

        LogicBuild logic = (LogicBuild) obj;
        int instructionCount = (logic.executor != null && logic.executor.instructions != null) ? logic.executor.instructions.length : 0;

        return new ProcessorInfo(logic.tileX(), logic.tileY(), logic.enabled, logic.team.name, instructionCount);
    }

    public static class ProcessorInfo {
        public final int x;
        public final int y;
        public final boolean enabled;
        public final String team;
        public final int instructionCount;

        public ProcessorInfo(int x, int y, boolean enabled, String team, int instructionCount) {
            this.x = x; this.y = y; this.enabled = enabled;
            this.team = team; this.instructionCount = instructionCount;
        }

        @Override
        public String toString() {
            return String.format("Processor at (%d, %d) [%s] - %d ops", x, y, team, instructionCount);
        }
    }
}