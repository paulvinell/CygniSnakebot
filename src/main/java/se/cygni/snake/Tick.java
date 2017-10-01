package se.cygni.snake;

import java.util.ArrayList;
import java.util.List;
import se.cygni.snake.api.model.GameSettings;
import se.cygni.snake.behaviors.CorridorAttackBehavior;
import se.cygni.snake.behaviors.DirectAntiSnakeCollisionBehavior;
import se.cygni.snake.behaviors.AreaBehavior;
import se.cygni.snake.behaviors.AreaWiggleRoomBehavior;
import se.cygni.snake.behaviors.Behavior;
import se.cygni.snake.behaviors.AvoidHeadTrapBehavior;
import se.cygni.snake.behaviors.IndirectAntiSnakeCollisionBehavior;
import se.cygni.snake.behaviors.RoomBehavior;
import se.cygni.snake.behaviors.RoomWiggleRoomBehavior;
import se.cygni.snake.behaviors.SnakeAmountBehavior;
import se.cygni.snake.behaviors.WiggleRoomBehavior;
import se.cygni.snake.api.event.MapUpdateEvent;
import se.cygni.snake.api.model.SnakeDirection;
import se.cygni.snake.client.MapUtil;
import se.cygni.snake.utility.Area;
import se.cygni.snake.utility.Coordinates;
import se.cygni.snake.utility.Movement;
import se.cygni.snake.utility.RelativeDirection;
import se.cygni.snake.utility.Room;

public final class Tick {

  /**
   * Hall of fame:
   * http://game.snake.cygni.se/#/viewgame/5c9bcd00-8a34-4ba4-9f6e-7a3fe7a1606f?_k=hvy96k
   * http://game.snake.cygni.se/#/viewgame/c45ca2cc-004f-4e2d-8482-d9c5097cb1fb?_k=mek6rm
   * http://game.snake.cygni.se/#/viewgame/e04c06a0-5aa6-4ccb-9a4d-dcee07201356?_k=vulr3u
   * http://game.snake.cygni.se/#/viewgame/120b3029-ad98-4637-9a4d-94a8baf155cf?_k=s9xxj9
   *
   * http://game.snake.cygni.se/#/viewgame/caf4f04c-94ff-4d33-bf05-9ec65eebd09e?_k=uf3jp8
   * straight fucking savage killing shit
   *
   * http://game.snake.cygni.se/#/viewgame/9fb8f98c-f471-4a8f-aab2-4da468faf06b?_k=urib0t
   * cuts off the route for two snakes,
   * then wins the game by colliding with enemy when ahead of points
   *
   * technically, you can win the game if only two snakes are alive
   * and you have more points, and execute both head-to-head
   * Disable anti snake collision behaviors and go toward enemy
   * http://game.snake.cygni.se/#/viewgame/4d81775f-4a83-4389-b029-17b4ed164f2a?_k=p6sz9o
   *
   * To do:
   *
   * add behavior that checks if the room the snake is in can be cut off by other snakes
   *
   * http://game.snake.cygni.se/#/viewgame/d63edd72-d65d-4eba-ae2c-c988467532f7?_k=wsjedn
   * Very very simple, add a behavior that penalizes walking to tiles that enemies' only option is to go to.
   * Check where they can go, if it's only one tile. Penalize
   *
   * http://game.snake.cygni.se/#/viewgame/d2b919b4-d95d-4a3a-a38e-919e13aceb26?_k=fdcujp
   * fuck
   *
   * Only difference is that a wall is besides the snake:
   * http://game.snake.cygni.se/#/viewgame/c2846515-9d2a-4102-8d61-a5fc8566e639?_k=dgfcnk
   * avoid this death by adding an artificial tile between snakes and walk toward biggest are
   *
   * http://game.snake.cygni.se/#/viewgame/a7ee7e8e-7d20-409f-8a17-f94f80ae34cf?_k=9wprre
   * check if area calculation is relevant one step forward
   *
   * http://game.snake.cygni.se/#/viewgame/23c5f559-a92f-4e8e-bae2-a41693259ec0?_k=l8ct46
   * ??
   */

  public static Tick tick;

  public final SimpleSnakePlayer ssp;
  public final GameSettings gameSettings;

  public final Area area;
  public final Movement movement;
  public final Coordinates coordinates;
  public final RelativeDirection relativeDirection;
  public final Room room;

  public MapUpdateEvent mapUpdateEvent;
  public MapUtil mapUtil;

  public Tick(final SimpleSnakePlayer ssp, final GameSettings gameSettings) {
    Tick.tick = this;
    Behavior.clearBehaviors();

    this.ssp = ssp;
    this.gameSettings = gameSettings;

    this.area = new Area(this);
    this.movement = new Movement(this);
    this.coordinates = new Coordinates(this);
    this.relativeDirection = new RelativeDirection(this);
    this.room = new Room(this);

    new AreaBehavior(this);
    new AreaWiggleRoomBehavior(this);
    new AvoidHeadTrapBehavior(this);
    new CorridorAttackBehavior(this);
    new DirectAntiSnakeCollisionBehavior(this);
    new IndirectAntiSnakeCollisionBehavior(this);
    new SnakeAmountBehavior(this);
    new RoomBehavior(this);
    new RoomWiggleRoomBehavior(this);
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
      direction = Behavior.getBestMove(directions, nano);
    }

    ssp.registerMove(mapUpdateEvent.getGameTick(), direction);

    //lSystem.out.println((System.nanoTime() - nano) / Math.pow(10, 9));
  }
}
