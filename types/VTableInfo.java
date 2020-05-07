import java.util.LinkedHashMap;

public class VTableInfo {
    
    public LinkedHashMap <String, Integer> fieldsVTable;
    public LinkedHashMap <String, Integer> methodsVTable;

    public VTableInfo() {
        fieldsVTable = new LinkedHashMap<String, Integer>();
        methodsVTable = new LinkedHashMap<String, Integer>();
    }
}