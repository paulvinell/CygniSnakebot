package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.RelativeDirection.Direction;

public class IndirectAntiSnakeCollisionBehavior extends Behavior {

  public IndirectAntiSnakeCollisionBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(final List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    final SnakeDirection currentDirection = tick.relativeDirection.getCurrentSnakeDirection();

    if (directions.contains(currentDirection)) {
      final boolean left = isSnakeApproachingThroughPseudoCorridor(Direction.LEFT, 2)
          || isSnakeApproachingThroughPseudoCorridor(Direction.LEFT, 3);
      final boolean right = isSnakeApproachingThroughPseudoCorridor(Direction.RIGHT, 2)
          || isSnakeApproachingThroughPseudoCorridor(Direction.RIGHT, 3);

      if (left || right) {
        values.put(currentDirection, -0.5D);
      }
    }

    return values;
  }

  public final boolean isSnakeApproachingThroughPseudoCorridor(final Direction enemyDirection, final int forwardAmount) {
    MapCoordinate curP = tick.mapUtil.getMyPosition();

    for (int i = 0; i < forwardAmount; i++) {
      MapCoordinate enemySide = tick.relativeDirection.getCoordinateRelativeDirection(curP, enemyDirection);

      if (i != forwardAmount -1
          && i > 0
          && (!tick.movement.isTileAvailableForMovementTo(curP)
          || !tick.movement.isTileAvailableForMovementTo(enemySide))) {
        return false;
      }

      if (i == forwardAmount - 1 && tick.movement.isEnemyHeadAt(enemySide)) {
        return true;
      }

      curP = tick.relativeDirection.getCoordinateRelativeDirection(curP, Direction.FORWARD);
    }

    return false;
  }
}
