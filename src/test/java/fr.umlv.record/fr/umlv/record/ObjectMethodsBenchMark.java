package fr.umlv.record;

import static java.util.Arrays.stream;
import static java.util.stream.IntStream.range;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.BenchmarkMode;
import org.openjdk.jmh.annotations.Fork;
import org.openjdk.jmh.annotations.Measurement;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.annotations.OutputTimeUnit;
import org.openjdk.jmh.annotations.Scope;
import org.openjdk.jmh.annotations.State;
import org.openjdk.jmh.annotations.Warmup;
import org.openjdk.jmh.infra.Blackhole;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.RunnerException;
import org.openjdk.jmh.runner.options.Options;
import org.openjdk.jmh.runner.options.OptionsBuilder;

@SuppressWarnings("static-method")
@Warmup(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Measurement(iterations = 5, time = 5, timeUnit = TimeUnit.SECONDS)
@Fork(value = 1)
@BenchmarkMode(Mode.AverageTime)
@OutputTimeUnit(TimeUnit.NANOSECONDS)
@State(Scope.Benchmark)
public class ObjectMethodsBenchMark {
  
  static final class PersonClass {
    private final String name;
    private final int age;
    
    PersonClass(String name, int age) {
      this.name = name;
      this.age = age;
    }
    
    @Override
    public boolean equals(Object obj) {
      return obj instanceof PersonClass p &&
          age == p.age && name.equals(p.name);
    }
    @Override
    public int hashCode() {
      return age * 31 + name.hashCode();
    }
    @Override
    public String toString() {
      // use PersonRecord instead of PersonClass, to have the exact same string
      return "PersonRecord[name=" + name + ", age=" + age + "]"; 
    }
  }
  
  record PersonRecord(String name, int age) {
  }
  
  private static final String[] NAMES = {"Bob", "Genna", "Elvis"};
  private static final PersonClass[] PERSON_CLASS_ARRAY =
      range(0, 10).boxed().flatMap(age -> stream(NAMES).map(name -> new PersonClass(name, age))).toArray(PersonClass[]::new);
  private final PersonClass aPersonClass = new PersonClass("Bob", 0);
  
  private static final PersonRecord[] PERSON_RECORD_ARRAY =
      range(0, 10).boxed().flatMap(age -> stream(NAMES).map(name -> new PersonRecord(name, age))).toArray(PersonRecord[]::new);
  private final PersonRecord aPersonRecord = new PersonRecord("Bob", 0); 
  
  @Benchmark
  public void equals_class(Blackhole blackhole) {
    for(var person: PERSON_CLASS_ARRAY) {
      blackhole.consume(person.equals(aPersonClass));
    }
  }
  
  @Benchmark
  public void equals_record(Blackhole blackhole) {
    for(var person: PERSON_RECORD_ARRAY) {
      blackhole.consume(person.equals(aPersonRecord));
    }
  }
  
  @Benchmark
  public void hashCode_class(Blackhole blackhole) {
    blackhole.consume(aPersonClass.hashCode());
  }
  
  @Benchmark
  public void hashCode_record(Blackhole blackhole) {
    blackhole.consume(aPersonRecord.hashCode());
  }
  
  @Benchmark
  public void toString_class(Blackhole blackhole) {
    blackhole.consume(aPersonClass.toString());
  }
  
  @Benchmark
  public void toString_record(Blackhole blackhole) {
    blackhole.consume(aPersonRecord.toString());
  }
  
  public static void main(String[] args) throws RunnerException {
    Options opt = new OptionsBuilder()
        .include(ObjectMethodsBenchMark.class.getName())
        .build();
    new Runner(opt).run();
  }
}
