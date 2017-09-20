package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public class SnakeAmountBehavior extends Behavior {

  public SnakeAmountBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();

    MapCoordinate curC = tick.mapUtil.getMyPosition();

    int snakesLeft = 0;
    int snakesRight = 0;
    int snakesUp = 0;
    int snakesDown = 0;

    for (SnakeInfo enemy : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!enemy.isAlive()
          || enemy.getId().equals(tick.mapUpdateEvent.getReceivingPlayerId())) {
        continue;
      }

      MapCoordinate headPos = tick.mapUtil.translatePosition(enemy.getPositions()[0]);

      if (headPos.x < curC.x) {
        snakesLeft++;
      } else if (headPos.x > curC.x) {
        snakesRight++;
      }

      if (headPos.y < curC.y) {
        snakesUp++;
      } else if (headPos.y > curC.y) {
        snakesDown++;
      }
    }

    if (snakesLeft > snakesRight) {
      values.put(SnakeDirection.RIGHT, 0.25D);
    } else {
      values.put(SnakeDirection.LEFT, 0.25D);
    }

    if (snakesDown > snakesUp) {
      values.put(SnakeDirection.UP, 0.25D);
    } else {
      values.put(SnakeDirection.DOWN, 0.25D);
    }

    return values;
  }
}
