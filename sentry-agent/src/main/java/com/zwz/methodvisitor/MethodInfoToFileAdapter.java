package com.zwz.methodvisitor;

import org.objectweb.asm.MethodVisitor;

import static org.objectweb.asm.Opcodes.*;

public class MethodInfoToFileAdapter extends MethodVisitor {
    private String outPutPath;

    public MethodInfoToFileAdapter(int api, MethodVisitor mv, String outPutPath) {
        super(api, mv);
        this.outPutPath = outPutPath;
    }

    @Override
    public void visitInsn(int opcode) {
        if ((opcode >= IRETURN && opcode <= RETURN) || opcode == ATHROW) {
            writeToFile();
        }
        super.visitInsn(opcode);
    }

    private void writeToFile() {
        mv.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        mv.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);
        mv.visitLdcInsn(outPutPath);
        mv.visitMethodInsn(INVOKESTATIC, "com/zwz/util/SpyCollect", "writeInfo2File", "(Ljava/lang/String;Ljava/lang/String;)V", false);
    }

}
