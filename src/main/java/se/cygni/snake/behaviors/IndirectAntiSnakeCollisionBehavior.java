package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.RelativeDirection;
import se.cygni.snake.utility.RelativeDirection.Direction;

public class IndirectAntiSnakeCollisionBehavior extends Behavior {

  public IndirectAntiSnakeCollisionBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (SnakeDirection direction : directions) {
      final Direction currentDirection = tick.relativeDirection.getRelativeDirection(direction);

      boolean left = false;
      boolean right = false;

      switch (currentDirection) {
        case LEFT:
          right = isSnakeApproachingThroughPseudoCorridor(direction, Direction.RIGHT, 2)
              || isSnakeApproachingThroughPseudoCorridor(direction, Direction.RIGHT, 3);
          break;
        case RIGHT:
          left = isSnakeApproachingThroughPseudoCorridor(direction, Direction.LEFT, 2)
              || isSnakeApproachingThroughPseudoCorridor(direction, Direction.LEFT, 3);
          break;
        default:
          left = isSnakeApproachingThroughPseudoCorridor(direction, Direction.LEFT, 2)
              || isSnakeApproachingThroughPseudoCorridor(direction, Direction.LEFT, 3);
          right = isSnakeApproachingThroughPseudoCorridor(direction, Direction.RIGHT, 2)
              || isSnakeApproachingThroughPseudoCorridor(direction, Direction.RIGHT, 3);
      }

      if (left || right) {
        values.put(direction, -0.5D);
      }
    }

    return values;
  }

  public final boolean isSnakeApproachingThroughPseudoCorridor(final SnakeDirection checkDirection, final Direction enemyDirection, final int forwardAmount) {
    MapCoordinate curP = tick.mapUtil.getMyPosition();
    final RelativeDirection relativeDirection = new RelativeDirection(checkDirection, tick);

    for (int i = 0; i < forwardAmount; i++) {
      final MapCoordinate enemySide = relativeDirection.getCoordinateRelativeDirection(curP, enemyDirection);

      if (i != forwardAmount -1
          && i > 0
          && (!tick.movement.isTileAvailableForMovementTo(curP)
          || !tick.movement.isTileAvailableForMovementTo(enemySide))) {
        return false;
      }

      if (i == forwardAmount - 1 && tick.movement.isEnemyHeadAt(enemySide)) {
        return true;
      }

      curP = relativeDirection.getCoordinateRelativeDirection(curP, Direction.FORWARD);
    }

    return false;
  }
}
