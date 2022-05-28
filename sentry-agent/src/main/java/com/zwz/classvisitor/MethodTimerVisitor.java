package com.zwz.classvisitor;

import org.objectweb.asm.ClassVisitor;

public class MethodTimerVisitor extends ClassVisitor{
    private String methodName;

    public MethodTimerVisitor(int api, ClassVisitor cv, String methodName) {
        super(api,cv);
        this.methodName = methodName;
    }
}
