package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public final class WiggleRoomAreaBehavior extends Behavior {

  public WiggleRoomAreaBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public final HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (final SnakeDirection direction : directions) {
      final MapCoordinate resultingPos = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      if (resultingPos.x > 0 && resultingPos.x < tick.mapUpdateEvent.getMap().getWidth() - 1
          && resultingPos.y > 0 && resultingPos.y < tick.mapUpdateEvent.getMap().getHeight() - 1) {
        values.put(direction, 0.5D);
      }
    }

    return values;
  }
}
