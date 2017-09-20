package se.cygni.snake.utility;

import java.util.ArrayList;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class RelativeDirection {

  public enum Direction {
    FORWARD, RIGHT, BACK, LEFT
  }

  private SnakeDirection[] trueDirections = new SnakeDirection[4];
  private Direction[] relativeDirections = new Direction[4];

  private Tick tick;

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

  /**
   * @param position Current position on map.
   * @param dx The change in x. Negative values go left, positive right.
   * @param dy The change in y. Negative values go back, positive forward.
   * @return
   */
  public MapCoordinate translateRelativeDirection(MapCoordinate position, int dx, int dy) {
    Direction direction = getRelativeDirection(getCurrentSnakeDirection());

    switch (direction) {
      case FORWARD:
        break;
      case RIGHT:
        break;
      case BACK:
        break;
      case LEFT:
        break;
    }

    return null;
  }

  public Direction getRelativeDirection(SnakeDirection direction) {
    SnakeDirection curD = getCurrentSnakeDirection();

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

  public SnakeDirection getCurrentSnakeDirection() {
    MapCoordinate[] coordinates = tick.mapUtil
        .getSnakeSpread(tick.mapUpdateEvent.getReceivingPlayerId());

    if (coordinates.length > 1) {
      int dX = coordinates[0].x - coordinates[1].x;
      int dY = coordinates[0].y - coordinates[1].y;

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
}
