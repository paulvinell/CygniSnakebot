package se.cygni.snake.utility;

import java.util.ArrayList;
import java.util.HashSet;
import se.cygni.snake.Tick;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;

public class Area {

  private final Tick tick;

  public final ArrayList<MapCoordinate> artificialObstacles = new ArrayList<>();

  public Area(Tick tick) {
    this.tick = tick;
  }

  public final HashSet<Integer> getAreaFrom(final MapCoordinate c) {
    final HashSet<Integer> traversable = new HashSet<>();

    traversable.add(tick.coordinates.translateCoordinate(c));

    int lastSize = -1;
    while (lastSize != traversable.size()) {
      lastSize = traversable.size();

      for (final Integer curC : (HashSet<Integer>) traversable.clone()) {
        final MapCoordinate mapC = tick.mapUtil.translatePosition(curC);

        final ArrayList<MapCoordinate> moves = new ArrayList<>();

        moves.add(mapC.translateBy(-1, 0));
        moves.add(mapC.translateBy(1, 0));
        moves.add(mapC.translateBy(0, -1));
        moves.add(mapC.translateBy(0, 1));

        for (final MapCoordinate move : moves) {
          if (tick.movement.isTileAvailableForMovementTo(move)) {
            traversable.add(
                tick.coordinates.translateCoordinate(move));
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