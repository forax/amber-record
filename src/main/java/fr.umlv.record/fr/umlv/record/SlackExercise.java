package fr.umlv.record;

import static java.util.Collections.unmodifiableList;
import static java.util.Collections.unmodifiableSet;
import static java.util.Objects.requireNonNull;
import static java.util.stream.Collectors.counting;
import static java.util.stream.Collectors.flatMapping;
import static java.util.stream.Collectors.groupingBy;
import static java.util.stream.Collectors.mapping;
import static java.util.stream.Collectors.toSet;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Objects;
import java.util.Set;
import java.util.TreeMap;
import java.util.stream.Collector;

// Like Slack, you have some User, Channel, Group, only Channel and User stores the message (Group is just a list of User).
// A User has to subscribe to a Channel to be able to send a message to it.
// There is an event system so one can gather statistics or create an index from the messages.
// Only the MessageManager is mutable, everything else is non mutable.
@SuppressWarnings("preview")
public interface SlackExercise {
  @FunctionalInterface
  interface Receiver {
    void dispatch(MessageManager manager, Message message);
    
    public static Receiver group(User... users) {
      var group = List.of(users);
      return (manager, message) -> {
        group.forEach(user -> user.dispatch(manager, message));
      };
    }
  }
    
  interface Storage {
    default void dispatch(MessageManager manager, Message message) {
      requireNonNull(manager);
      requireNonNull(message);
      manager.onReceive(this, message);
    }

    default List<Message> messages(MessageManager manager) {
      return unmodifiableList(manager.messageMap.getOrDefault(this, List.of()));
    }
  }
  
  record Channel(String name) implements Receiver, Storage {
    public Channel {
      requireNonNull(name);
    }
    
    public Set<User> subscribers(MessageManager manager) {
      requireNonNull(manager);
      return unmodifiableSet(manager.subscriptionMap.getOrDefault(this, Set.of()));
    }
    
    @Override
    public void dispatch(MessageManager manager, Message message) {
      requireNonNull(manager);
      requireNonNull(message);
      if (!manager.hasSubscribedTo(message.sender, this)) {
        throw new IllegalArgumentException("sender " + message.sender + " has not subscribed to " + this);
      }
      Storage.super.dispatch(manager, message);
    }
    
    public boolean hasSubscribedTo(MessageManager manager, User user) {
      Objects.requireNonNull(manager);
      Objects.requireNonNull(user);
      return manager.hasSubscribedTo(user, this);
    }
  }
  record User(String name) implements Receiver, Storage {
    public User {
      requireNonNull(name);
    }
    
    public Set<Channel> subscribed(MessageManager manager) {
      return unmodifiableSet(manager.subscribedMap.getOrDefault(this, Set.of()));
    }
    
    @Override
    public void dispatch(MessageManager manager, Message message) {
      Storage.super.dispatch(manager, message);
    }
  }
  
  record Message(String text, User sender, Instant timestamp) {
    public Message {
      requireNonNull(text);
      requireNonNull(sender);
      requireNonNull(timestamp);
    }
  }
  
  interface EventListener {
    public record Event(Storage storage, Message message) { /* empty */ }
    
    void onReceive(Event event);
  }
  
  public interface Aggregator<R> {
    EventListener listener();
    R result();

    private static <A, R> Aggregator<R> aggregate(Collector<EventListener.Event, A, R> collector) {
      var container = collector.supplier().get();
      var accumulator = collector.accumulator();
      var finisher = collector.finisher();
      return new Aggregator<>() {
        public EventListener listener() {
          return event -> accumulator.accept(container, event);
        }
        public R result() {
          return finisher.apply(container);
        }
      };
    }

    static Aggregator<NavigableMap<Instant, Long>> statistics() {
      return aggregate(groupingBy(event -> event.message.timestamp, TreeMap::new, counting()));
    }
    
    static Aggregator<Map<Storage, NavigableMap<Instant, Long>>> usersStatistics() {
      return aggregate(groupingBy(event -> event.storage, groupingBy(event -> event.message.timestamp, TreeMap::new, counting())));
    }

    static Aggregator<Map<String, Set<Message>>> index() {
      record Pair(String token, Message message) { /* empty */ }
      return aggregate(flatMapping(event -> Arrays.stream(event.message.text.split(" |\\t|\\n")).filter(token -> token.length() > 2).map(token -> new Pair(token, event.message)),
          groupingBy(pair -> pair.token.toLowerCase(), mapping(Pair::message, toSet()))));
    }
  }
  
  class MessageManager {
    private final HashMap<Storage, List<Message>> messageMap = new HashMap<>();
    
    private final HashMap<Channel, Set<User>> subscriptionMap = new HashMap<>();
    private final HashMap<User, Set<Channel>> subscribedMap = new HashMap<>();
    
    private final ArrayList<EventListener> listeners = new ArrayList<>();
    
    private void onReceive(Storage storage, Message message) {
      messageMap.computeIfAbsent(storage, __ -> new ArrayList<>()).add(message);
      
      var event = new EventListener.Event(storage, message);
      listeners.forEach(listener -> listener.onReceive(event));
    }
    
    private boolean hasSubscribedTo(User user, Channel channel) {
      return subscribedMap.getOrDefault(user, Set.of()).contains(channel);
    }
    
    public void postMessage(Receiver receiver, String text, User sender) {
      requireNonNull(receiver);
      requireNonNull(sender);
      receiver.dispatch(this, new Message(text, sender, Instant.now())); 
    }
    
    public void subscribe(User user, Channel channel) {
      requireNonNull(user);
      requireNonNull(channel);
      subscriptionMap.computeIfAbsent(channel, __ -> new HashSet<>()).add(user);
      subscribedMap.computeIfAbsent(user, __ -> new HashSet<>()).add(channel);
    }
  
    public void addEventListener(EventListener listener) {
      requireNonNull(listener);
      listeners.add(listener);
    }
  }

  public static void main(String[] args) {
    var manager = new MessageManager();
    var stats = Aggregator.statistics();
    var index = Aggregator.index();
    manager.addEventListener(stats.listener());
    manager.addEventListener(index.listener());
    
    var mary = new User("mary");
    var bob = new User("bob");
    var kevin = new User("kevin");
    
    var channel = new Channel("misc");
    
    // send direct message
    manager.postMessage(Receiver.group(bob, kevin), "hello Bob and Kevin !", mary);
    manager.postMessage(mary, "hello Mary !", bob);
    
    System.out.println("mary messages " + mary.messages(manager));
    System.out.println("bob messages " + bob.messages(manager));
    System.out.println("kevin messages " + kevin.messages(manager));
    
    // send channel message
    manager.subscribe(bob, channel);
    manager.subscribe(mary, channel);
    manager.postMessage(channel, "hello Everybody !", bob);
    
    System.out.println("channel misc messages " + channel.messages(manager));
    
    // stat and index
    System.out.println("stats " + stats.result());
    System.out.println("index for 'hello' " + index.result().get("hello"));
  }
}
