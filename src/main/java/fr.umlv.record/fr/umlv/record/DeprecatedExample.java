package fr.umlv.record;

import java.util.Arrays;
import java.util.List;

public class DeprecatedExample {
  public @Deprecated record Foo(@Deprecated int x) {}
  
  public static void main(String[] args) {
    var components = List.of(Foo.class.getRecordComponents());
    for(var component: components) {
      System.out.println(Arrays.toString(component.getDeclaredAnnotations()));
    }
  }
}
