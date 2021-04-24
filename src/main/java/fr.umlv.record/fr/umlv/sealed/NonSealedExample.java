package fr.umlv.sealed;

import java.util.Arrays;

public class NonSealedExample {
  sealed interface I {
  }
  non-sealed static class A implements I {
  }
  
  public static void main(String[] args) {
		I i = new A();
  	
		var permitted = I.class.permittedSubclasses();
		System.out.println(Arrays.toString(permitted));
	}
}
