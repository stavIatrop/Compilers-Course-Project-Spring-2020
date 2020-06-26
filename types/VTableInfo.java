package types;

import java.util.LinkedHashMap;

public class VTableInfo {
    
    boolean isMainClass;
    public LinkedHashMap <String, Integer> fieldsVTable;
    public LinkedHashMap <String, Integer> methodsVTable;

    public VTableInfo(boolean ismainclass) {
        isMainClass = ismainclass;
        fieldsVTable = new LinkedHashMap<String, Integer>();
        methodsVTable = new LinkedHashMap<String, Integer>();
    }
}