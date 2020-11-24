package application;

import java.awt.geom.Line2D;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Set;

import javafx.scene.shape.Polygon;

public class AStar {
	private static final double DEFAULT_HORIZONTAL_VERTICAL_COST = 10;
	private static final double DEFAULT_DIAGONAL_COST = 14.1421356237; // sqrt(200);
	private double horizontalVerticalCost;
	private double diagonalCost;
	private PriorityQueue<Node> openSet;
	private HashSet<Node> closedSet;
	private Node startNode = new Node();
	private Node goalNode = new Node();
	private Node[][] area;
	private List<Node> nodesToCheck; // marks off vertices of shapes, and start and end points

	public AStar(int width, int height, Node startNode, Node goalNode, double horizontalVerticalCost,
			double diagonalCost) {
		setStartNode(startNode);
		setGoalNode(goalNode);
		this.horizontalVerticalCost = horizontalVerticalCost;
		this.diagonalCost = diagonalCost;
		this.area = new Node[width][height];
		this.openSet = new PriorityQueue<Node>(new Comparator<Node>() {
			public int compare(Node n0, Node n1) {
				return Integer.compare((int) n0.getF(), (int) n1.getF());
			}
		});

		initNodes();
		this.closedSet = new HashSet<>();
		this.nodesToCheck = new ArrayList<Node>();
	}

	// constructor for default costs
	public AStar(int width, int height, Node startNode, Node goalNode) {
		this(width, height, startNode, goalNode, DEFAULT_HORIZONTAL_VERTICAL_COST, DEFAULT_DIAGONAL_COST);

	}

	public void initNodes() {
		for (int i = 0; i < area.length; i++) {
			for (int j = 0; j < area[0].length; j++) {
				Node node = new Node(i, j);
				node.setIsObstacle(false);
				node.calculateHeuristic(getGoalNode());
				this.area[i][j] = node;
			}
		}
	}

	public void setObstacles(Polygon p) {
		List<Double> list = p.getPoints();
		List<Double> list2;
		for (int i = 0; i < list.size(); i++) {
			double temp = list.get(i);
			list.set(i, Math.floor(temp));
		}

		Polygon p2 = new Polygon();
		p2.getPoints().addAll(list);
		list2 = p2.getPoints();

		// keep track of vertices
		for (int i = 0; i < list2.size() - 1; i += 2) {
			getNodesToCheck().add(new Node(list2.get(i), list2.get(i + 1)));
		}

		for (int i = 0; i < area.length; i++) {
			for (int j = 0; j < area[0].length; j++) {
				if (p2.contains(i, j)) {
					this.setObstacle(i, j);
				}
			}
		}
	}

	public List<Node> buildPath() {
		openSet.add(startNode);
		while (!openSet.isEmpty()) {
			Node currentNode = openSet.poll(); // priority queue returns minimum element first;
			closedSet.add(currentNode);

			if (isGoalNode(currentNode)) {
				return getPath(currentNode);
			} else {
				// check for next node
				addNodes(currentNode);
			}
		}
		return new ArrayList<Node>();
	}

	// builds path backward from the end, adding parents nodes to the head of the
	// list until no parent node is found
	public List<Node> getPath(Node currentNode) {
		List<Node> path = new ArrayList<Node>();
		path.add(currentNode);
		Node parent;

		while ((parent = currentNode.getParent()) != null) {
			path.add(0, parent);
			currentNode = parent;
		}

		return path;
	}

	// checks if current node has cheaper path or not
	public void checkNode(Node currentNode, double xPos, double yPos, double cost) {
		Node adjacentNode = getArea()[(int) xPos][(int) yPos];
		if (!adjacentNode.isObstacle() && !getClosedSet().contains(adjacentNode)) {
			if (!getOpenSet().contains(adjacentNode)) {
				adjacentNode.setNodeCost(currentNode, cost);
				getOpenSet().add(adjacentNode);
			} else {
				boolean isBetterPath = adjacentNode.checkForBetterPath(currentNode, cost);
				if (isBetterPath) {
					// the changed node is removed and added back to the priority queue with its
					// modified cost
					getOpenSet().remove(adjacentNode);
					getOpenSet().add(adjacentNode);
				}
			}
		}
	}

	public void addNodes(Node currentNode) {
		findUpperRows(currentNode);
		findMiddleRows(currentNode);
		findLowerRows(currentNode);
	}

	// finds adjacent rows above current node
	public void findUpperRows(Node currentNode) {
		int row = (int) currentNode.getY();
		int col = (int) currentNode.getX();
		int upperRow = row - 1;
		if (upperRow >= 0) {
			if (col - 1 >= 0) {
				checkNode(currentNode, col - 1, upperRow, getDiagonalCost());
			}
			if (col + 1 < getArea()[0].length) {
				checkNode(currentNode, col + 1, upperRow, getDiagonalCost());
			}
			checkNode(currentNode, col, upperRow, getHorizontalVerticalCost());
		}
	}

	// finds adjacent nodes to either side of current node
	public void findMiddleRows(Node currentNode) {
		int row = (int) currentNode.getY();
		int col = (int) currentNode.getX();
		int middleRow = row;
		if (col - 1 >= 0) {
			checkNode(currentNode, col - 1, middleRow, getHorizontalVerticalCost());
		}
		if (col + 1 < getArea()[0].length) {
			checkNode(currentNode, col + 1, middleRow, getHorizontalVerticalCost());
		}
	}

	public void findLowerRows(Node currentNode) {
		int row = (int) currentNode.getY();
		int col = (int) currentNode.getX();
		// get lower adjacent rows
		int lowerRow = row + 1;
		if (lowerRow < getArea().length) {
			if (col - 1 >= 0) {
				checkNode(currentNode, col - 1, lowerRow, getDiagonalCost());
			}
			if (col + 1 < getArea()[0].length) {
				checkNode(currentNode, col + 1, lowerRow, getDiagonalCost());
			}
			checkNode(currentNode, col, lowerRow, getHorizontalVerticalCost());
		}
	}

	public boolean isGoalNode(Node currentNode) {
		return currentNode.equals(goalNode);
	}

	private void setObstacle(int x, int y) {
		this.area[x][y].setIsObstacle(true);
	}

	// getters and setters
	public double getHorizontalVerticalCost() {
		return horizontalVerticalCost;
	}

	public void setHorizontalVerticalCost(double horizontalVerticalCost) {
		this.horizontalVerticalCost = horizontalVerticalCost;
	}

	public double getDiagonalCost() {
		return diagonalCost;
	}

	public void setDiagonalCost(double diagonalCost) {
		this.diagonalCost = diagonalCost;
	}

	public PriorityQueue<Node> getOpenSet() {
		return openSet;
	}

	public void setOpenSet(PriorityQueue<Node> openSet) {
		this.openSet = openSet;
	}

	public Set<Node> getClosedSet() {
		return closedSet;
	}

	public void setClosedSet(HashSet<Node> closedSet) {
		this.closedSet = closedSet;
	}

	public Node getStartNode() {
		return startNode;
	}

	public void setStartNode(Node startNode) {
		this.startNode = startNode;
	}

	public Node getGoalNode() {
		return goalNode;
	}

	public void setGoalNode(Node goalNode) {
		this.goalNode = goalNode;
	}

	public void addtoNodesCheck(Node node) {
		this.nodesToCheck.add(node);
	}

	public List<Node> getNodesToCheck() {
		return this.nodesToCheck;
	}

	public Node[][] getArea() {
		return area;
	}

	public void setArea(Node[][] area) {
		this.area = area;
	}

}
