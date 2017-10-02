package se.cygni.snake.utility;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public class SnakeHandler {

  private Tick tick;

  public SnakeHandler(Tick tick) {
    this.tick = tick;
  }

  public final boolean isSnakeWinning() {
    return getWinningSnake().getId().equals(tick.mapUpdateEvent.getReceivingPlayerId());
  }

  public final SnakeInfo getWinningSnake() {
    SnakeInfo winningSnake = null;

    for (SnakeInfo snakeInfo : getAliveSnakes()) {
      if (winningSnake == null || winningSnake.getPoints() < snakeInfo.getPoints()) {
        winningSnake = snakeInfo;
      }
    }

    return winningSnake;
  }

  public final List<SnakeInfo> getAliveSnakes() {
    return Arrays.stream(tick.mapUpdateEvent.getMap().getSnakeInfos())
        .filter(snakeInfo -> snakeInfo.isAlive()).collect(Collectors.toList());
  }

  public final boolean isPartOfThisHeadOrNeck(final MapCoordinate coordinate) {
    final MapCoordinate[] snakeSpread = tick.mapUtil.getSnakeSpread(tick.mapUpdateEvent.getReceivingPlayerId());

    return (snakeSpread[0].x == coordinate.x && snakeSpread[0].y == coordinate.y)
        || (snakeSpread.length > 1 && snakeSpread[1].x == coordinate.x && snakeSpread[1].y == coordinate.y);
  }

  public final boolean isEnemyHeadAt(final MapCoordinate coordinate) {
    for (final SnakeInfo enemy : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!enemy.isAlive()
          || enemy.getId().equals(tick.mapUpdateEvent.getReceivingPlayerId())) {
        continue;
      }

      final MapCoordinate headPos = tick.mapUtil.translatePosition(enemy.getPositions()[0]);

      if (headPos.x == coordinate.x && headPos.y == coordinate.y) {
        return true;
      }
    }

    return false;
  }

  public final SnakeInfo getSnake(final MapCoordinate coordinate) {
    for (final SnakeInfo enemy : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!enemy.isAlive()) {
        continue;
      }

      for (final int enemyPos : enemy.getPositions()) {
        final MapCoordinate enemyC = tick.mapUtil.translatePosition(enemyPos);

        if (enemyC.x == coordinate.x && enemyC.y == coordinate.y) {
          return enemy;
        }
      }
    }

    return null;
  }
}
