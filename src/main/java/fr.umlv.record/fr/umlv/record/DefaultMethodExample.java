package fr.umlv.record;

public interface DefaultMethodExample {
	interface I {
		default int x() {
			System.out.println("foo");
			return -1;
		}
	}
	
  record Foo(int x) implements I {
    // empty
  }
  
  public static void main(String[] args) {
  	System.out.println(new Foo(12).x());
  }
}
