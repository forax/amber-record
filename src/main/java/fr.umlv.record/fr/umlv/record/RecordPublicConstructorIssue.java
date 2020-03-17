package fr.umlv.record;

public class RecordPublicConstructorIssue {
	  private record Result(Object o) {
	    @Override
      public String toString() {
	      return o.toString();
	    }
	  }

	  public static Object processResult() {
	    return new Result("foo");
	  }
}
