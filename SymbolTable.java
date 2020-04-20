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

	public boolean checkOverriding(String className, String methodName ) {

		ClassInfo childClass = hmap.get(className);
		FunInfo CurrMethod = childClass.class_methods.get(methodName);
		ClassInfo parent_Class;		//keep track of parentClass if exists
		String parentName;
		if (hmap.get(className).parentClass == null) {	//method does not inherits from another class
			return true;
		}
		boolean flag = true;
		parentName = childClass.parentClass;	//initialize first ancestor
		parent_Class = hmap.get(parentName);
		FunInfo ParentMethod;
		while(flag) {

			for (String MethodStr : parent_Class.class_methods.keySet()) {

				if (MethodStr == methodName) {										//1st requirement, same name

					ParentMethod = parent_Class.class_methods.get(MethodStr);
					if (CurrMethod.return_type == ParentMethod.return_type ) {		//2nd requirement, same return type

						ArrayList<String> parentArgs = new ArrayList<String>();
						ArrayList<String> childArgs = new ArrayList<String>();
						parentArgs.addAll(ParentMethod.arg_types.values());
						childArgs.addAll(CurrMethod.arg_types.values());
						
						if (parentArgs.equals(childArgs) ) {						//3rd requirement, same (ordered) argument types, valuable use of LinkedHashmap here to check the insertion order
							CurrMethod.isOverriding = true;
							return true;
						}
						
					}else {
						return false;	//method name same, but return type not
					}
					return false; 	//method name same, but arg types not
					
				}
			}
			//next ancestor if exists
			if (parent_Class.parentClass != null) {
				parentName = parent_Class.parentClass;
				parent_Class = hmap.get(parentName);
			}else {
				flag = false;
			}
			

		}
		return true;	//not overriding any method, no parse errors
	}

	public void printSTable() {

		for (String ClassStr : hmap.keySet() ) {

			System.out.println("Class: " + ClassStr);
			ClassInfo cinfo = hmap.get(ClassStr);
			System.out.println("Class variables:");
			for (String VarStr : cinfo.class_vars.keySet()) {

				String type = cinfo.class_vars.get(VarStr);;
				System.out.println(type + " " + VarStr);
			}
			System.out.println("Class methods: ");
			for (String MethodStr : cinfo.class_methods.keySet()) {

				FunInfo funinfo = cinfo.class_methods.get(MethodStr);
				String rettype = funinfo.return_type;
				System.out.print(rettype + " " + MethodStr + " " + "( ");
				for (String ParamStr : funinfo.arg_types.keySet()) {
					String type = funinfo.arg_types.get(ParamStr);
					System.out.print(type + " " + ParamStr + " ");
				}
				System.out.println(")");
			}
		}
	}

}

class ClassInfo  {	//structure of a declared class' info
	
	LinkedHashMap<String, String> class_vars;
	LinkedHashMap<String, FunInfo> class_methods;
	String parentClass;
	ArrayList<String> children;
	boolean isMain;

	public ClassInfo(String parent, boolean ismain) {

		class_vars = new LinkedHashMap<String, String>();
		class_methods = new LinkedHashMap<String, FunInfo>();
		children = new ArrayList<String>();
		parentClass = parent;
		isMain = ismain;
	}

}

// class VarInfo {		//structure of a declared variable's info
	
// 	String type;	//int or boolean
// 	String value;	//might not be needed

// 	public VarInfo( String tp, String val) {
// 		type = tp;
// 		value = val;
// 	}

// }

class FunInfo  {		//structure of a declared function's info

	String return_type;
	LinkedHashMap<String, String> arg_types;
	boolean isOverriding;
	LinkedHashMap<String, String> fun_vars;

	public FunInfo( String rettype, boolean isover) {
		return_type = rettype;
		arg_types = new LinkedHashMap<String, String>();
		isOverriding = isover;
		fun_vars = new LinkedHashMap<String, String>();
	}
}