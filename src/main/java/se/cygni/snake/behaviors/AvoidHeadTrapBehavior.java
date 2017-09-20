package se.cygni.snake.behaviors;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;

public class AvoidHeadTrapBehavior extends Behavior {

  public AvoidHeadTrapBehavior(Tick tick) {
    super(tick);
  }

  @Override
  public HashMap<SnakeDirection, Double> getValues(List<SnakeDirection> directions) {
    HashMap<SnakeDirection, Double> values = new HashMap<>();

    for (SnakeDirection direction : directions) {
      boolean headTrapSide = isVulnerableToHeadTrapSide(direction);
      boolean headTrapFront = isVulnerableToHeadTrapFront(direction);

      if (headTrapSide && headTrapFront) {
        values.put(direction, -1D);
      } else if (headTrapSide || headTrapFront) {
        values.put(direction, 0D);
      } else {
        values.put(direction, 1D);
      }
    }

    return values;
  }

  public boolean isVulnerableToHeadTrapFront(SnakeDirection direction) {
    MapCoordinate oneAhead = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());
    MapCoordinate twoAhead = tick.movement.getNewCoordinate(direction, oneAhead);

    if (tick.mapUtil.isTileAvailableForMovementTo(twoAhead)) {
      return false;
    }

    ArrayList<SnakeDirection> directionChecks = new ArrayList<>();

    if (direction.equals(SnakeDirection.UP) || direction.equals(SnakeDirection.DOWN)) {
      directionChecks.add(SnakeDirection.LEFT);
      directionChecks.add(SnakeDirection.RIGHT);
    } else if (direction.equals(SnakeDirection.LEFT) || direction.equals(SnakeDirection.RIGHT)) {
      directionChecks.add(SnakeDirection.UP);
      directionChecks.add(SnakeDirection.DOWN);
    }

    MapCoordinate curC = null;

    boolean block = false;
    boolean enemy = false;

    for (SnakeDirection curD : directionChecks) {
      curC = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      for (int i = 0; i < 2; i++) {

        if (i == 0) {
          curC = tick.movement.getNewCoordinate(curD, curC);

          if (!tick.mapUtil.isTileAvailableForMovementTo(curC)) {
            block = true;
            break;
          }
        } else if (i == 1) {
          curC = tick.movement.getNewCoordinate(direction, curC);

          if (tick.movement.isEnemyHeadAt(curC)) {
            enemy = true;
            break;
          }
        }
      }
    }

    return block && enemy;
  }

  public boolean isVulnerableToHeadTrapSide(SnakeDirection direction) {
    MapCoordinate oneAhead = tick.movement
        .getNewCoordinate(direction, tick.mapUtil.getMyPosition());
    MapCoordinate twoAhead = tick.movement.getNewCoordinate(direction, oneAhead);

    if (tick.mapUtil.isTileAvailableForMovementTo(twoAhead)) {
      return false;
    }

    ArrayList<SnakeDirection> directionChecks = new ArrayList<>();

    if (direction.equals(SnakeDirection.UP) || direction.equals(SnakeDirection.DOWN)) {
      directionChecks.add(SnakeDirection.LEFT);
      directionChecks.add(SnakeDirection.RIGHT);
    } else if (direction.equals(SnakeDirection.LEFT) || direction.equals(SnakeDirection.RIGHT)) {
      directionChecks.add(SnakeDirection.UP);
      directionChecks.add(SnakeDirection.DOWN);
    }

    MapCoordinate curC = null;

    boolean block = false;
    boolean enemy = false;

    for (SnakeDirection curD : directionChecks) {
      curC = tick.movement.getNewCoordinate(direction, tick.mapUtil.getMyPosition());

      for (int i = 0; i < 2; i++) {
        curC = tick.movement.getNewCoordinate(curD, curC);

        if (i == 0 && !tick.mapUtil.isTileAvailableForMovementTo(curC)) {
          block = true;
          break;
        } else if (i == 1 && tick.movement.isEnemyHeadAt(curC)) {
          enemy = true;
          break;
        }
      }
    }

    return block && enemy;
  }
}
