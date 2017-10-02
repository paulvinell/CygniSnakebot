package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.Corridor;
import se.cygni.snake.utility.astar.Pathfinder;

public final class CorridorAttackBehavior extends Behavior {

  public CorridorAttackBehavior(Tick tick) {
    super(tick);
  }

  @Override
  protected final boolean canRun() {
    return true;
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    final MapCoordinate curPos = tick.mapUtil.getMyPosition();

    Corridor bestCorridor = null;
    Pathfinder bestPath = null;

    for (final SnakeInfo snake : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!snake.isAlive() || snake.getId() == tick.mapUpdateEvent.getReceivingPlayerId()) {
        continue;
      }

      final Corridor cur = new Corridor(tick, snake);

      if (cur.isInCorridor()
          && tick.mapUtil.translatePosition(snake.getPositions()[0])
          .getManhattanDistanceTo(curPos) > 2) {
        Pathfinder path = null;

        /* If the snake causes the corridor itself,
          it can never reach lastCorridorTile, so go to openCorridorTile instead */
        if (curPos.getManhattanDistanceTo(cur.getOpenCorridorTile()) == 2
            && curPos.getManhattanDistanceTo(cur.getLastCorridorTile()) == 1) {
          path = new Pathfinder(tick, curPos, cur.getLastCorridorTile());
        } else {
          path = new Pathfinder(tick, curPos, cur.getOpenCorridorTile());
        }

        if (path.isReachable()
            && path.path.size() <= cur.getCorridorLength()
            && ((bestCorridor == null || bestPath == null)
            || bestPath.path.size() > path.path.size())) {
          bestCorridor = cur;
          bestPath = path;
        }
      }
    }

    if (bestCorridor != null) {
      MapCoordinate nextTile = bestPath.path.get(0).coordinate;

      final int dX = (int) Math.signum(nextTile.x - curPos.x);
      final int dY = (int) Math.signum(nextTile.y - curPos.y);

      double value = (bestPath.path.size() == 1) ? 3 : 0.75;

      if (dX == 1) {
        values.put(SnakeDirection.RIGHT, value);
      } else if (dX == -1) {
        values.put(SnakeDirection.LEFT, value);
      }
      if (dY == 1) {
        values.put(SnakeDirection.DOWN, value);
      } else if (dY == -1) {
        values.put(SnakeDirection.UP, value);
      }
    }

    return values;
  }
}
