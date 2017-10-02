package se.cygni.snake.utility.astar;

import java.util.ArrayList;
import java.util.List;
import se.cygni.snake.client.MapCoordinate;

public final class Node {

  public MapCoordinate coordinate;

  public List<Node> neighbors = new ArrayList<>();
  public Node parent;

  public int g; // Cost from start to this node
  public int h; // Cost from node to finish (manhattan distance)
}
