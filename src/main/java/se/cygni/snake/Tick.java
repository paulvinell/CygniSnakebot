package se.cygni.snake;

import java.util.ArrayList;
import java.util.List;
import se.cygni.snake.api.model.GameSettings;
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
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.utility.Area;
import se.cygni.snake.utility.Coordinates;
import se.cygni.snake.utility.Movement;
import se.cygni.snake.utility.RelativeDirection;

public class Tick {

  /**
   * To do:
   *
   * http://game.snake.cygni.se/#/viewgame/d63edd72-d65d-4eba-ae2c-c988467532f7?_k=wsjedn
   * Very very simple, add a behavior that penalizes walking to tiles that enemies' only option is to go to.
   * Check where they can go, if it's only one tile. Penalize
   *
   * Pathfinding
   * Find the point which is furthest away from all enemies
   * Constantly move toward that point if possible
   *
   * Add Indirect collision avoidance
   * Definitely add corridor avoidance
   * Penalize closed corridors even more
   *
   * http://game.snake.cygni.se/#/viewgame/d2b919b4-d95d-4a3a-a38e-919e13aceb26?_k=fdcujp
   *
   * dont just calculate areas, calculate rooms too
   *
   * http://game.snake.cygni.se/#/viewgame/4a7d5514-5130-4f81-b8d8-e5720193945f?_k=6ubepl
   *
   * http://game.snake.cygni.se/#/viewgame/d2ef0803-b334-42d4-87b7-f8f161ee81fe?_k=0ugxcn
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
   *
   * http://game.snake.cygni.se/#/viewgame/fd3ee8e4-71e6-4e55-8021-0d9c3ff52f07?_k=fndou8
   *
   * to make calculations better. Check if the obstacle is a snake. Check the manhattan distance.
   * Remove the amount of tiles equals to the manhattan distance to the tile from the enemy snakes length.
   * If there is no obstacle by the time we get there, there is no obstacle
   *
   * http://game.snake.cygni.se/#/viewgame/a7ee7e8e-7d20-409f-8a17-f94f80ae34cf?_k=9wprre
   * check if area calculation is relevant one step forward
   */

  public final SimpleSnakePlayer ssp;
  public final GameSettings gameSettings;

  public final Area area;
  public final Movement movement;
  public final Coordinates coordinates;
  public final RelativeDirection relativeDirection;

  public MapUpdateEvent mapUpdateEvent;
  public MapUtil mapUtil;

  public Tick(SimpleSnakePlayer ssp, GameSettings gameSettings) {
    this.ssp = ssp;
    this.gameSettings = gameSettings;

    this.area = new Area(this);
    this.movement = new Movement(this);
    this.coordinates = new Coordinates(this);
    this.relativeDirection = new RelativeDirection(this);

    new AreaBehavior(this);
    new AreaWiggleRoomBehavior(this);
    new AvoidHeadTrapBehavior(this);
    new DirectAntiSnakeCollisionBehavior(this);
    new SnakeAmountBehavior(this);
    new IndirectAntiSnakeCollisionBehavior(this);
    new WiggleRoomBehavior(this);
  }

  public final void onMapUpdate(final MapUpdateEvent mapUpdateEvent) {
    long nano = System.nanoTime();

    this.mapUpdateEvent = mapUpdateEvent;
    this.mapUtil = new MapUtil(mapUpdateEvent.getMap(), ssp.getPlayerId());

    final List<SnakeDirection> directions = new ArrayList<>();

    for (final SnakeDirection direction : SnakeDirection.values()) {
      if (movement.canIMoveInDirection(direction)) {
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

    //System.out.println((System.nanoTime() - nano) / Math.pow(10, 9));
  }
}
