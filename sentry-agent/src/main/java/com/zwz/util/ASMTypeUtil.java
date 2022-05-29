package com.zwz.util;

import java.util.HashMap;

public class ASMTypeUtil {
    static String[] typeDesc = {"Z", "C", "B", "S", "I", "F", "J", "D", "Ljava/lang/Object;", "Ljava/lang/String"};
    static String[] ZXTypes = {"java/lang/Boolean", "java/lang/Character", "java/lang/Byte", "java/lang/Short",
            "java/lang/Integer", "java/lang/Float", "java/lang/Long", "java/lang/Double"};

    static HashMap<String, String> type2ZxMap = new HashMap<>(16);

    static {
        for (int i = 0; i < 8; i++) {
            type2ZxMap.put(typeDesc[i], ZXTypes[i]);
        }
    }

    public static String getZXbyTypeDesc(String desc) {
        return type2ZxMap.getOrDefault(desc, "Ljava/lang/Object;");
    }
}
