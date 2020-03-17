package fr.umlv.sealed;


public class ImplicitWeirdness {
  sealed interface I { }
  sealed interface J extends I { }
  final static class A implements I, J { }
}

