package fr.umlv.record;

import java.util.AbstractList;
import java.util.Iterator;
import java.util.List;
import java.util.function.IntBinaryOperator;

/*
@SuppressWarnings("preview")
public sealed interface Expr {
  public record Op(Expr left, char c, Expr right, IntBinaryOperator op)
    implements Expr {
  	
  }
  public record Value(int value)
    implements Expr {
  	
  }
  
	static Expr parse(Iterator<String> it) {
  	var token = it.next();
  	return switch(token) {
  	  case "+" -> new Op(parse(it), '+', parse(it), (a, b) -> a + b);
  	  case "-" -> new Op(parse(it), '-', parse(it), (a, b) -> a - b);
  	  case "*" -> new Op(parse(it), '*', parse(it), (a, b) -> a * b);
  	  case "/" -> new Op(parse(it), '/', parse(it), (a, b) -> a / b);
  	  default -> new Value(Integer.parseInt(token));
  	};
  }
  
  public static void main(String[] args) {
  	 var expr = parse(new java.util.Scanner("* + 2 3 5"));
  	 System.out.println(expr);
  }
}
*/