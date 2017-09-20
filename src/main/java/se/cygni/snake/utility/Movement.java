package se.cygni.snake.utility;

import se.cygni.snake.Tick;
import se.cygni.snake.api.model.Map;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public class Movement {

  private Tick tick;

  public Movement(Tick tick) {
    this.tick = tick;
  }

  public MapCoordinate getNewCoordinate(SnakeDirection direction, MapCoordinate coordinate) {
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

  public boolean hasEncounteredSeparatingObstacles() {
    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        MapCoordinate curPos = new MapCoordinate(tick.mapUtil.getMyPosition().x + x, tick.mapUtil.getMyPosition().y + y);

        if (!tick.mapUtil.isTileAvailableForMovementTo(curPos)
            && !isPartOfThisHeadOrNeck(curPos)) {
          return true;
        }
      }
    }

    return false;
  }

  public boolean isPartOfThisHeadOrNeck(MapCoordinate coordinate) {
    MapCoordinate[] snakeSpread = tick.mapUtil.getSnakeSpread(tick.mapUpdateEvent.getReceivingPlayerId());

    if (snakeSpread[0].x == coordinate.x && snakeSpread[0].y == coordinate.y) {
      return true;
    } else if(snakeSpread.length > 1 && snakeSpread[1].x == coordinate.x && snakeSpread[1].y == coordinate.y) {
      return true;
    }

    return false;
  }

  public boolean isPartOfThisSnake(MapCoordinate coordinate) {
    for (MapCoordinate curC : tick.mapUtil.getSnakeSpread(tick.mapUpdateEvent.getReceivingPlayerId())) {
      if (curC.x == coordinate.x && curC.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }

  public boolean isEnemyHeadAt(MapCoordinate coordinate) {
    for (SnakeInfo enemy : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!enemy.isAlive()
          || enemy.getId().equals(tick.mapUpdateEvent.getReceivingPlayerId())) {
        continue;
      }

      MapCoordinate headPos = tick.mapUtil.translatePosition(enemy.getPositions()[0]);

      if (headPos.x == coordinate.x && headPos.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }

  public boolean isAnObstacle(MapCoordinate coordinate) {
    for (MapCoordinate curC : tick.mapUtil.listCoordinatesContainingObstacle()) {
      if (curC.x == coordinate.x && curC.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }
}
