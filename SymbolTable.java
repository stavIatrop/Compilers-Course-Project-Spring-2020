import java.util.ArrayList;
import java.util.HashMap;
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

	public void printOffsets() {
		HashMap<String, Integer> offsets = new HashMap<String, Integer>();
		offsets.put("int", 4);
		offsets.put("boolean", 1);
		offsets.put("int[]", 8);
		offsets.put("boolean[]", 8);
		HashMap<String, Integer> offsetVars = new HashMap<String, Integer>();		//keep track of the offset enumeration of variables and methods to each class
		HashMap<String, Integer> offsetMethods = new HashMap<String, Integer>();

		for (String ClassStr : hmap.keySet() ) {
			
			ClassInfo cinfo = hmap.get(ClassStr);
			if (cinfo.isMain) {
				offsetMethods.put(ClassStr, 0);
				offsetVars.put(ClassStr, 0);
				continue;
			}
			if (cinfo.parentClass == null ) {
				offsetMethods.put(ClassStr, 0);
				offsetVars.put(ClassStr, 0);
			}else {			//this search only one ancestor "behind" works because
							//LinkedHashMap stores the classes with insertion order
							//and also because when we have "class B extends A”, A must be defined before B
				offsetMethods.put(ClassStr, offsetMethods.get(cinfo.parentClass));
				offsetVars.put(ClassStr, offsetVars.get(cinfo.parentClass));
			}

			System.out.println("-----------Class " + ClassStr + "-----------" );
			System.out.println("---Variables---");
			for (String VarStr : cinfo.class_vars.keySet()) {

				String type = cinfo.class_vars.get(VarStr);
				System.out.println(ClassStr + "." + VarStr + " : " + offsetVars.get(ClassStr));
				if (!offsets.containsKey(type)) {
					offsetVars.put(ClassStr, offsetVars.get(ClassStr) + 8);

				}else {
					offsetVars.put(ClassStr, offsetVars.get(ClassStr) + offsets.get(type));

				}

			}
			System.out.println("---Methods---");
			for (String MethodStr : cinfo.class_methods.keySet()) {

				if (cinfo.class_methods.get(MethodStr).isOverriding)
					continue;
				
				System.out.println(ClassStr + "." + MethodStr + " : " + offsetMethods.get(ClassStr));
				offsetMethods.put(ClassStr, offsetMethods.get(ClassStr) + 8);
			}
			System.out.println();

		}
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
				System.out.println("Method Variables: ");
				for (String var : funinfo.fun_vars.keySet()) {
					System.out.println(funinfo.fun_vars.get(var) + " " + var);
				}
			}
		}
	}
	
	public boolean checkClass(String className) {
		
		if (hmap.containsKey(className)) {
			return true;
		}
		return false;
	}

	public boolean checkSubtype(String type, String ancestorType) {
		
	}
	public FunInfo lookupMethod(String className, String methodName) {

		ClassInfo cInfo = hmap.get(className);
		FunInfo fInfo;
		if (cInfo.class_methods.containsKey(methodName)) {
			fInfo = cInfo.class_methods.get(methodName);
			return fInfo;
		}
		//maybe it is a method from an ancestor
		boolean ancestors;
        ClassInfo parent;

		if (cInfo.parentClass != null) {
			ancestors = true;
			parent = hmap.get(cInfo.parentClass);  //search for method in ancestors
		}else {
			ancestors = false;
			parent = null;
		}
		while(ancestors) {

			if (parent.class_methods.containsKey(methodName)){
				fInfo = parent.class_methods.get(methodName);
				return fInfo;
			}
			if (parent.parentClass != null) {
				parent = hmap.get(parent.parentClass);
			} else {
				ancestors = false;
			}
		}
		return null;
	}

	
	public String lookupName(String className, String methodName, String idName) {

		ClassInfo cInfo;
        cInfo = hmap.get(className);
        FunInfo funInfo = cInfo.class_methods.get(methodName);  //search the identifier idName to current method's scope

        ClassInfo parent;           //parent declaration outside if scope for later use is needed
        boolean flag = false;

        if (!funInfo.fun_vars.containsKey(idName) && !funInfo.arg_types.containsKey(idName) && !cInfo.class_vars.containsKey(idName)) {
            
            boolean ancestors;
            if (cInfo.parentClass != null) {
                ancestors = true;
                parent = hmap.get(cInfo.parentClass);  //search for variable in ancestors
            }else {
                ancestors = false;
                parent = null;
            }
            while (ancestors) {

                if (parent.class_vars.containsKey(idName)) {
                    flag = true;
                    break;
                }
                if (parent.parentClass != null) {
                    parent = hmap.get(parent.parentClass);
                } else {
                    ancestors = false;
                }
            }
            if (flag == false) {
				return null;
            }
        } else {
            parent = null;
        }
        
        String type = "null";

        if (flag == true) {         //it means that variable found on an ancestor's scope
            type = parent.class_vars.get(idName);
            
        }else { 

            if (funInfo.fun_vars.containsKey(idName)) {       //first check method's scope
                type = funInfo.fun_vars.get(idName);
                
            }else if (funInfo.arg_types.containsKey(idName)) {
                type = funInfo.arg_types.get(idName);
                
            } else if (cInfo.class_vars.containsKey(idName)) {     //then class' and its ancestors' scopes
                type = cInfo.class_vars.get(idName);
                    
            }
		}
		return type;

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