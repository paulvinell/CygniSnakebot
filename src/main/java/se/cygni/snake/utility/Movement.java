package se.cygni.snake.utility;

import java.util.Arrays;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public final class Movement {

  private final Tick tick;

  public Movement(Tick tick) {
    this.tick = tick;
  }

  public final MapCoordinate getNewCoordinate(final SnakeDirection direction, final MapCoordinate coordinate) {
    switch (direction) {
      case UP:
        return coordinate.translateBy(0 , -1);
      case DOWN:
        return coordinate.translateBy(0 , 1);
      case LEFT:
        return coordinate.translateBy(-1 , 0);
      case RIGHT:
        return coordinate.translateBy(1 , 0);
    }
    return null;
  }

  public final boolean isTileAvailableForMovementTo(final MapCoordinate coordinate) {
    final int position = tick.coordinates.translateCoordinate(coordinate);

    snakeCheck:
    for (final SnakeInfo snake : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!snake.isAlive()) {
        continue;
      }

      final int[] positions = snake.getPositions();
      for (int i = 0; i < positions.length; i++) {
        if (position == positions[i]) {
          if (i < positions.length - 1) {
            final int distance = tick.mapUtil.getMyPosition().getManhattanDistanceTo(coordinate);

            if (((positions.length - 1) - i) + Math.ceil((double) distance / getGrowthFrequency()) + 1 < distance) {
              return true;
            }
          } else if (positions.length > 1
              && snake.getTailProtectedForGameTicks() == 0
              && !snake.getId().equals(tick.mapUpdateEvent.getReceivingPlayerId())) {
            return true;
          }

          break snakeCheck;
        }
      }
    }

    return tick.mapUtil.isTileAvailableForMovementTo(coordinate);
  }

  public final int getGrowthFrequency() {
    return tick.gameSettings.getSpontaneousGrowthEveryNWorldTick();
  }

  public final boolean canIMoveInDirection(final SnakeDirection direction) {
    final MapCoordinate myPos = tick.mapUtil.getMyPosition();

    switch (direction) {
      case DOWN:
        return isTileAvailableForMovementTo(myPos.translateBy(0, 1));
      case UP:
        return isTileAvailableForMovementTo(myPos.translateBy(0, -1));
      case LEFT:
        return isTileAvailableForMovementTo(myPos.translateBy(-1, 0));
      case RIGHT:
        return isTileAvailableForMovementTo(myPos.translateBy(1, 0));
    }

    return false;
  }

  public final boolean hasEncounteredSeparatingObstacles() {
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        final MapCoordinate curPos = new MapCoordinate(tick.mapUtil.getMyPosition().x + x, tick.mapUtil.getMyPosition().y + y);

        if (!isTileAvailableForMovementTo(curPos)
            && !tick.snakeHandler.isPartOfThisHeadOrNeck(curPos)) {
          return true;
        }
      }
    }

    return false;
  }

  public final boolean isAnObstacle(final MapCoordinate coordinate) {
    for (final MapCoordinate curC : tick.mapUtil.listCoordinatesContainingObstacle()) {
      if (curC.x == coordinate.x && curC.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }
}
