// A one hour more formal presentation on Pattern Matching
// I a valid jshell file that you

// # Algebraic Data Types and Pattern Matching

// ## Java Version
System.out.println(Runtime.version());
System.out.println(System.getProperty("java.home"));

// # Algebraic Data Types and Where to find them ?
// ![Fantastic Beast](images/fantastic-beasts.jpg)

// ## Algebraic Data Types
// Composite type of product types and sum types
// - product types (A x B x C)
// - sum type  (A | B | C)

// ## Algebraic Data Types in OCaml
// Examples of product types and sum types in OCaml
// ```ocaml
// (* record *)
// type car = { brand: string; color: string; };;
// type bus = { brand: string; height: float; };;
// ```
// ```ocaml
// (* variant *)
// type vehicle = Car of car | Bus of bus ;;
// ```

// ## Pattern Matching
// ```ocaml
// let color = function
//  | Car { color=c } -> c
//  | Bus _ -> "yellow"
//  ;;
// ```

// ## Pattern Matching: when clause
// ```ocaml
// let okayForLowBridge = function
//  | Car _ -> true
//  | Bus { height=h } when h < 12.0 -> true
//  | _ -> false
//  ;;
// ```

// ## Equivalence in OOP
// - sum type  (interface)
//   ```java
//   interface Vehicle { }
//   ```
// - product types  (class)
//   ```java
//   class Bus implements Vehicle {
//     String name;
//     double height;
//   }
//   ```

// ## Differences FP vs OOP
// - OOP defines
//   - open types,
//   - closed functions (methods)
// - FP defines
//   - closed types,
//   - open functions

// ## OOP == polymorphism
// Actual: OOP

// | Behaviors      | Function             | Method         |
// | -------------- | -------------------- | -------------- |
// | class POV      | outside a class      | inside a class |
// | Open interface | visitor / instanceof |  polymorphism  |

// > having open interfaces has a cost !

// ## ![Rick Deckard](images/blade-runner-2049.jpg)

// ## With Pattern matching
// Proposed: fusion OOP + FP

// | Behaviors       | Function         | Method         |
// | --------------- | ---------------- | -------------- |
// | Closed type???  | pattern matching |  polymorphism  |
// | Open interface  | pattern matching |  polymorphism  |

// # Pattern Matching in Java

// ## Plan
// - expression switch (Java 12 to 14)
// - record  (Java 14+)
// - sealed interface (Java 15+)
// - instanceof enhancement (Java 14+)
// - future ?

// # Expression Switch
// - Enhance switch to be an expression too
// - Fix C switch warts ?

// ## What wrong with the C switch ?
// `break` is easy to forget (fallthrough) + weird scope
void color(String vehicle) {
  switch(vehicle) {
    case "car":
    case "sedan":
      var length = vehicle.length();
      System.out.println((length < 4)? "blue": "red");
      // oops
    case "bus":
      var length = 0;   // oops
      System.out.println("yellow");
      break;
  }  // oops no default
}
color("sedan");

// ## Arrow Syntax : Enhance existing switch
// - avoid fallthrough: use curly braces
// - allow comma separated values
void color(String vehicle) {
  switch(vehicle) {
    case "car", "sedan" -> {
      var length = vehicle.length();
      System.out.println((length < 4)? "blue": "red");
    }
    case "bus" -> {
      var length = 0;
      System.out.println("yellow");
    }
  } // oops no default
}
color("sedan");

// ## Expression Switch
// switch can be used as an expression, `default` is mandatory !
String color(String vehicle) {
  return switch(vehicle) {
    case "car", "sedan" -> {
      var length = vehicle.length();
      yield (length < 4)? "blue": "red";
    }
    case "bus" -> "yellow";
    default -> {
      throw new AssertionError();
    }
  };
}
System.out.println(color("sedan"));

// ## Yield backward compatibility issue
// `yield` is enable as keyword only at the start of an instruction
void yield(int value) { }
void color(String vehicle) {
  yield (42);
}


// ## Future
// ```java
// String color(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Car(String brand, Color color) -> color;
//     case Bus(String brand, double height) -> "yellow";
//   };  //no default
// }
// ```

// We need to be able to deconstruct a class


// # Record

// ## Example of Record
// Declaration
record Bus(String brand, double height) { }

// Usage
var bus = new Bus("imperial", 7);
System.out.println(bus);

// ## Constructors
record Bus(String brand, double height) {
  // canonical constructor, generated automatically
  // public Bus(String brand, double height) {
  //   ...
  // }
  // compact constructor
  public Bus {
    Objects.requireNonNull(brand);
  }
}

// ## equals, hashCode and toString
// are automatically generated
record Bus(String brand, double height) { }
var bus1 = new Bus("imperial", 7);
var bus2 = new Bus("imperial", 7);
System.out.println(bus1.equals(bus2));

// ## Records are immutable
// Avoid mutation during the matching
// ```java
// String color(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Bus(_, double height) when (bus.height = 3) < 11 -> {}
//     case Bus(_, double height) when bus.height == 3) -> {}
//     default -> ...
//   };
// }
// ```

// ## Reflection support
record Bus(String brand, double height) { }
var components = List.of(Bus.class.getRecordComponents());
System.out.println(components);


// ## Restrictions
// - shallow immutability
// - no inheritance
// - other constructors has to delegate to primary constructor,
//   initializer blocks are not supported


// # Sealed interface

// ## Closed hierarchy
// Add a keyword `sealed` + a `permits` list
// ```java
// sealed interface Vehicle
//   permits Car, Bus { }
// record Car(String brand, String color) implements Vehicle { }
// record Bus(String brand, double height) implements Vehicle { }
// ```

// ## Add inference of `permits` clause ?
// The clause `permits` is inferred if everything in the same compilation unit
sealed interface Vehicle {
   // inferred permits Car, Bus
  record Car(String brand, String color) implements Vehicle { }
  record Bus(String brand, double height) implements Vehicle { }
}

// ## Exhaustiveness
// The compiler doesn't require the `default` clause anymore.
// ```java
// String color(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Car car -> car.color;
//     case Bus bus -> "yellow";
//   };  //no default
// }
// ```
// but before switch on type, let starts by enhancing `instanceof`


// # Enhanced `instanceof`

// - Introduce de-construction
// - Fix the unnecessary cast

// ## What wrong with the old instanceof ?
// The cast is unnecessary.
record Bus(String brand, double height) {
  public boolean equals(Object o) {
    if (!(o instanceof Bus)) {
      return false;
    }
    var bus = (Bus) o;   // <-- that cast
    return brand.equals(bus.brand)
        && Double.compare(height, bus.height) == 0;
  }
}

// > The VM routinely removes it, so it's not present
// > in the generated assembly code

// # Type test Pattern

// ## Type test pattern
// Declare a variable available if the `instanceof` is true
record Bus(String brand, double height) {
  public boolean equals(Object o) {
    return o instanceof Bus bus
        && brand.equals(bus.brand)
        && Double.compare(height, bus.height) == 0;
  }
}
var bus = new Bus("imperial", 7);
var bus2 = new Bus("imperial", 7);
System.out.println(bus.equals(bus2));

// ## Deconstructing
record Bus(String brand, double height) {
  public boolean equals(Object o) {
    return o instanceof Bus(String brand2, double height2)
      && brand.equals(brand2)
      && Double.compare(height, height2) == 0;
  }
}
var bus = new Bus("imperial", 7);
var bus2 = new Bus("imperial", 7);
System.out.println(bus.equals(bus2));

// ## Deconstructing + var
record Bus(String brand, double height) {
  public boolean equals(Object o) {
    return o instanceof Bus(var brand2, var height2)
      && brand.equals(brand2)
      && Double.compare(height, height2) == 0;
  }
}
var bus = new Bus("imperial", 7);
var bus2 = new Bus("imperial", 7);
System.out.println(bus.equals(bus2));


// ## Type test vs Code block
record Bus(String brand, double height) {
  public boolean equals(Object o){
    if (o instanceof Bus bus) {
      return brand.equals(bus.brand)
          && Double.compare(height, bus.height) == 0;
    }
    return false;
  }
}

// ## Type test vs Code block
record Bus(String brand, double height) {
  public boolean equals(Object o){
    if (!(o instanceof Bus bus)) {
      return false;
    }
    return brand.equals(author.bran)
        && Double.compare(height, bus.height) == 0;
  }
}

// ## More fun
public double add(Object o1, Object o2) {
  if (o1 instanceof Integer i1 && o2 instanceof Integer i2) {
    return i1 + i2;
  }
  throw new ArithmeticException();
}

// ## Even funnier
public void loop(Object o) {
  while((o instanceof Boolean b) && b) {
    System.out.println(o);
    o = false;
  }
}
loop(true);


// # Future

// ## Switch on types
// ```java
// String brand(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Car car -> car.brand;
//     case Bus bus -> bus.brand;
//   };  //no default
// }
// ```

// # Deconstruction of local declaration

// ## Extracting values of a record
record Car(String brand, String color) {}
var car = new Car("imperial", "red");
var brand = car.brand();
var color = car.color();
System.out.println(brand + " " + color);

// ## Use destructuring
// ```java
// record Car(String brand, String color) {}
// var car = new Car("imperial", "red");
// Car(String owner, String color) = car;
// System.out.println(owner + " " + color);
// ```

// ## With inference
// Reusing `var` and `_`
// ```java
// record Car(String brand, String color) {}
// var car = new Car("imperial", "red");
// Car(_, var color) = car;
// System.out.println(color);
// ```

// # Tuple ?

// ## Use inference
// Removing the name of the type which can be inferred too
// ```java
// record Car(String brand, String color) {}
// Car car = ("imperial", "red");   // inference
// (_, var color) = car;            // inference
// System.out.println(color);
// ```

// ## Inference in for loop (tuple)
// The type Map.Entry is inferred
// ```java
// Map<String, Car> mapNameToCar = ...
// for((var name, var car) : mapNameToCar.entrySet()) {
//   System.out.println(name + " " + car);
// }
// ```

// # Deconstruction in switch

// ## A switch on types + destructuring
// Allow to de-construct the content of a record
// ```java
// String brand(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Car(var brand, _) -> brand;
//     case Bus(var brand, _) -> brand;
//   };  //no default
// }
// ```

// ## And inferring a structural common super type
// ```java
// String brand(Vehicle vehicle) {
//   return switch(vehicle) {
//     case Car(var brand, _), Bus(var brand, _) -> brand;
//   };  //no default
// }
// ```


// # Conclusion: Full Pattern Matching

// ## Kind of patterns
// - __null pattern__ (`null`), match only `null`
// - __type test pattern__ (`Foo foo`) match the type (not `null`)
//   - __var test pattern__ (`var foo`) infer the type
//   - __any test pattern__ (`_`) don't introduce a variable
// - __or pattern__ (`pattern1, pattern2`) match either one side or the other
// - __extraction pattern__ (`(..., pattern, ...)`) match a component
// - __constant pattern__ (`123`) match the constant value

// `var` or `_` are just inference, no special matching
