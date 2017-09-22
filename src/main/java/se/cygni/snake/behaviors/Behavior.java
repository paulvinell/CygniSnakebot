package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import se.cygni.snake.SimpleSnakePlayer;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public abstract class Behavior {

  public static HashMap<SnakeDirection, Double> values = new HashMap<>();
  public static ArrayList<Behavior> behaviors = new ArrayList<>();

  static {
    values.put(SnakeDirection.UP, 0D);
    values.put(SnakeDirection.DOWN, 0D);
    values.put(SnakeDirection.LEFT, 0D);
    values.put(SnakeDirection.RIGHT, 0D);
  }

  public static SnakeDirection getBestMove(List<SnakeDirection> directions) {
    resetValues();
    callBehaviors(directions);

    double bestScore = Double.MIN_VALUE;
    SnakeDirection bestDirection = SnakeDirection.DOWN;

    for (SnakeDirection direction : values.keySet()) {
      if (directions.contains(direction) && values.get(direction) > bestScore) {
        bestScore = values.get(direction);
        bestDirection = direction;
      }
    }

    return bestDirection;
  }

  private static void callBehaviors(List<SnakeDirection> directions) {
    boolean relevant = false;

    for (Behavior b : behaviors) {
      HashMap<SnakeDirection, Double> behaviorValues = b.getValues(directions);

      if (relevant) {
        System.out.println();
        System.out.println(b.getClass().getName());
      }

      for (SnakeDirection direction : behaviorValues.keySet()) {
        if (relevant) System.out.println(direction + " " + behaviorValues.get(direction));
        values.put(direction, values.get(direction) + behaviorValues.get(direction));
      }
    }

    if (relevant) {
      System.out.println();
      for (SnakeDirection direction : values.keySet()) {
        System.out.println(direction + " " + values.get(direction));
      }
      System.out.println();
    }
  }

  private static void resetValues() {
    for (SnakeDirection direction : values.keySet()) {
      values.put(direction, 0D);
    }
  }


  protected final Tick tick;

  public Behavior(Tick tick) {
    this.tick = tick;

    behaviors.add(this);
  }

  public abstract HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions);
}
