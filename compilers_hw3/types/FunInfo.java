package types;

import java.util.LinkedHashMap;

public class FunInfo  {		//structure of a declared function's info

    public String return_type;
    public LinkedHashMap<String, String> arg_types;
    public boolean isOverriding;
    public LinkedHashMap<String, String> fun_vars;

    public FunInfo( String rettype, boolean isover) {
        return_type = rettype;
        arg_types = new LinkedHashMap<String, String>();
        isOverriding = isover;
        fun_vars = new LinkedHashMap<String, String>();
    }
}