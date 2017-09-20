package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class AreaWiggleRoomBehavior extends Behavior {

  public AreaWiggleRoomBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();

    MapCoordinate curC = tick.mapUtil.getMyPosition();

    HashSet<Double> unprocessedValuesUnsorted = new HashSet<>();

    if (directions.contains(SnakeDirection.LEFT) && directions.contains(SnakeDirection.RIGHT)
        && (curC.y == 1 || curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2)) {
      if (curC.y == 1) {
        tick.area.artificialObstacles.add(curC.translateBy(0, -1));
      } else if (curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2) {
        tick.area.artificialObstacles.add(curC.translateBy(0, 1));
      }

      double areaLeft = tick.area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.LEFT, curC)).size();
      double areaRight = tick.area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.RIGHT, curC)).size();

      values.put(SnakeDirection.LEFT, areaLeft);
      values.put(SnakeDirection.RIGHT, areaRight);
      unprocessedValuesUnsorted.add(areaLeft);
      unprocessedValuesUnsorted.add(areaRight);

    } else if (directions.contains(SnakeDirection.UP) && directions.contains(SnakeDirection.DOWN)
        && (curC.x == 1 || curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2)) {
      if (curC.x == 1) {
        tick.area.artificialObstacles.add(curC.translateBy(-1, 0));
      } else if (curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2) {
        tick.area.artificialObstacles.add(curC.translateBy(1, 0));
      }

      double areaUp = tick.area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.UP, curC)).size();
      double areaDown = tick.area.getAreaFrom(tick.movement.getNewCoordinate(SnakeDirection.DOWN, curC)).size();

      values.put(SnakeDirection.UP, areaUp);
      values.put(SnakeDirection.DOWN, areaDown);
      unprocessedValuesUnsorted.add(areaUp);
      unprocessedValuesUnsorted.add(areaDown);
    } else {
      return values;
    }

    ArrayList<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending

    double value = 0;

    for (double area : unprocessedValuesSorted) {
      for (SnakeDirection direction : values.keySet()) {
        if (values.get(direction) == area) {
          values.put(direction, value);
        }
      }

      value++;
    }

    tick.area.artificialObstacles.clear();

    return values;
  }
}
