package se.cygni.snake;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.behaviors.AvoidCorridorBehavior;
import se.cygni.snake.behaviors.DirectAntiSnakeCollisionBehavior;
import se.cygni.snake.behaviors.AreaBehavior;
import se.cygni.snake.behaviors.AreaWiggleRoomBehavior;
import se.cygni.snake.behaviors.Behavior;
import se.cygni.snake.behaviors.AvoidHeadTrapBehavior;
import se.cygni.snake.behaviors.IndirectAntiSnakeCollisionBehavior;
import se.cygni.snake.behaviors.SnakeAmountBehavior;
import se.cygni.snake.behaviors.WiggleRoomBehavior;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.utility.Area;
import se.cygni.snake.utility.Coordinates;
import se.cygni.snake.utility.Movement;
import se.cygni.snake.utility.RelativeDirection;

public class Tick {

  /**
   * To do:
   *
   * Pathfinding
   * Find the point which is furthest away from all enemies
   * Constantly move toward that point if possible
   *
   * Add Indirect collision avoidance
   * Definitely add corridor avoidance
   * Penalize closed corridors even more
   *
   * Remember that you can step on enemy snake tails
   *
   * http://game.snake.cygni.se/#/viewgame/dbb3f8b0-3528-439e-b5fb-334618b16f08?_k=rm2vk5
   * fix defense against attack involving wiggle room exploit
   *
   * http://game.snake.cygni.se/#/viewgame/997f4a98-b331-4bcb-91f6-7308e4c91d08?_k=lca240
   * simple check away
   *
   * also simple-ish
   * http://game.snake.cygni.se/#/viewgame/9246bf44-483b-4d14-9b19-cf2b5adc8314?_k=mmy13i
   *
   * another one
   * http://game.snake.cygni.se/#/viewgame/19ec56a7-707e-4ee6-b517-7e11eee65335?_k=h6lnqk
   *
   * Only difference is that a wall is besides the snake:
   * http://game.snake.cygni.se/#/viewgame/c2846515-9d2a-4102-8d61-a5fc8566e639?_k=dgfcnk
   * avoid this death by adding an artificial tile between snakes and walk toward biggest area
   * http://game.snake.cygni.se/#/viewgame/d1114dca-97fd-46a8-8163-dcd5ae38b093?_k=t1y2qe
   * easily avoidable confusion, just turn earlier
   *
   * http://game.snake.cygni.se/#/viewgame/fd3ee8e4-71e6-4e55-8021-0d9c3ff52f07?_k=fndou8
   */

  private SimpleSnakePlayer ssp;

  public Area area;
  public Movement movement;
  public Coordinates coordinates;
  public RelativeDirection relativeDirection;

  public MapUpdateEvent mapUpdateEvent;
  public MapUtil mapUtil;
  public HashMap<SnakeDirection, Integer> directions = new HashMap<>(); //direction and associated area

  public Tick(SimpleSnakePlayer ssp) {
    this.ssp = ssp;

    this.area = new Area(this);
    this.movement = new Movement(this);
    this.coordinates = new Coordinates(this);
    this.relativeDirection = new RelativeDirection(this);

    new AreaBehavior(this);
    new AreaWiggleRoomBehavior(this);
    new AvoidHeadTrapBehavior(this);
    new DirectAntiSnakeCollisionBehavior(this);
    new SnakeAmountBehavior(this);
    new WiggleRoomBehavior(this);

//    new AvoidCorridorBehavior(this);
//    new IndirectAntiSnakeCollisionBehavior(this);
  }

  public void onMapUpdate(MapUpdateEvent mapUpdateEvent) {
    this.mapUpdateEvent = mapUpdateEvent;
    this.mapUtil = new MapUtil(mapUpdateEvent.getMap(), ssp.getPlayerId());

    List<SnakeDirection> directions = new ArrayList<>();

    for (SnakeDirection direction : SnakeDirection.values()) {
      if (mapUtil.canIMoveInDirection(direction)) {
        directions.add(direction);
      }
    }

    SnakeDirection direction = null;

    if (directions.size() == 0) {
      direction = SnakeDirection.DOWN;
    } else if (directions.size() == 1) {
      direction = directions.get(0);
    } else if (directions.size() >= 2) {
      direction = Behavior.getBestMove(directions);
    }

    ssp.registerMove(mapUpdateEvent.getGameTick(), direction);
  }
}
