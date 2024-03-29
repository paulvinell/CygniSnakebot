package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.WeakHashMap;
import se.cygni.snake.SimpleSnakePlayer;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public abstract class Behavior implements Runnable {

  public static final WeakHashMap<SnakeDirection, Double> values = new WeakHashMap<>();
  private static final ArrayList<Behavior> behaviors = new ArrayList<>();

  protected static List<SnakeDirection> directions;
  public static long currentTick;

  static {
    values.put(SnakeDirection.UP, 0D);
    values.put(SnakeDirection.DOWN, 0D);
    values.put(SnakeDirection.LEFT, 0D);
    values.put(SnakeDirection.RIGHT, 0D);
  }

  public static final SnakeDirection getBestMove(final List<SnakeDirection> directions, long nanoseconds) {
    Behavior.directions = directions;
    currentTick = Tick.tick.mapUpdateEvent.getGameTick();

    resetValues();
    callBehaviors();

    while (areBehaviorsRunning() && (System.nanoTime() - nanoseconds < 1800000000L)) { //2100000000L
      try {
        Thread.sleep(1);
      } catch (InterruptedException e) {}
    }

    double bestScore = Double.MIN_VALUE;
    SnakeDirection bestDirection = SnakeDirection.DOWN;
    final SnakeDirection curDirection = Tick.tick.relativeDirection.getCurrentSnakeDirection();

    System.out.println();
    System.out.println("Sum " + currentTick);
    synchronized (values) {
      for (SnakeDirection direction : values.keySet()) {
        System.out.println(direction + " " + values.get(direction));
        if (directions.contains(direction) && values.get(direction) > bestScore) {
          bestScore = values.get(direction);
          bestDirection = direction;
        }
      }
    }

    return bestDirection;
  }

  public static final void clearBehaviors() {
    behaviors.clear();
  }

  private static final boolean areBehaviorsRunning() {
    for (Behavior b : behaviors) {
      if (b.lastGameTick != Tick.tick.mapUpdateEvent.getGameTick()) {
        return true;
      }
    }

    return false;
  }

  private static final void callBehaviors() {
    for (final Behavior b : behaviors) {
      new Thread(b).start();
    }
  }

  private static final void resetValues() {
    for (final SnakeDirection direction : values.keySet()) {
      values.put(direction, 0D);
    }
  }


  protected final Tick tick;
  public long lastGameTick;

  public Behavior(Tick tick) {
    this.tick = tick;

    behaviors.add(this);
  }

  protected abstract boolean canRun();

  public final void run() {
    final long workingTick = tick.mapUpdateEvent.getGameTick();
    if (this.canRun()) {
      final HashMap<SnakeDirection, Double> behaviorValues = this.getValues(directions);

      if (Behavior.currentTick == workingTick) {
      System.out.println();
      System.out.println(workingTick + " " + this.getClass());
        synchronized (values) {
          if (Behavior.currentTick == workingTick) {
            for (final SnakeDirection direction : behaviorValues.keySet()) {
              System.out.println(direction + " " + behaviorValues.get(direction));
              values.put(direction, values.get(direction) + behaviorValues.get(direction));
            }
          }
        }

        if (lastGameTick < workingTick) {
          lastGameTick = workingTick;
        }
      }
    } else {
      lastGameTick = Behavior.currentTick;
    }
  }

  public abstract HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions);
}
