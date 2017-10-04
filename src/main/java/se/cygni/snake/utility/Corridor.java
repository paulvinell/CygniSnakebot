package se.cygni.snake.utility;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import se.cygni.snake.Tick;
import se.cygni.snake.api.model.SnakeInfo;
import se.cygni.snake.client.MapCoordinate;

public final class Corridor {

  private final Tick tick;
  private final SnakeInfo snake;

  private boolean corridor;
  private MapCoordinate lastCorridorTile;
  private MapCoordinate openCorridorTile;
  private int corridorLength;

  public Corridor(final Tick tick, final SnakeInfo snake) {
    this.tick = tick;
    this.snake = snake;

    this.corridor = false;
    this.lastCorridorTile = null;
    this.openCorridorTile = null;
    this.corridorLength = 0;

    calculate();
  }

  private final void calculate() {
    if (snake.getLength() <= 1) {
      return;
    }

    final int headPosInt = snake.getPositions()[0];
    final MapCoordinate headPos = tick.mapUtil.translatePosition(headPosInt);

    final int neckPosInt = snake.getPositions()[1];
    final MapCoordinate neckPos = tick.mapUtil.translatePosition(neckPosInt);

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
        } else if (corridorLength > 0 && moves.size() == 1) {
          corridorLength = 0;
          break;
        } else {
          break;
        }
      }

      this.corridor = corridorLength > 0;
      this.lastCorridorTile = lastCorridorTile;
      this.openCorridorTile = currentCorridorTile;
      this.corridorLength = corridorLength;
    }
  }

  private final List<MapCoordinate> getTrueAvailableTilesFrom(final MapCoordinate coordinate) {
    final ArrayList<MapCoordinate> coordinates = new ArrayList<>();

    coordinates.add(coordinate.translateBy(1, 0));
    coordinates.add(coordinate.translateBy(-1, 0));
    coordinates.add(coordinate.translateBy(0, 1));
    coordinates.add(coordinate.translateBy(0, -1));

    return coordinates.stream()
        .filter(c -> tick.mapUtil.isTileAvailableForMovementTo(c))
        .collect(Collectors.toList());
  }

  private final List<MapCoordinate> getDiagonalAvailableTilesFrom(final MapCoordinate coordinate) {
    final List<MapCoordinate> coordinates = getTrueAvailableTilesFrom(coordinate);
    final List<MapCoordinate> newCoordinates = new ArrayList<>();

    for (final MapCoordinate x : coordinates) {
      for (final MapCoordinate y : coordinates) {
        if (x.x == y.x && x.y == y.y) {
          continue;
        }

        final int dX = x.x + y.x - 2 * (coordinate.x);
        final int dY = x.y + y.y - 2 * (coordinate.y);

        if (dX == 0 && dY == 0) {
          continue;
        }

        final MapCoordinate newCoordinate = coordinate.translateBy(dX, dY);
        if (tick.mapUtil.isTileAvailableForMovementTo(newCoordinate)) {
          newCoordinates.add(newCoordinate);
        }
      }
    }

    return newCoordinates;
  }

  public final boolean isInCorridor() {
    return corridor;
  }

  public final MapCoordinate getLastCorridorTile() {
    return this.lastCorridorTile;
  }

  public final MapCoordinate getOpenCorridorTile() {
    return this.openCorridorTile;
  }

  public final int getCorridorLength() {
    return this.corridorLength;
  }
}
