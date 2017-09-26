package se.cygni.snake.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class Room {

  private final Tick tick;

  public final ArrayList<MapCoordinate> artificialObstacles = new ArrayList<>();

  public Room(Tick tick) {
    this.tick = tick;
  }

  public final HashSet<Integer> getRoomFrom(final MapCoordinate c) {
    final HashSet<Integer> traversable = new HashSet<>();
    final HashSet<Integer> current = new HashSet<>();

    final Integer startPosition = tick.coordinates.translateCoordinate(c);

    traversable.add(startPosition);
    current.add(startPosition);

    int lastSize = -1;
    while (lastSize != traversable.size()) {
      lastSize = traversable.size();

      final HashSet<Integer> iterator = (HashSet<Integer>) current.clone();
      current.clear();

      for (final Integer curC : iterator) {
        final MapCoordinate mapC = tick.mapUtil.translatePosition(curC);

        final ArrayList<SnakeDirection> moves = new ArrayList<>();

        moves.add(SnakeDirection.UP);
        moves.add(SnakeDirection.DOWN);
        moves.add(SnakeDirection.LEFT);
        moves.add(SnakeDirection.RIGHT);

        for (final SnakeDirection move : (List<SnakeDirection>) moves.clone()) {
          if (!tick.movement.isTileAvailableForMovementTo(
              tick.movement.getNewCoordinate(move, mapC))) {
            moves.remove(move);
          }
        }

        boolean containsVertical = moves.contains(SnakeDirection.UP) || moves.contains(SnakeDirection.DOWN);
        boolean containsHorizontal = moves.contains(SnakeDirection.LEFT) || moves.contains(SnakeDirection.RIGHT);

        if (containsVertical && containsHorizontal) {
          for (final SnakeDirection move : moves) {
            final Integer currentPosition
                = tick.coordinates.translateCoordinate(
                    tick.movement.getNewCoordinate(move, mapC));

            traversable.add(currentPosition);
            current.add(currentPosition);
          }
        }
      }
    }

    return traversable;
  }

  public final boolean isArtificialObstacle(final MapCoordinate coordinate) {
    for (final MapCoordinate curC : artificialObstacles) {
      if (curC.x == coordinate.x && curC.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }
}
