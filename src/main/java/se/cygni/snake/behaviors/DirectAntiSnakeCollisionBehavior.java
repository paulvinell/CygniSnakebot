package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class DirectAntiSnakeCollisionBehavior extends Behavior {

  public DirectAntiSnakeCollisionBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (SnakeDirection direction : directions) {
      boolean headOnCollision = canCollideWithEnemyHeadInDirection(direction, 2);
      boolean sideCollision = canCollideWithCloseEnemyHead(direction);

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

  public boolean canCollideWithCloseEnemyHead(SnakeDirection direction) {
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

    return tick.movement.isEnemyHeadAt(sideOne) || tick.movement.isEnemyHeadAt(sideTwo);
  }

  public boolean canCollideWithEnemyHeadInDirection(SnakeDirection direction, int steps) {
    MapCoordinate curPos = tick.mapUtil.getMyPosition();

    for (int i = 0; i < steps; i++) {
      curPos = tick.movement.getNewCoordinate(direction, curPos);

      if (tick.movement.isAnObstacle(curPos) || tick.mapUtil.isCoordinateOutOfBounds(curPos)) {
        return false;
      }
    }

    return tick.movement.isEnemyHeadAt(curPos);
  }
}
