package com.zwz.transformer;


import com.zwz.classvisitor.MethodInOutputVisitor;
import com.zwz.classvisitor.MethodInfoToFileVisitor;
import com.zwz.classvisitor.MethodTimerVisitor;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.ClassReader;
import org.objectweb.asm.Opcodes;

import java.io.File;
import java.io.FileOutputStream;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.nio.file.Paths;
import java.security.ProtectionDomain;

public class SpyTransformer implements ClassFileTransformer {
    public static final String CLASS_FILE = "classFile";
    private String clzName;
    private String methodName;
    private String outPutPath;

    public SpyTransformer(String cn, String mn, String path) {
        this.clzName = cn;
        this.methodName = mn;
        this.outPutPath = path;
    }

    @Override
    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) throws IllegalClassFormatException {
        className = className.replace("/", ".");
        if (className.equals(clzName)) {
            //将原先的字节码文件写入spy/classfile目录下暂存，以便日后恢复
            writeClassBufferToFile(classfileBuffer);

            //（1）构建ClassReader
            ClassReader cr = new ClassReader(classfileBuffer);

            //(2) 构建ClassWriter
            ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_FRAMES);
            try{
                //(3) 串联ClassVisitor
                int api = Opcodes.ASM9;
                MethodInfoToFileVisitor outFileCV = new MethodInfoToFileVisitor(api, cw, methodName, outPutPath);
                MethodInOutputVisitor methodInOutCV = new MethodInOutputVisitor(api, outFileCV, methodName, outPutPath);
                MethodTimerVisitor timerCV = new MethodTimerVisitor(api, methodInOutCV, methodName);

                //(4) 结合ClassReader和ClassVisitor
                int parsingOptions = ClassReader.SKIP_DEBUG|ClassReader.SKIP_FRAMES;
                cr.accept(timerCV,parsingOptions);
                return cw.toByteArray();
            }catch (Exception e){
                e.printStackTrace();
            }
        }
        return classfileBuffer;
    }

    private void writeClassBufferToFile(byte[] classfileBuffer) {
        File classFile = Paths.get(outPutPath, CLASS_FILE, clzName).toFile();
        if (!classFile.exists()) {
            classFile.getParentFile().mkdirs();
        }
        try (FileOutputStream out = new FileOutputStream(classFile)) {
            out.write(classfileBuffer);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
