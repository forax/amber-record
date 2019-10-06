package fr.umlv.record;

public class AncestorPermitExample {
	/*
  sealed interface I permits A {
  	
  }
  final static class A {
  	
  }
  */
	/*
  sealed interface I permits J {
  	
  }
  sealed interface J permits I {
  	
  }
  */
	/*
  sealed interface I permits I {
  	
  }*/
  
  
  public static void main(String[] args) throws Exception {
		//var a = new A();
		//I i = new A();
  	
  	//Class.forName(I.class.getName());
	}
}
