package fr.umlv.record;

public class PublicConstructorProblem {
  private record Foo(int x) {
  	
  }
  
  public static Object foo() {
  	return new Foo(42);
  }
  
  public static void main(String[] args) throws Exception {
		Object o = foo();
		Object o2 = o.getClass().getConstructor(int.class).newInstance(43);
		System.out.println(o2);
	}
}
