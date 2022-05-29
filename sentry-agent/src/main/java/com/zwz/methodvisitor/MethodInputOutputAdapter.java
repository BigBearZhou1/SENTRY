package com.zwz.methodvisitor;

import com.zwz.util.ASMTypeUtil;
import org.objectweb.asm.Type;
import org.objectweb.asm.MethodVisitor;
import sun.swing.StringUIClientPropertyKey;

import static org.objectweb.asm.Opcodes.*;

public class MethodInputOutputAdapter extends MethodVisitor {
    private final int methodAccess;
    private final String methodName;
    private final String methodDesc;
    private final String path;

    public MethodInputOutputAdapter(int api, MethodVisitor mv, int access, String name, String descriptor, String outPutPath) {
        super(api, mv);
        this.methodAccess = access;
        this.methodName = name;
        this.methodDesc = descriptor;
        this.path = outPutPath;
    }

    @Override
    public void visitCode() {
        super.visitFieldInsn(GETSTATIC, "java/lang/System", "out", "Ljava/io/PrintStream");
        super.visitLdcInsn("开始采集方法：" + methodName + "\n");
        super.visitMethodInsn(INVOKEVIRTUAL, "java/io/PrintStream", "println", "(Ljava/lang/String;)V", false);

        boolean isStatic = ((methodAccess & ACC_STATIC) != 0);
        int slotIndex = isStatic ? 0 : 1;

        Type methodType = Type.getMethodType(methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        if (argumentTypes.length == 0) {
            processVoidInput();
        }
        for (Type t : argumentTypes) {
            int sort = t.getSort();
            int size = t.getSize();
            String descriptor = t.getDescriptor();
            int opcode = t.getOpcode(ILOAD);

            super.visitVarInsn(opcode, slotIndex);
            if (sort >= Type.BOOLEAN && sort <= Type.BOOLEAN) {
                String owner = ASMTypeUtil.getZXbyTypeDesc(descriptor);
                String desc = "(" + descriptor + ")L" + owner + ";";
                super.visitMethodInsn(INVOKESTATIC, owner, "valueOf", desc, false);
                super.visitMethodInsn(INVOKEVIRTUAL, owner, "toString", "()Ljava/lang/String;", false);
            } else {
                super.visitMethodInsn(INVOKESTATIC, "com/alibaba/fastjson/JSON", "toJSONString", "(Ljava/lang/Object;)Ljava/lang/String;", false);
            }

            super.visitLdcInsn(t.getClassName());

            super.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);

            super.visitMethodInsn(INVOKESTATIC, "com/zwz/util/SpyCollect", "saveInput", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);

            slotIndex += size;
        }
        super.visitCode();
    }

    @Override
    public void visitInsn(int opcode) {
        if((opcode>=IRETURN && opcode<=RETURN)||opcode ==ATHROW){
            if(opcode <= DRETURN){
                Type methodType = Type.getMethodType(methodDesc);
                Type returnType = methodType.getReturnType();
                int size = returnType.getSize();
                String descriptor = returnType.getDescriptor();

                if(size == 1){
                    super.visitInsn(DUP);
                }else{
                    super.visitInsn(DUP2);
                }
                String owner = ASMTypeUtil.getZXbyTypeDesc(descriptor);
                String desc = "(" + descriptor + ")L" + owner + ";";
                super.visitMethodInsn(INVOKESTATIC, owner, "valueOf", desc, false);
                super.visitMethodInsn(INVOKEVIRTUAL, owner, "toString", "()Ljava/lang/String;", false);
            }else if(opcode == ARETURN){
                super.visitInsn(DUP);
                super.visitMethodInsn(INVOKESTATIC, "com/alibaba/fastjson/JSON", "toJSONString",
                        "(Ljava/lang/Object;)Ljava/lang/String;", false);
            }else if(opcode == RETURN){
                super.visitLdcInsn("void");
            }else{
                super.visitLdcInsn("abnormal has exception");
            }
            super.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
            super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);

            super.visitInsn(SWAP);

            super.visitMethodInsn(INVOKESTATIC, "com/zwz/util/SpyCollect", "saveOutput",
                    "(Ljava/lang/String;Ljava/lang/String;)V", false);
        }
        super.visitInsn(opcode);
    }

    private void processVoidInput() {
        super.visitLdcInsn("{}");
        super.visitLdcInsn("Void");
        super.visitMethodInsn(INVOKESTATIC, "java/lang/Thread", "currentThread", "()Ljava/lang/Thread;", false);
        super.visitMethodInsn(INVOKEVIRTUAL, "java/lang/Thread", "getName", "()Ljava/lang/String;", false);

        super.visitMethodInsn(INVOKESTATIC, "com/zwz/util/SpyCollect", "saveInput", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
    }
}
