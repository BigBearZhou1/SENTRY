package com.zwz.methodvisitor;

import jdk.nashorn.internal.runtime.regexp.joni.constants.OPCode;
import org.objectweb.asm.MethodVisitor;
import sun.dc.pr.PRError;

import static org.objectweb.asm.Opcodes.*;


public class MethodTimerAdapter extends MethodVisitor {
    private final String SET_END_TIME = "setEndTime";
    private final String SET_START_TIME = "setStartTime";
    private final String methodName;
    private String prevMethodName;

    public MethodTimerAdapter(int api, MethodVisitor mv, String methodName) {
        super(api, mv);
        this.methodName = methodName;
    }

    @Override
    public void visitCode() {
        setTime(SET_START_TIME, methodName);
        prevMethodName = methodName;
        super.visitCode();
    }

    @Override
    public void visitMethodInsn(int opcode, String owner, String name, String descriptor, boolean isInterface) {
        if (prevMethodName != null && !prevMethodName.equals(this.methodName)) {
            setTime(SET_END_TIME, prevMethodName);
        }

        String curMethodName = owner.replace("/", ".") + "@" + name;
        setTime(SET_START_TIME, curMethodName);
        prevMethodName = curMethodName;

        super.visitMethodInsn(opcode, owner, name, descriptor, isInterface);
    }


    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            registerEndTime();
        }

        super.visitInsn(opcode);
    }

    private void registerEndTime() {
        if (prevMethodName != null && !prevMethodName.equals(methodName)) {
            setTime(SET_END_TIME, prevMethodName);
        }
        setTime(SET_END_TIME, methodName);
    }

    private void setTime(String flag, String methodName) {
        super.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);

        super.visitLdcInsn(methodName);
        super.visitMethodInsn(INVOKESTATIC, "java/lang/System", "nanoTime", "()J", false);

        super.visitMethodInsn(INVOKESTATIC, "com/zwz/util/SpyCollect", flag, "(Ljava/lang/String;Ljava/lang/String;J)V", false);

    }
}
