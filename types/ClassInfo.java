package types;

import java.util.LinkedHashMap;
import java.util.ArrayList;

public class ClassInfo  {	//structure of a declared class' info
	
    public LinkedHashMap<String, String> class_vars;
	public LinkedHashMap<String, FunInfo> class_methods;
	public String parentClass;
	public ArrayList<String> children;
	public boolean isMain;

	public ClassInfo(String parent, boolean ismain) {

		class_vars = new LinkedHashMap<String, String>();
		class_methods = new LinkedHashMap<String, FunInfo>();
		children = new ArrayList<String>();
		parentClass = parent;
		isMain = ismain;

	}

}