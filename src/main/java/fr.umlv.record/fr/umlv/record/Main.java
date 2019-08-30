package fr.umlv.record;

public class Main {
	
	
	public static void main(String[] args) throws NoSuchMethodException {
  	var p = new Point(1, 2);
  	System.out.println("x " + p.x());
  	System.out.println("y " + p.y());
  	
  	var circle = new Circle(p, 2);
  	System.out.println(circle);
  	
  	for(var m: Circle.class.getRecordAccessors()) {
  		System.out.println(m);
  	}
  }
}
