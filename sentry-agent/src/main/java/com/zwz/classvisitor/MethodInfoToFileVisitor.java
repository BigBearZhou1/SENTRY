package com.zwz.classvisitor;

import com.zwz.methodvisitor.MethodInfoToFileAdapter;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodInfoToFileVisitor extends ClassVisitor {
    private String methodName;
    private String outPutPath;

    public MethodInfoToFileVisitor(int api, ClassVisitor cv, String methodName, String outPutPath) {
        super(api,cv);
        this.methodName = methodName;
        this.outPutPath = outPutPath;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;

        if(isAbstractMethod){
            return mv;//需要返回错误给客户端
        }

        boolean isNativeMethod = (access & ACC_NATIVE) != 0;
        if(isNativeMethod){
            return mv;
        }

        if(mv!=null && name.equals(methodName)){
            mv = new MethodInfoToFileAdapter(api,mv,outPutPath);
        }
        return mv;
    }
}
