package fr.umlv.record;

import java.util.Objects;

public record Circle(Point center, int radius) {
  public Circle {
  	Objects.requireNonNull(center);
  }
}
