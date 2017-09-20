package se.cygni.snake.utility;

import se.cygni.snake.Tick;
import se.cygni.snake.client.MapCoordinate;

public class Coordinates {

  private Tick tick;

  public Coordinates(Tick tick) {
    this.tick = tick;
  }

  public final int translateCoordinate(final MapCoordinate coordinate) {
    return coordinate.x + coordinate.y * tick.mapUpdateEvent.getMap().getWidth();
  }
}
