package mlogdebugger;

import java.io.*;
import java.net.*;
import java.util.List;

public class DebuggerServer extends Thread {
    private final int port;

    public DebuggerServer(int port) {
        this.port = port;
        setDaemon(true); // Don't block game shutdown
    }

    @Override
    public void run() {
        try (ServerSocket serverSocket = new ServerSocket(port)) {
            while (!Thread.currentThread().isInterrupted()) {
                try (Socket socket = serverSocket.accept();
                     BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
                     PrintWriter out = new PrintWriter(socket.getOutputStream(), true)) {

                    String line;
                    // The Fix: Stay on the line for consecutive mIDE queries!
                    while ((line = in.readLine()) != null) {
                        String[] parts = line.split(" ", 2);
                        String command = parts[0];
                        String arg = (parts.length > 1) ? parts[1] : "";

                        ProcessorSession session = MlogDebuggerMod.getCurrentSession();
                        boolean requiresSession = !command.equals("LIST_PROCESSORS") && !command.equals("SELECT_PROCESSOR");

                        if (requiresSession && session == null) {
                            out.println("ERROR: No active session");
                            out.println("END");
                            continue;
                        }

                        switch (command) {
                            case "GET_VARS":
                                List<ExecutorInspector.VariableInfo> vars = session.getAllVariables();
                                for (ExecutorInspector.VariableInfo var : vars) {
                                    out.println(var.name + ":" + var.value);
                                }
                                out.println("END");
                                break;

                            case "SET_VAR":
                                String[] varArgs = arg.split(" ", 2);
                                if (varArgs.length >= 2) {
                                    session.setVariable(varArgs[0], Double.parseDouble(varArgs[1]));
                                    out.println("OK");
                                } else {
                                    out.println("ERROR: Missing arguments");
                                }
                                out.println("END");
                                break;

                            case "GET_CODE":
                                out.println(session.getCode().replace("\n", "\\n"));
                                out.println("END");
                                break;

                            case "SET_CODE":
                                session.setCode(arg.replace("\\n", "\n"));
                                out.println("OK");
                                out.println("END");
                                break;

                            case "LIST_PROCESSORS":
                                List<Object> processors = ProcessorScanner.getAllProcessors();
                                for (int i = 0; i < processors.size(); i++) {
                                    out.println(i + ":" + ProcessorScanner.getProcessorInfo(processors.get(i)));
                                }
                                if (processors.isEmpty()) out.println("0:No processors found on map");
                                out.println("END");
                                break;

                            case "SELECT_PROCESSOR":
                                try {
                                    int index = Integer.parseInt(arg);
                                    List<Object> processorsList = ProcessorScanner.getAllProcessors();
                                    if (index >= 0 && index < processorsList.size()) {
                                        MlogDebuggerMod.selectProcessor(processorsList.get(index));
                                        out.println("OK");
                                    } else {
                                        out.println("ERROR: Invalid index");
                                    }
                                } catch (NumberFormatException e) {
                                    out.println("ERROR: Invalid format");
                                }
                                out.println("END");
                                break;

                            default:
                                out.println("ERROR: Unknown command");
                                out.println("END");
                                break;
                        }
                    }
                } catch (Exception e) {
                    // Ignore client dropouts gracefully
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}