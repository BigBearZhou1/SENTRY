package com.zwz.classvisitor;

import com.zwz.methodvisitor.MethodInfoToFileAdapter;
import com.zwz.methodvisitor.MethodInputOutputAdapter;
import com.zwz.util.SpyCollect;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodInOutputVisitor extends ClassVisitor {
    private String methodName;
    private String outPutPath;
    private boolean isMethodExist;

    public MethodInOutputVisitor(int api, ClassVisitor cv, String methodName, String outPutPath) {
        super(api, cv);
        this.methodName = methodName;
        this.outPutPath = outPutPath;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;

        if (isAbstractMethod) {
            SpyCollect.writeErrorInfo2File("error: " + methodName + " is abstarct method", outPutPath);
            return mv;
        }

        boolean isNativeMethod = (access & ACC_NATIVE) != 0;
        if (isNativeMethod) {
            SpyCollect.writeErrorInfo2File("error: " + methodName + " is native method", outPutPath);
            return mv;
        }

        if (mv != null && name.equals(methodName)) {
            isMethodExist = true;
            mv = new MethodInputOutputAdapter(api, mv, access, name, descriptor, outPutPath);
        }
        return mv;
    }

    @Override
    public void visitEnd() {
        if (!isMethodExist) {
            SpyCollect.writeErrorInfo2File("error: " + methodName + " is not exist", outPutPath);
        }
        super.visitEnd();
    }
}
