package fr.umlv.record;

import java.util.Objects;

public class RecordPublicConstructorIssue {
	  private record Result(Object o) {
	    public String toString() {
	      return o.toString();
	    }
	  }

	  public static Object processResult() {
	    return new Result("foo");
	  }
}
