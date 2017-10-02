package se.cygni.snake.utility.astar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import se.cygni.snake.Tick;
import se.cygni.snake.behaviors.Behavior;
import se.cygni.snake.client.MapCoordinate;
import se.cygni.snake.utility.Area;

public final class Pathfinder {

  private final List<Node> nodes = new ArrayList<>();

  private final Tick tick;

  private final Node start;
  private Node goal;

  public final List<Node> path = new ArrayList<>();

  public Pathfinder(Tick tick, MapCoordinate startCoordinate, MapCoordinate goalCoordinate, List<MapCoordinate> allowedTiles) {
    this.tick = tick;

    start = new Node();
    start.coordinate = startCoordinate;

    Area area = new Area(tick);
    if (allowedTiles != null) {
      area.allowedTiles.addAll(allowedTiles);
    }

    for (final Integer position : area.getAreaFrom(startCoordinate)) {
      MapCoordinate coordinate = tick.mapUtil.translatePosition(position);

      Node newNode = new Node();
      newNode.coordinate = coordinate;

      if (goal == null && goalCoordinate.x == coordinate.x && goalCoordinate.y == coordinate.y) {
        goal = newNode;
      }

      nodes.add(newNode);
    }

    if (isReachable()) {
      calculate();
    }
  }

  public Pathfinder(Tick tick, MapCoordinate startCoordinate, MapCoordinate goalCoordinate) {
    this(tick, startCoordinate, goalCoordinate, null);
  }

  public final boolean isReachable() {
    return goal != null;
  }

  private final void calculate() {
    final long gameTickStart = Behavior.currentTick;

    final LinkedList<Node> closedSet = new LinkedList<>();
    final LinkedList<Node> openSet = new LinkedList<>();

    openSet.add(start);

    while (gameTickStart == Behavior.currentTick) {
      final Node currentNode = getLowestCostNode(openSet);

      closedSet.add(currentNode);
      openSet.remove(currentNode);

      if (currentNode.coordinate.x == goal.coordinate.x
          && currentNode.coordinate.y == goal.coordinate.y) {
        path.add(goal);
        while(true) {
          Node node = path.get(0).parent;

          if (node.equals(start)) {
            break;
          }

          path.add(0, node);
        }

        return;
      }

      final List<Node> neighbors = getNodesNextTo(currentNode, closedSet);
      for (final Node neighborNode : neighbors) {
        if (!openSet.contains(neighborNode)) {
          neighborNode.parent = currentNode;
          neighborNode.g = currentNode.g + 1;
          neighborNode.h = goal.coordinate.getManhattanDistanceTo(neighborNode.coordinate);

          openSet.add(neighborNode);
        } else if (neighborNode.g > currentNode.g + 1) {
          neighborNode.parent = currentNode;
          neighborNode.g = currentNode.g + 1;
        }
      }

      if (openSet.isEmpty()) {
        goal = null;
        return;
      }
    }
  }

  private final Node getLowestCostNode(final LinkedList<Node> openSet) {
    Node bestNode = null;

    for (final Node node : openSet) {
      if (bestNode == null || bestNode.g + bestNode.h > node.g + node.h) {
        bestNode = node;
      }
    }

    return bestNode;
  }

  private final List<Node> getNodesNextTo(final Node node, final LinkedList<Node> closedSet) {
    final List<Node> neighbors = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        if (Math.abs(x) + Math.abs(y) != 1) {
          continue;
        }

        final Node addNode = getNode(node.coordinate.translateBy(x, y));

        if (addNode != null && !closedSet.contains(addNode)) {
          neighbors.add(addNode);
        }
      }
    }

    return neighbors;
  }

  private final Node getNode(final MapCoordinate coordinate) {
    for (final Node node : nodes) {
      if (coordinate.x == node.coordinate.x && coordinate.y == node.coordinate.y) {
        return node;
      }
    }

    return null;
  }
}
