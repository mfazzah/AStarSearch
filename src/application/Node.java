package application;

public class Node {
	private double x; // col
	private double y; // row
	private double g; // cost from start node to current node
	private double f; // sum of g + h
	private double h; // heuristic cost, estimates cheapest path from current node to goal
	private Node parent;
	private boolean isObstacle = false; //reflects virtual obstacle 
	public Node() {
	}

	public Node(double x, double y) {
		this.x = x;
		this.y = y;
	}

	//uses Manhattan distance 
	public void calculateHeuristic(Node goalNode) {
		this.h = Math.abs(goalNode.getX() - getX()) + Math.abs(goalNode.getY() - getY());
	}
	

	public void setNodeCost(Node currentNode, double cost) {
		double costFromStart = currentNode.getG() + cost;
		setParent(currentNode);
		setG(costFromStart);
		calcFinalCost();

	}

	public void finalCost() {
		setF(getG() + getH());
	}

	public boolean checkForBetterPath(Node currentNode, double cost) {
		double costFromStart = currentNode.getG() + cost;

		if (costFromStart < getG()) {
			setNodeCost(currentNode, costFromStart);
			return true;
		}
		return false;
	}
	
	public void calcFinalCost() {
		double finalCost = getG() + getH(); 
		setF(finalCost);
	}
	
	@Override
	public String toString() {
		String s = "Node ( " + x + " , " + y + ")"; 
		return s;

	}
	
	@Override
	public boolean equals(Object ob) {
		Node otherNode = (Node) ob; 
		return this.getX() == otherNode.getX() && this.getY() == otherNode.getY();
	}

	// getters and setters
	public double getX() {
		return x;
	}

	public void setX(double x) {
		this.x = x;
	}

	public double getY() {
		return y;
	}

	public void setY(double y) {
		this.y = y;
	}

	public double getG() {
		return g;
	}

	public void setG(double g) {
		this.g = g;
	}

	public double getF() {
		return f;
	}

	public void setF(double f) {
		this.f = f;
	}

	public double getH() {
		return h;
	}

	public void setH(double h) {
		this.h = h;
	}

	public Node getParent() {
		return parent;
	}

	public void setParent(Node parent) {
		this.parent = parent;
	}
	
	public boolean isObstacle() {
		return isObstacle;
	}
	
	public void setIsObstacle(boolean isObstacle) {
		this.isObstacle = isObstacle;
	}
}
