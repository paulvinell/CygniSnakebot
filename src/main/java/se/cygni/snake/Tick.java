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

  /*
    http://game.snake.cygni.se/#/viewgame/f2c4fe6b-6daf-4ef7-9e83-48f5af048f06?_k=3dlbi1
    area behavior thinks that
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
