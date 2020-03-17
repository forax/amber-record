package fr.umlv.record;

import java.util.Objects;

public class RecordConstructorExample {
  record Foo(Object o, int v) {
  	Foo(Object o) {
  	  this(o, 0);
  	}
  	
  	public Foo {
  		Objects.requireNonNull(o);
  	}
  }
  
  public static void main(String[] args) {
  	var foo = new Foo(null);
  	System.out.println(foo);
  }
}
