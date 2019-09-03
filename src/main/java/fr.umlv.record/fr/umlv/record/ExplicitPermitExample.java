package fr.umlv.record;

public class ExplicitPermitExample {
	sealed interface Itf permits Foo {
		
	}
  record Foo(int x) { }
}
