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

class TableStruct {		//structure of each scope's hashmap

	HashMap<String, NameInfo> hm;


	public TableStruct() {
		hm = new HashMap<String, NameInfo>();
	}	
}

class NameInfo {	//structure of a declared name's info


}
