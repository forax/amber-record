package fr.umlv.record;

import java.util.Objects;

public class RecordConstructorExample {
  record Foo(Object o, int v) {
  	Foo(Object o) {
  		this.o = o;
  		this.v = 0;
  	}
  	
  	public Foo {
  		Objects.requireNonNull(o);
  	}
  }
  
  public static void main(String[] args) {
  	new Foo(null);
  }
}
