package fr.umlv.record.main;

import java.lang.reflect.InvocationTargetException;

import fr.umlv.record.RecordPublicConstructorIssue;

public class Main {
  public static void main(String[] args) throws InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException {
		Object o = RecordPublicConstructorIssue.processResult();
		Object o2 = o.getClass().getConstructor(Object.class).newInstance((Object)null);
		System.out.println(o2);
	}
}
