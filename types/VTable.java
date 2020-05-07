package types;

import java.util.LinkedHashMap;

public class VTable {

    public LinkedHashMap <String, VTableInfo> VTablesHMap;

    public VTable() {
        VTablesHMap = new LinkedHashMap<String, VTableInfo>();
    }

}