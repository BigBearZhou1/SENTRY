package com.zwz.classvisitor;

import com.zwz.methodvisitor.MethodInputOutputAdapter;
import com.zwz.methodvisitor.MethodTimerAdapter;
import com.zwz.util.SpyCollect;
import org.objectweb.asm.ClassVisitor;
import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.ACC_ABSTRACT;
import static org.objectweb.asm.Opcodes.ACC_NATIVE;

public class MethodTimerVisitor extends ClassVisitor{
    private String methodName;

    public MethodTimerVisitor(int api, ClassVisitor cv, String methodName) {
        super(api,cv);
        this.methodName = methodName;
    }

    @Override
    public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, descriptor, signature, exceptions);
        boolean isAbstractMethod = (access & ACC_ABSTRACT) != 0;

        if (isAbstractMethod) {
            return mv;
        }

        boolean isNativeMethod = (access & ACC_NATIVE) != 0;
        if (isNativeMethod) {
            return mv;
        }

        if (mv != null && name.equals(methodName)) {
            mv = new MethodTimerAdapter(api, mv, methodName);
        }
        return mv;
    }
}
