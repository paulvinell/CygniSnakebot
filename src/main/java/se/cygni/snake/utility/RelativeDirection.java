package se.cygni.snake.utility;

import java.util.ArrayList;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class RelativeDirection {

  public enum Direction {
    FORWARD, RIGHT, BACK, LEFT
  }

  private final SnakeDirection[] trueDirections = new SnakeDirection[4];
  private final Direction[] relativeDirections = new Direction[4];

  private final Tick tick;

  public RelativeDirection(Tick tick) {
    this.tick = tick;

    trueDirections[0] = SnakeDirection.UP;
    trueDirections[1] = SnakeDirection.RIGHT;
    trueDirections[2] = SnakeDirection.DOWN;
    trueDirections[3] = SnakeDirection.LEFT;

    relativeDirections[0] = Direction.FORWARD;
    relativeDirections[1] = Direction.RIGHT;
    relativeDirections[2] = Direction.BACK;
    relativeDirections[3] = Direction.LEFT;
  }

  public final MapCoordinate getCoordinateRelativeDirection(final MapCoordinate position, final Direction direction) {
    switch (direction) {
      case FORWARD:
        return translateRelativeDirection(position, 0, 1);
      case RIGHT:
        return translateRelativeDirection(position, 1, 0);
      case BACK:
        return translateRelativeDirection(position, 0, -1);
      case LEFT:
        return translateRelativeDirection(position, -1, 0);
    }

    return null;
  }

  /**
   * @param position Current position on map.
   * @param dx The change in x. Negative values go left, positive right.
   * @param dy The change in y. Negative values go back, positive forward.
   * @return
   */
  public final MapCoordinate translateRelativeDirection(final MapCoordinate position, final int dx, final int dy) {
    final SnakeDirection direction = getCurrentSnakeDirection();

    switch (direction) {
      case UP:
        return position.translateBy(dx, -dy);
      case RIGHT:
        return position.translateBy(dy, dx);
      case DOWN:
        return position.translateBy(-dx, dy);
      case LEFT:
        return position.translateBy(-dy, -dx);
    }

    return null;
  }

  public final Direction getRelativeDirection(final SnakeDirection direction) {
    final SnakeDirection curD = getCurrentSnakeDirection();

    int trueDirection = 0;
    for (SnakeDirection snakeDirection : trueDirections) {
      if (snakeDirection == curD) {
        break;
      } else {
        trueDirection++;
      }
    }

    for (int i = 0; i < 4; i++) {
      if (trueDirections[(trueDirection + i) % 4] == direction) {
        return relativeDirections[i];
      }
    }

    return Direction.BACK;
  }

  public final SnakeDirection getCurrentSnakeDirection() {
    final MapCoordinate[] coordinates = tick.mapUtil
        .getSnakeSpread(tick.mapUpdateEvent.getReceivingPlayerId());

    if (coordinates.length > 1) {
      final int dX = coordinates[0].x - coordinates[1].x;
      final int dY = coordinates[0].y - coordinates[1].y;

      if (dX == 1) {
        return SnakeDirection.RIGHT;
      } else if (dX == -1) {
        return SnakeDirection.LEFT;
      } else if (dY == 1) {
        return SnakeDirection.DOWN;
      } else if (dY == -1) {
        return SnakeDirection.UP;
      }
    }

    return SnakeDirection.DOWN;
  }

  public final Direction getOppositeRelativeDirection(final Direction direction) {
    switch (direction) {
      case FORWARD:
        return Direction.BACK;
      case RIGHT:
        return Direction.LEFT;
      case BACK:
        return Direction.FORWARD;
      case LEFT:
        return Direction.RIGHT;
    }

    return null;
  }
}
