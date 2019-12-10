package fr.umlv.record;

public class RecordWithCovariantInterfaceExample {
  public interface Namable {
    Object name();
  }
  
  public record Foo(String name) implements Namable {
    
  }
}
