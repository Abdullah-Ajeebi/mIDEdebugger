package mlogdebugger;

import mindustry.mod.Mod;
import arc.util.Log;

public class MlogDebuggerMod extends Mod {

    public static final String NAME = "mIDE-debugger";
    public static final String VERSION = "1.0";

    private static ProcessorSession currentSession;
    private static DebuggerServer server;

    public MlogDebuggerMod() {
        super();
    }

    @Override
    public void init() {
        server = new DebuggerServer(9999);
        server.start(); // This runs in the background!
        Log.info("[mIDE-Debugger] Initialized and TCP server started on port 9999.");
    }

    public static void selectProcessor(Object logicBuild) {
        if (!ProcessorScanner.isLogicBuild(logicBuild)) {
            Log.err("[mIDE-Debugger] Not a logic build!");
            return;
        }

        currentSession = new ProcessorSession(logicBuild);
        ProcessorScanner.ProcessorInfo info = ProcessorScanner.getProcessorInfo(logicBuild);
        Log.info("[mIDE-Debugger] Selected: " + info.toString());
    }

    public static ProcessorSession getCurrentSession() {
        return currentSession;
    }
}