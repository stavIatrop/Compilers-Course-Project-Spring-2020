import java.util.ArrayList;
import java.util.HashMap;

public class SymbolTable {		//structure of each scope's hashmap

	HashMap<String, NameInfo> hm;


	public ScopeStruct() {
		hm = new HashMap<String, NameInfo>();
	}	

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


class NameInfo {

	String type_id;		//identifier of type var, class or function

	public NameInfo(String typeid) {

		type_id = typeid;
	}
}

class VarInfo extends NameInfo{		//structure of a declared variable's info
	
	String type;	//int or boolean
	String value;

	public VarInfo(String typeid, String tp, String val) {
		super("var");
		type = tp;
		value = val;
	}

}


class VarArrayInfo extends NameInfo{		//structure of a declared array's info
	
	String type;	//int[] or boolean[]
	int size;
	ArrayList<String> values;

	public VarArrayInfo(String typeid, String tp, int sz, ArrayList<String> vals) {
		super("var");
		type = tp;
		size = sz;
		values = new ArrayList<String>();
		for (String str : vals) {
			
			values.add(str);
		}
	}

}


class ClassInfo extends NameInfo {	//structure of a declared class' info
	
	HashMap<String, NameInfo> class_scope;

	public ClassInfo(String typeid) {
		super("class");
		class_scope = new HashMap<String, NameInfo>();
	}

}

class FunInfo extends Nameinfo {		//structure of a declared function's info

	String return_type;
	ArrayList<String> arg_types;
	boolean isVirtual;

	public FunInfo(String typeid, String rettype, ArrayList<String> argtypes,boolean isvirtual) {
		super("function");
		return_type = rettype;
		arg_types = new ArrayList<String>();
		for (String str : argtypes){
			arg_types.add(str);
		}
		isVirtual = isvirtual;
	}
}
