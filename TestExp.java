class TestExp {
	public static void main(String[] args) {
		int j;
		System.out.println(new A().fun(10, true));
	}
}

class A {
	int num;
	boolean bool;
	boolean[] bool2;
	public int fun(int num, boolean x) {
		int num_aux;
		A obj;
		obj = new B();
		num_aux = 2;
		return 2;
	}
}

class B extends A {
	int othernum;
	public int fun(int num, boolean x){
		return 0;
	}

}

class C extends B {

	public int fun(int num, boolean x){
		return 0;
	}
	public int foo(){
		return 0;
	}
}