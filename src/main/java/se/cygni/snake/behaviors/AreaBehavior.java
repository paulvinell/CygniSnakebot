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
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();
    final HashMap<SnakeDirection, Double> unprocessedMap = new HashMap<>();

    if (!tick.movement.hasEncounteredSeparatingObstacles()) {
      return unprocessedMap;
    }

    final HashSet<Double> unprocessedValuesUnsorted = new HashSet<>();

    for (SnakeDirection direction : directions) {
      MapCoordinate posAfterMove = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      double areaAtPos = tick.area.getAreaFrom(posAfterMove).size();

      unprocessedMap.put(direction, areaAtPos);
      unprocessedValuesUnsorted.add(areaAtPos);
    }

    final ArrayList<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending
    Collections.reverse(unprocessedValuesSorted); //now descending

    double value = 3;
    double decrement = 2;

    if (unprocessedValuesSorted.size() == 2) {
      final double min = Math.min(unprocessedValuesSorted.get(0), unprocessedValuesSorted.get(1));
      final double max = Math.max(unprocessedValuesSorted.get(0), unprocessedValuesSorted.get(1));

      if (min / max <= 0.1) {
        decrement = 3;
      }
    }

    for (final double area : unprocessedValuesSorted) {
      for (final SnakeDirection direction : unprocessedMap.keySet()) {
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
