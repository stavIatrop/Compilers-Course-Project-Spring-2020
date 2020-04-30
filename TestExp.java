class TestExp {
	public static void main(String[] args) {
		A a;
		a = new B();
		System.out.println(a.fun(10, true, a));
	}
}

class A {
	int num;
	boolean bool;
	boolean[] bool2;
	public int fun(int num, boolean x, A a) {
		int num_aux;
		A obj;
		B obj2;
		obj2 = new B();
		obj = new B();
		num_aux = obj2.fun(2, true, obj);
		
		return 2;
	}
}

class B extends A {
	int othernum;
	public int fun(int num, boolean x, A a){
		return 0;
	}

}

class C extends B {

	public int fun(int num, boolean x, A a){
		return 0;
	}
	public A foo(){
		A obj2;
		obj2 = new C();
		return obj2;
	}
}