package se.cygni.snake.utility;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

public final class Area {

  private final Tick tick;

  public final List<MapCoordinate> artificialObstacles = new ArrayList<>();
  public final List<MapCoordinate> allowedTiles = new ArrayList<>();

  public Area(Tick tick) {
    this.tick = tick;
  }

  public final HashSet<Integer> getAreaFrom(final MapCoordinate c) {
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

        final List<MapCoordinate> moves = new ArrayList<>();

        moves.add(mapC.translateBy(-1, 0));
        moves.add(mapC.translateBy(1, 0));
        moves.add(mapC.translateBy(0, -1));
        moves.add(mapC.translateBy(0, 1));

        for (final MapCoordinate move : moves) {
          if ((!isArtificialObstacle(move) &&
              tick.movement.isTileAvailableForMovementTo(move))
              || isAllowedTile(move)) {
            final Integer currentPosition = tick.coordinates.translateCoordinate(move);

            final int traversableSize = traversable.size();
            traversable.add(currentPosition);

            if (traversable.size() > traversableSize) {
              current.add(currentPosition);
            }
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

  public final boolean isAllowedTile(final MapCoordinate coordinate) {
    for (final MapCoordinate curC : allowedTiles) {
      if (curC.x == coordinate.x && curC.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }
}
