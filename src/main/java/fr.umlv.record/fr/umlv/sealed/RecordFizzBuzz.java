package fr.umlv.sealed;

import static java.lang.System.out;
import static java.util.stream.IntStream.rangeClosed;

import java.util.Optional;
import java.util.function.IntFunction;

public interface RecordFizzBuzz {
  interface Matcher {
    Optional<String> match(int value);

    default Matcher or(Matcher matcher) {
      return value -> match(value).or(() -> matcher.match(value));
    }

    default IntFunction<String> orDefault() {
      return value -> match(value).orElseGet(() -> "" + value);
    }
  }

  sealed interface Divisible extends Matcher
    permits Fizz, Buzz, FizzBuzz {
    int divisor();
    default Optional<String> match(int value) {
      return Optional.of(getClass().getName()).filter(__ -> value % divisor() == 0);
    }
  }

  record Fizz(int divisor) implements Divisible {}
  record Buzz(int divisor) implements Divisible {}
  record FizzBuzz(int divisor) implements Divisible {}

  static void main(String[] args) {
    var fizz = new Fizz(3);
    var buzz = new Buzz(5);
    var fizzbuzz = new FizzBuzz(15);

    rangeClosed(1, 100).mapToObj(fizzbuzz.or(fizz).or(buzz).orDefault()).forEach(out::println);
  }
}
