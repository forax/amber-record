package fr.umlv.sealed;

public class ExplicitPermitExample {
	sealed interface Itf permits Foo {
		
	}
  record Foo(int x) implements Itf { }
}

