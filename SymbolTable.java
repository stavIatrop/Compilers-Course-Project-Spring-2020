import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolTable {		//structure of each scope's hashmap

	LinkedHashMap<String, ClassInfo> hm;


	public SymbolTable() {
		hm = new LinkedHashMap<String, ClassInfo>();
	}	

}


// class NameInfo {

// 	String type_id;		//identifier of type var, class or function

// 	public NameInfo(String typeid) {

// 		type_id = typeid;
// 	}
// }

class ClassInfo  {	//structure of a declared class' info
	
	LinkedHashMap<String, VarInfo> class_vars;
	LinkedHashMap<String, FunInfo> class_methods;
	String parentClass;
	ArrayList<String> children;

	public ClassInfo() {

		class_vars = new LinkedHashMap<String, VarInfo>();
		class_methods = new LinkedHashMap<String, FunInfo>();
		children = new ArrayList<String>();
	}

}

class VarInfo {		//structure of a declared variable's info
	
	String type;	//int or boolean
	String value;	//might not be needed

	public VarInfo( String tp, String val) {
		type = tp;
		value = val;
	}

}

class FunInfo  {		//structure of a declared function's info

	String return_type;
	ArrayList<String> arg_types;
	boolean isVirtual;

	public FunInfo( String rettype, ArrayList<String> argtypes,boolean isvirtual) {
		return_type = rettype;
		arg_types = new ArrayList<String>();
		for (String str : argtypes){
			arg_types.add(str);
		}
		isVirtual = isvirtual;
	}
}

class VarArrayInfo {		//structure of a declared array's info
	
	String type;	//int[] or boolean[]
	int size;
	ArrayList<String> values;

	public VarArrayInfo( String tp, int sz, ArrayList<String> vals) {
		type = tp;
		size = sz;
		values = new ArrayList<String>();
		for (String str : vals) {
			
			values.add(str);
		}
	}

}

