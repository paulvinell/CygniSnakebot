package se.cygni.snake.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public class Corridor {

  private Tick tick;
  private SnakeInfo snake;

  private boolean corridor;
  private MapCoordinate lastCorridorTile;
  private MapCoordinate openCorridorTile;
  private int corridorLength;

  public Corridor(Tick tick, SnakeInfo snake) {
    this.tick = tick;
    this.snake = snake;

    this.corridor = false;
    this.lastCorridorTile = null;
    this.openCorridorTile = null;
    this.corridorLength = 0;

    calculate();
  }

  private void calculate() {
    if (snake.getLength() <= 1) {
      return;
    }

    int headPosInt = snake.getPositions()[0];
    MapCoordinate headPos = tick.mapUtil.translatePosition(headPosInt);

    int neckPosInt = snake.getPositions()[1];
    MapCoordinate neckPos = tick.mapUtil.translatePosition(neckPosInt);

    List<MapCoordinate> moves = getTrueAvailableTilesFrom(headPos);

    if (moves.size() == 1) {
      int corridorLength = 0;
      MapCoordinate lastCorridorTile = null;
      MapCoordinate currentCorridorTile = moves.get(0);

      while (true) {
        moves = getTrueAvailableTilesFrom(currentCorridorTile);

        if (corridorLength == 0 && moves.size() == 1) {
          lastCorridorTile = currentCorridorTile;
          currentCorridorTile = moves.get(0);

          corridorLength++;
        } else if (corridorLength > 0
            && moves.size() == 2
            && getDiagonalAvailableTilesFrom(currentCorridorTile).size() == 0) {
          moves.remove(lastCorridorTile);

          lastCorridorTile = currentCorridorTile;
          currentCorridorTile = moves.get(0);

          corridorLength++;
        } else if (moves.size() == 0) {
          corridorLength = 0;
          break;
        } else {
          break;
        }
      }

      if (corridorLength > 0) {
        this.corridor = true;
        this.lastCorridorTile = lastCorridorTile;
        this.openCorridorTile = currentCorridorTile;
        this.corridorLength = corridorLength;
      }
    }
  }

  private List<MapCoordinate> getTrueAvailableTilesFrom(MapCoordinate coordinate) {
    ArrayList<MapCoordinate> coordinates = new ArrayList<>();

    coordinates.add(coordinate.translateBy(1, 0));
    coordinates.add(coordinate.translateBy(-1, 0));
    coordinates.add(coordinate.translateBy(0, 1));
    coordinates.add(coordinate.translateBy(0, -1));

    return coordinates.stream()
        .filter(c -> tick.mapUtil.isTileAvailableForMovementTo(c))
        .collect(Collectors.toList());
  }

  private List<MapCoordinate> getDiagonalAvailableTilesFrom(MapCoordinate coordinate) {
    List<MapCoordinate> coordinates = getTrueAvailableTilesFrom(coordinate);
    List<MapCoordinate> newCoordinates = new ArrayList<>();

    for (MapCoordinate x : coordinates) {
      for (MapCoordinate y : coordinates) {
        if (x.x == y.x && x.y == y.y) {
          continue;
        }

        int dX = x.x + y.x - 2 * (coordinate.x);
        int dY = x.y + y.y - 2 * (coordinate.y);

        if (dX == 0 && dY == 0) {
          continue;
        }

        MapCoordinate newCoordinate = coordinate.translateBy(dX, dY);
        if (tick.mapUtil.isTileAvailableForMovementTo(newCoordinate)) {
          newCoordinates.add(newCoordinate);
        }
      }
    }

    return newCoordinates;
  }

  public boolean isInCorridor() {
    return corridor;
  }

  public MapCoordinate getLastCorridorTile() {
    return this.lastCorridorTile;
  }

  public MapCoordinate getOpenCorridorTile() {
    return this.openCorridorTile;
  }

  public int getCorridorLength() {
    return this.corridorLength;
  }
}
