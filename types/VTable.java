package types;

import java.util.LinkedHashMap;
import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.nio.charset.StandardCharsets;

public class VTable {

    public LinkedHashMap <String, VTableInfo> VTablesHMap;

    public VTable() {
        VTablesHMap = new LinkedHashMap<String, VTableInfo>();
    }

    public boolean createVTable(File llvmFile, SymbolTable sTable) {

        //write first lines of llvm file about v-tables and boilerplate code
        try {
            FileWriter fw = new FileWriter(llvmFile);
            PrintWriter pw = new PrintWriter(fw);
            ArrayList<String> classKeys = new ArrayList<String>(VTablesHMap.keySet());
            Collections.reverse(classKeys);
            for (String classStr : classKeys) {
                
                VTableInfo vInfo = VTablesHMap.get(classStr);
                Integer numMethods = vInfo.methodsVTable.size();
                pw.printf("@.%s_vtable = global [%d x i8*] [", classStr, numMethods);
                if (numMethods != 0) {
                    pw.print("\n");
                }
                for (String methodStr : vInfo.methodsVTable.keySet() ) {
                                        
                    String[] parentName = new String[1];
                    parentName[0] = "";
                    FunInfo finfo = sTable.lookupMethod(classStr, methodStr, parentName);
                    if (finfo == null) {
                        pw.close();
                        System.out.print("Something went wrong while searching in symbol table for " + methodStr +
                                         " method of class " + classStr);    
                        return false;   //something went wrong
                    }
                    
                    String retType = finfo.return_type;
                    if (retType == "int") {
                        retType = "i32";
                    }else if (retType == "boolean"){
                        retType = "i1";
                    }else if (retType == "int[]") {
                        retType = "i32*";
                    }else {
                        retType = "i8*";
                    }

                    String argsString = "";
                    for (String funArg : finfo.arg_types.values()) {
                        if (funArg == "int") {
                            argsString = argsString + ",i32";
                        }else if (funArg == "boolean") {
                            argsString = argsString + ",i1";
                        }else if (funArg == "int[]") {
                            argsString = argsString + ",i32*";
                        }else {
                            argsString = argsString + ",i8*";
                        }
                    }
                    argsString = "i8*" + argsString;
                    if (parentName[0] == "") {
                        pw.printf("\ti8* bitcast (%s (%s)* @%s.%s to i8*),\n", retType, argsString, classStr, methodStr);   
                    }else {
                        pw.printf("\ti8* bitcast (%s (%s)* @%s.%s to i8*),\n", retType, argsString, parentName[0], methodStr);

                    }
                }
                pw.print("]\n\n");
            }
            
            String text = new String(Files.readAllBytes(Paths.get("boilerplateCode.ll")), StandardCharsets.UTF_8);
            pw.print(text);
            pw.close();

        }catch (IOException ex) {
            System.out.println(ex.getMessage());
            return false;
        }        
        return true;
    }

    public Integer findOffset(String className, String field) {

        VTableInfo vinfo = VTablesHMap.get(className);
        return vinfo.fieldsVTable.get(field);
    }

    public Integer findMethodOffset(String className, String methodName) {
        VTableInfo vinfo = VTablesHMap.get(className);
        return vinfo.methodsVTable.get(methodName);
    }

    public Integer getSizeOfObj(String className, SymbolTable sTable) {

        
        if (!VTablesHMap.containsKey(className)) {
            return -1;
        }
        VTableInfo vInfo = VTablesHMap.get(className);
        ArrayList<Integer> vals = new ArrayList<Integer>();
        vals.addAll(vInfo.fieldsVTable.values());
        
        Integer size;
        if (vals.size() != 0) {
            size = 8 + vals.get(vals.size() - 1);
            ClassInfo cInfo = sTable.hmap.get(className);
            ArrayList<String> fields = new ArrayList<String>();
            fields.addAll(vInfo.fieldsVTable.keySet());
            String lastVar = fields.get(fields.size() - 1);
            String type = cInfo.class_vars.get(lastVar);
            if (type == "int") {
                size = size + 4;
            }else if ( type == "boolean"){
                size = size + 1;
            }else {
                size = size + 8;
            }
        }
        else
            size = 8;
        

        return size;
    }
}