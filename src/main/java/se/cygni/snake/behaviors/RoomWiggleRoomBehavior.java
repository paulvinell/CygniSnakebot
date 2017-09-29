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
import se.cygni.snake.utility.Room;

public class RoomWiggleRoomBehavior extends Behavior {

  public RoomWiggleRoomBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final Room room = new Room(tick);
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    final MapCoordinate curC = tick.mapUtil.getMyPosition();

    final HashSet<Double> unprocessedValuesUnsorted = new HashSet<>();

    if (directions.contains(SnakeDirection.LEFT) && directions.contains(SnakeDirection.RIGHT)
        && (curC.y == 1 || curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2)) {
      if (curC.y == 1) {
        room.artificialObstacles.add(curC.translateBy(0, -1));
      } else if (curC.y == tick.mapUpdateEvent.getMap().getHeight() - 2) {
        room.artificialObstacles.add(curC.translateBy(0, 1));
      }

      final double roomLeft = room.getRoomFrom(tick.movement.getNewCoordinate(SnakeDirection.LEFT, curC)).size();
      final double roomRight = room.getRoomFrom(tick.movement.getNewCoordinate(SnakeDirection.RIGHT, curC)).size();

      values.put(SnakeDirection.LEFT, roomLeft);
      values.put(SnakeDirection.RIGHT, roomRight);
      unprocessedValuesUnsorted.add(roomLeft);
      unprocessedValuesUnsorted.add(roomRight);

    } else if (directions.contains(SnakeDirection.UP) && directions.contains(SnakeDirection.DOWN)
        && (curC.x == 1 || curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2)) {
      if (curC.x == 1) {
        room.artificialObstacles.add(curC.translateBy(-1, 0));
      } else if (curC.x == tick.mapUpdateEvent.getMap().getWidth() - 2) {
        room.artificialObstacles.add(curC.translateBy(1, 0));
      }

      final double roomUp = room.getRoomFrom(tick.movement.getNewCoordinate(SnakeDirection.UP, curC)).size();
      final double roomDown = room.getRoomFrom(tick.movement.getNewCoordinate(SnakeDirection.DOWN, curC)).size();

      values.put(SnakeDirection.UP, roomUp);
      values.put(SnakeDirection.DOWN, roomDown);
      unprocessedValuesUnsorted.add(roomUp);
      unprocessedValuesUnsorted.add(roomDown);
    } else {
      return values;
    }

    final ArrayList<Double> unprocessedValuesSorted = new ArrayList<>();
    unprocessedValuesSorted.addAll(unprocessedValuesUnsorted);
    Collections.sort(unprocessedValuesSorted); //ascending

    double value = 0;

    for (double curRoom : unprocessedValuesSorted) {
      for (SnakeDirection direction : values.keySet()) {
        if (values.get(direction) == curRoom) {
          values.put(direction, value);
        }
      }

      value += 0.75D;
    }

    return values;
  }
}
