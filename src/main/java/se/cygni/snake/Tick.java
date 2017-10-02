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
import se.cygni.snake.utility.SnakeHandler;

public final class Tick {

  /**
   * To do:
   *
   * indirect snake behavior broken
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
  public final SnakeHandler snakeHandler;

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
    this.snakeHandler = new SnakeHandler(this);

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

    //System.out.println((System.nanoTime() - nano) / Math.pow(10, 9));
  }
}
