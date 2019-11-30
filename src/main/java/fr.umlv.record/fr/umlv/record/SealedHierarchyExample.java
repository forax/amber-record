package fr.umlv.record;

import java.util.Iterator;
/*
public class SealedHierarchyExample {
	sealed interface Expr {}
	record Value(int value) implements Expr {}
	
  sealed interface BinOp extends Expr {
  	Expr left();
  	Expr right();
  }
  record Add(Expr left, Expr right) implements BinOp {}
  record Sub(Expr left, Expr right) implements BinOp {}
  record Mul(Expr left, Expr right) implements BinOp {}
  record Div(Expr left, Expr right) implements BinOp {}
  
  static Expr parse(Iterator<String> it) {
  	var token = it.next();
  	return switch(token) {
  	  case "+" -> new Add(parse(it), parse(it));
  	  case "-" -> new Sub(parse(it), parse(it));
  	  case "*" -> new Mul(parse(it), parse(it));
  	  case "/" -> new Div(parse(it), parse(it));
  	  default -> new Value(Integer.parseInt(token));
  	};
  }
  
  public static void main(String[] args) {
  	 var expr = parse(new java.util.Scanner("* + 2 3 5"));
  	 System.out.println(expr);
  }
}
*/
