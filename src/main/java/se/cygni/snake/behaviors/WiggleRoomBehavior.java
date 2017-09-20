package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class WiggleRoomBehavior extends Behavior {

  public WiggleRoomBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (SnakeDirection direction : directions) {
      MapCoordinate resultingPos = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      if (resultingPos.x > 0 && resultingPos.x < tick.mapUpdateEvent.getMap().getWidth() - 1
          && resultingPos.y > 0 && resultingPos.y < tick.mapUpdateEvent.getMap().getHeight() - 1) {
        values.put(direction, 0.5D);
      }
    }

    return values;
  }
}
