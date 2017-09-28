package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public final class RoomBehavior extends Behavior {

  public RoomBehavior(Tick tick) {
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

    for (final SnakeDirection direction : directions) {
      final MapCoordinate posAfterMove = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      final double roomAtPos = tick.room.getRoomFrom(posAfterMove).size();

      unprocessedMap.put(direction, roomAtPos);
      unprocessedValuesUnsorted.add(roomAtPos);
    }

    final List<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending

    double value = 0;

    for (final double room : unprocessedValuesSorted) {
      for (final SnakeDirection direction : unprocessedMap.keySet()) {
        if (unprocessedMap.get(direction) == room) {
          values.put(direction, value);
        }
      }

      value += 0.75;
    }

    return values;
  }
}
