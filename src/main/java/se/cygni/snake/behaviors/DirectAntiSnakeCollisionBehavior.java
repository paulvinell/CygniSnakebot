package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public final class DirectAntiSnakeCollisionBehavior extends Behavior {

  public DirectAntiSnakeCollisionBehavior(Tick tick) {
    super(tick);
  }

  @Override
  protected final boolean canRun() {
    return true;
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (SnakeDirection direction : directions) {
      final boolean headOnCollision = canCollideWithEnemyHeadInDirection(direction, 2);
      final boolean sideCollision = canCollideWithCloseEnemyHead(direction);

      if (headOnCollision && sideCollision) {
        values.put(direction, -1D);
      } else if (headOnCollision || sideCollision) {
        values.put(direction, 0D);
      } else {
        values.put(direction, 1D);
      }
    }

    return values;
  }

  public final boolean canCollideWithCloseEnemyHead(final SnakeDirection direction) {
    MapCoordinate curC = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

    MapCoordinate sideOne;
    MapCoordinate sideTwo;

    if (direction.equals(SnakeDirection.UP) || direction.equals(SnakeDirection.DOWN)) {
      sideOne = curC.translateBy(1, 0);
      sideTwo = curC.translateBy(-1, 0);
    } else {
      sideOne = curC.translateBy(0, 1);
      sideTwo = curC.translateBy(0, -1);
    }

    return tick.snakeHandler.isEnemyHeadAt(sideOne) || tick.snakeHandler.isEnemyHeadAt(sideTwo);
  }

  public final boolean canCollideWithEnemyHeadInDirection(final SnakeDirection direction, final int steps) {
    MapCoordinate curPos = tick.mapUtil.getMyPosition();

    for (int i = 0; i < steps; i++) {
      curPos = tick.movement.getNewCoordinate(direction, curPos);

      if (tick.movement.isAnObstacle(curPos) || tick.mapUtil.isCoordinateOutOfBounds(curPos)) {
        return false;
      }
    }

    return tick.snakeHandler.isEnemyHeadAt(curPos);
  }
}
