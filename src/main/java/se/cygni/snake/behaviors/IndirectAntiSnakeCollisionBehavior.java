package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;

public class IndirectAntiSnakeCollisionBehavior extends Behavior {

  public IndirectAntiSnakeCollisionBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();
    //http://game.snake.cygni.se/#/viewgame/d1114dca-97fd-46a8-8163-dcd5ae38b093?_k=t1y2qe for instance
    return values;
  }
}
