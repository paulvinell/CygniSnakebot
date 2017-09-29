package se.cygni.snake.behaviors;

import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.Corridor;

public class CorridorAttackBehavior extends Behavior {

  public CorridorAttackBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    final HashMap<SnakeDirection, Double> values = new HashMap<>();

    final MapCoordinate curPos = tick.mapUtil.getMyPosition();

    Corridor bestCorridor = null;
    int bestDistance = Integer.MAX_VALUE;

    for (SnakeInfo snake : tick.mapUpdateEvent.getMap().getSnakeInfos()) {
      if (!snake.isAlive() || snake.getId() == tick.mapUpdateEvent.getReceivingPlayerId()) {
        continue;
      }

      Corridor cur = new Corridor(tick, snake);

      if (cur.isInCorridor()) {
        int distanceToEnd = curPos.getManhattanDistanceTo(cur.getOpenCorridorTile());

        if (distanceToEnd <= 6 && distanceToEnd <= cur.getCorridorLength() && bestDistance > distanceToEnd) {
          bestCorridor = cur;
          bestDistance = distanceToEnd;
        }
      }
    }

    if (bestCorridor != null) {
      int dX = 0;
      int dY = 0;

      if (curPos.getManhattanDistanceTo(bestCorridor.getLastCorridorTile())
          < curPos.getManhattanDistanceTo(bestCorridor.getOpenCorridorTile())) {
        dX = bestCorridor.getLastCorridorTile().x - curPos.x;
        dY = bestCorridor.getLastCorridorTile().y - curPos.y;
      } else {
        dX = bestCorridor.getOpenCorridorTile().x - curPos.x;
        dY = bestCorridor.getOpenCorridorTile().y - curPos.y;
      }

      final int dXs = (int) Math.signum(dX);
      final int dYs = (int) Math.signum(dY);

      double value = (Math.abs(dX) <= 1 && Math.abs(dY) <= 1) ? 3 : 0.75;

      if (dXs == 1) {
        values.put(SnakeDirection.RIGHT, value);
      } else if (dXs == -1) {
        values.put(SnakeDirection.LEFT, value);
      }
      if (dYs == 1) {
        values.put(SnakeDirection.DOWN, value);
      } else if (dYs == -1) {
        values.put(SnakeDirection.UP, value);
      }

      System.out.println("Kicking a fucker down in direction " + dX + " " + dY);
    }

    return values;
  }
}
