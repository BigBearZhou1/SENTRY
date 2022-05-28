package com.zwz;

import com.zwz.transformer.SpyTransformer;

import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;

public class SpyAgentMain {
    public static void agentmain(String agentArgs, Instrumentation inst){
        String[] args = agentArgs.split(",");
        String className = args[0];
        String methodName = args[1];
        String outPutPath = args[2];
        SpyTransformer spyTransformer = new SpyTransformer(className, methodName, outPutPath);
        inst.addTransformer(spyTransformer,true);
        try {
            inst.retransformClasses(getClassByRedefinePath(className));
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    private static Class<?> getClassByRedefinePath(String targetClassName) throws ClassNotFoundException {
        return Class.forName(targetClassName);
    }
}
