import java.util.ArrayList;
import java.util.LinkedHashMap;

public class SymbolTable {		//structure of each scope's hashmap

	LinkedHashMap<String, ClassInfo> hmap;


	public SymbolTable() {
		hmap = new LinkedHashMap<String, ClassInfo>();
		System.out.println("Symbol Table created.");
	}
	
	public boolean enter(String parentClass, String className, boolean mainclass) {			//function for creating new class scope

		if (hmap.containsKey(className)) {		//class with the same name already declared
			return false;
		}
		
		hmap.put(className, new ClassInfo(parentClass, mainclass));
		if (parentClass != null) {	//insert child to parent class

			ClassInfo cinfo = hmap.get(parentClass);
			cinfo.children.add(className);
		}
		return true;
	}

	public boolean checkParent(String parentClass) {			//function for checking former declaration of parent class

		if (hmap.containsKey(parentClass)) {	//it means that parent class has been declared before its children
			return true;
		}
		return false;
	}

	public void printSTable() {

		for (String ClassStr : hmap.keySet() ) {

			System.out.println("Class: " + ClassStr);
			ClassInfo cinfo = hmap.get(ClassStr);
			System.out.println("Class variables:");
			for (String VarStr : cinfo.class_vars.keySet()) {

				VarInfo vinfo = cinfo.class_vars.get(VarStr);
				String type = vinfo.type;
				System.out.println(type + " " + VarStr);
			}
			System.out.println("Class methods: ");
			for (String MethodStr : cinfo.class_methods.keySet()) {

				FunInfo funinfo = cinfo.class_methods.get(MethodStr);
				String rettype = funinfo.return_type;
				System.out.print(rettype + " " + MethodStr + " " + "( ");
				for (String ParamStr : funinfo.arg_types.keySet()) {
					String type = funinfo.arg_types.get(ParamStr).type;
					System.out.print(type + " " + ParamStr + " ");
				}
				System.out.println(")");
			}
		}
	}

}

class ClassInfo  {	//structure of a declared class' info
	
	LinkedHashMap<String, VarInfo> class_vars;
	LinkedHashMap<String, FunInfo> class_methods;
	String parentClass;
	ArrayList<String> children;
	boolean isMain;

	public ClassInfo(String parent, boolean ismain) {

		class_vars = new LinkedHashMap<String, VarInfo>();
		class_methods = new LinkedHashMap<String, FunInfo>();
		children = new ArrayList<String>();
		isMain = ismain;
		parentClass = parent;
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
	LinkedHashMap<String, VarInfo> arg_types;
	boolean isVirtual;
	LinkedHashMap<String, VarInfo> fun_vars;

	public FunInfo( String rettype, boolean isvirtual) {
		return_type = rettype;
		arg_types = new LinkedHashMap<String, VarInfo>();
		isVirtual = isvirtual;
		fun_vars = new LinkedHashMap<String, VarInfo>();
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

