package com.zwz.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class SpyCollect {
    private static Map<String, List<String>> threadName2Info = new ConcurrentHashMap<>();
    private static Map<String, Map<String, Long>> threadName2StartTime = new HashMap<>();
    private static Map<String, Map<String, Long>> threadName2EndTime = new HashMap<>();

    public static void saveInput(String argInfo, String printDesc, String threadName) {
        String detail = printDesc + " : " + argInfo;
        if (threadName2Info.containsKey(threadName)) {
            threadName2Info.get(threadName).add(detail);
        } else {
            ArrayList<String> infos = new ArrayList<>();
            infos.add(detail);
            threadName2Info.put(threadName, infos);
        }
    }

    public static void saveOutput(String threadName, String argInfo) {
        threadName2Info.get(threadName).add(argInfo);
    }

    public static void setStartTime(String threadName, String methodName, long time) {
        if (threadName2StartTime.containsKey(threadName)) {
            threadName2StartTime.get(threadName).put(methodName, time);
        } else {
            Map<String, Long> methodName2StartTime = new HashMap<>();
            methodName2StartTime.put(methodName, time);
            threadName2StartTime.put(threadName, methodName2StartTime);
        }
    }

    public static void setEndTime(String threadName, String methodName, long time) {
        if (threadName2EndTime.containsKey(threadName)) {
            threadName2EndTime.get(threadName).put(methodName, time);
        } else {
            Map<String, Long> methodName2EndTime = new HashMap<>();
            methodName2EndTime.put(methodName, time);
            threadName2EndTime.put(threadName, methodName2EndTime);
        }
        getCostTime(threadName, methodName);
    }

    private static void getCostTime(String threadName, String methodName) {
        Long start = threadName2StartTime.get(threadName).get(methodName);
        Long end = threadName2EndTime.get(threadName).get(methodName);

        DecimalFormat df = new DecimalFormat("#.0000");
        String format = df.format((end - start));

        String info = "[ " + format + " ms ] " + methodName;
        threadName2Info.get(threadName).add(info);
    }

    public static void writeInfo2File(String threadName, String path) {
        File outputFile = Paths.get(path, threadName + "#" + System.currentTimeMillis()).toFile();
        List<String> methodInfo = threadName2Info.remove(threadName);
        doWriteOutputFile(outputFile, methodInfo);
    }

    private static void doWriteOutputFile(File outputFile, List<String> methodInfo) {
        if (!outputFile.getParentFile().exists()) {
            outputFile.getParentFile().mkdirs();
        }
        try (FileOutputStream fOut = new FileOutputStream(outputFile);
             ObjectOutputStream out = new ObjectOutputStream(fOut)) {
            out.writeObject(methodInfo);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void writeErrorInfo2File(String context, String path) {
        File outputFile = Paths.get(path, "error#" + System.currentTimeMillis()).toFile();
        List<String> infos = new ArrayList<>();
        infos.add(context);
        doWriteOutputFile(outputFile, infos);
    }
}
