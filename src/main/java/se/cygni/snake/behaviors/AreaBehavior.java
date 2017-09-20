package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class AreaBehavior extends Behavior {

  public AreaBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();
    HashMap<SnakeDirection, Double> unprocessedMap = new HashMap<>();

    if (!tick.movement.hasEncounteredSeparatingObstacles()) {
      return unprocessedMap;
    }

    HashSet<Double> unprocessedValuesUnsorted = new HashSet<>();

    for (SnakeDirection direction : directions) {
      MapCoordinate posAfterMove = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      double areaAtPos = tick.area.getAreaFrom(posAfterMove).size();

      unprocessedMap.put(direction, areaAtPos);
      unprocessedValuesUnsorted.add(areaAtPos);
    }

    ArrayList<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending
    Collections.reverse(unprocessedValuesSorted); //now descending

    double value = 3;
    double decrement = 2;

    if (unprocessedValuesSorted.size() == 2) {
      double min = Math.min(unprocessedValuesSorted.get(0), unprocessedValuesSorted.get(1));
      double max = Math.max(unprocessedValuesSorted.get(0), unprocessedValuesSorted.get(1));

      if (min / max <= 0.1) {
        decrement = 3;
      }
    }

    for (double area : unprocessedValuesSorted) {
      for (SnakeDirection direction : unprocessedMap.keySet()) {
        if (unprocessedMap.get(direction) == area) {
          values.put(direction, value);
        }
      }

      value -= decrement;
      if (decrement > 1) {
        decrement--;
      }
    }

    return values;
  }
}
