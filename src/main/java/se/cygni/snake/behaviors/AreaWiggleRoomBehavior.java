package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.Area;

public final class AreaWiggleRoomBehavior extends Behavior {

  public AreaWiggleRoomBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final Area area = new Area(tick);
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    final MapCoordinate curC = tick.mapUtil.getMyPosition();

    final HashSet<Double> unprocessedValuesUnsorted = new HashSet<>();

    if (directions.contains(SnakeDirection.LEFT) && directions.contains(SnakeDirection.RIGHT)
        && (curC.y == 1 || curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2)) {
      if (curC.y == 1) {
        area.artificialObstacles.add(curC.translateBy(0, -1));
      } else if (curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2) {
        area.artificialObstacles.add(curC.translateBy(0, 1));
      }

      final double areaLeft = area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.LEFT, curC)).size();
      final double areaRight = area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.RIGHT, curC)).size();

      values.put(SnakeDirection.LEFT, areaLeft);
      values.put(SnakeDirection.RIGHT, areaRight);
      unprocessedValuesUnsorted.add(areaLeft);
      unprocessedValuesUnsorted.add(areaRight);

    } else if (directions.contains(SnakeDirection.UP) && directions.contains(SnakeDirection.DOWN)
        && (curC.x == 1 || curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2)) {
      if (curC.x == 1) {
        area.artificialObstacles.add(curC.translateBy(-1, 0));
      } else if (curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2) {
        area.artificialObstacles.add(curC.translateBy(1, 0));
      }

      final double areaUp = area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.UP, curC)).size();
      final double areaDown = area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.DOWN, curC)).size();

      values.put(SnakeDirection.UP, areaUp);
      values.put(SnakeDirection.DOWN, areaDown);
      unprocessedValuesUnsorted.add(areaUp);
      unprocessedValuesUnsorted.add(areaDown);
    } else {
      return values;
    }

    final ArrayList<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending

    double value = 0;

    for (double curArea : unprocessedValuesSorted) {
      for (SnakeDirection direction : values.keySet()) {
        if (values.get(direction) == curArea) {
          values.put(direction, value);
        }
      }

      value++;
    }

    return values;
  }
}
