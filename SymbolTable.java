import java.util.HashMap;

public class SymbolTable {


	public String enter() {			//enters a new scope level, creates new table

		return "";
	}

	public String insert(String name, String name_info) {	//creates entry for name in current scope, adds at current level

		return "";
	}

	public String lookup(String name) {		//lookup a name, return(?) an entry			

		return "";
	}

}

class ScopeStruct {		//structure of each scope's hashmap

	HashMap<String, NameInfo> hm;


	public ScopeStruct() {
		hm = new HashMap<String, NameInfo>();
	}	
}

class NameInfo {

	String type_id;		//identifier of type var, class or function

}

class VarInfo extends NameInfo{		//structure of a declared variable's info
	
	String type;	//int or boolean

}

class ClassInfo extends NameInfo {	//structure of a declared class' info
	
	HashMap<String, NameInfo> class_scope;

}

class FunInfo extends Nameinfo {		//structure of a declared function's info

	String return_type;
	String[] arg_types;
	boolean isVirtual;
}
