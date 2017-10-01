package se.cygni.snake.utility.astar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import se.cygni.snake.Tick;
import se.cygni.snake.behaviors.Behavior;
import se.cygni.snake.client.MapCoordinate;

public class Pathfinder {

  private List<Node> nodes = new ArrayList<>();

  private Tick tick;

  private Node start;
  private Node goal;

  public List<Node> path = new ArrayList<>();

  public Pathfinder(Tick tick, MapCoordinate startCoordinate, MapCoordinate goalCoordinate) {
    this.tick = tick;

    start = new Node();
    start.coordinate = startCoordinate;

    for (Integer position : tick.area.getAreaFrom(startCoordinate)) {
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

  public boolean isReachable() {
    return goal != null;
  }

  private void calculate() {
    long gameTickStart = Behavior.currentTick;

    LinkedList<Node> closedSet = new LinkedList<>();
    LinkedList<Node> openSet = new LinkedList<>();

    openSet.add(start);

    while (gameTickStart == Behavior.currentTick) {
      Node currentNode = getLowestCostNode(openSet);

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

      List<Node> neighbors = getNodesNextTo(currentNode, closedSet);
      for (Node neighborNode : neighbors) {
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

  private Node getLowestCostNode(LinkedList<Node> openSet) {
    Node bestNode = null;

    for (Node node : openSet) {
      if (bestNode == null || bestNode.g + bestNode.h > node.g + node.h) {
        bestNode = node;
      }
    }

    return bestNode;
  }

  private List<Node> getNodesNextTo(Node node, LinkedList<Node> closedSet) {
    List<Node> neighbors = new ArrayList<>();

    for (int x = -1; x <= 1; x++) {
      for (int y = -1; y <= 1; y++) {
        if (Math.abs(x) + Math.abs(y) != 1) {
          continue;
        }

        Node addNode = getNode(node.coordinate.translateBy(x, y));

        if (addNode != null && !closedSet.contains(addNode)) {
          neighbors.add(addNode);
        }
      }
    }

    return neighbors;
  }

  private Node getNode(MapCoordinate coordinate) {
    for (Node node : nodes) {
      if (coordinate.x == node.coordinate.x && coordinate.y == node.coordinate.y) {
        return node;
      }
    }

    return null;
  }
}
