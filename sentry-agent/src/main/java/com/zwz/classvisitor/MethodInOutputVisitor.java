package com.zwz.classvisitor;

import org.objectweb.asm.ClassVisitor;

public class MethodInOutputVisitor extends ClassVisitor {
    private String methodName;
    private String outPutPath;

    public MethodInOutputVisitor(int api, ClassVisitor cv, String methodName, String outPutPath) {
        super(api, cv);
        this.methodName = methodName;
        this.outPutPath = outPutPath;
    }

}
