package fr.umlv.record;

public class Main {
	public static void main(String[] args) throws Throwable {
	  System.out.println("simple Point and Circle");
  	var p = new Point(1, 2);
  	System.out.println("x " + p.x());
  	System.out.println("y " + p.y());
  	
  	var circle = new Circle(p, 2);
  	System.out.println(circle);
  	
  	System.out.println("reflection API");
  	for(var c: Circle.class.getRecordComponents()) {
  		System.out.println(c);
  	}
  	
  	System.out.println("serialization proxy");
  	SerializationExample.main(args);
  }
}
