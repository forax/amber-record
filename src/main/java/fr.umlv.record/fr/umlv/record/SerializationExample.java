package fr.umlv.record;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;
import java.util.Objects;

public class SerializationExample {
  public static class Bob implements Serializable {
    private static final long serialVersionUID = 14;
    
    private final int age;
    
    public Bob(int age) {
      this.age = age;
    }
    
    private Object writeReplace() {
      return new SerProxy(age, "Bob");
    }  
    
    @Override
    public String toString() {
      return "Bob " + age;
    }
  }
  
  private record SerProxy(int value, String name) implements Serializable {
    public SerProxy {
      Objects.requireNonNull(name);
    }
    
    private Object readResolve() throws ObjectStreamException {
      return switch(name) {
        case "Bob" -> new Bob(value);
        default -> throw new InvalidObjectException("no Bob ?");
      };
    }
  }
  
  
  public static void main(String[] args) throws IOException, ClassNotFoundException {
    var baos = new ByteArrayOutputStream();
    var oos = new ObjectOutputStream(baos);
    oos.writeObject(new Bob(42));
    
    var bais = new ByteArrayInputStream(baos.toByteArray());
    var ois = new ObjectInputStream(bais);
    var bob = (Bob) ois.readObject();
    System.out.println(bob);
  }
}
