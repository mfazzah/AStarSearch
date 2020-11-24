package application;

import java.util.ArrayList;

import javafx.collections.ObservableList;
import javafx.scene.shape.Polygon;

public class Robot {
	// robot is assumed to be shaped as an equilateral triangle with sidelength
	// defaulting to ten
	private double sideLength = 10;
	private double height = Math.sqrt(3) * 0.5 * sideLength;

	// Nodes a, b, and c represent the three Nodes of the triangle that forms the
	// robot
	// a is the relevant Node, as robot cannot rotate and Node a will be from where the robot decides to move
	private Node a, b, c;

	public Robot(Node locationOfA) {
		this.a = locationOfA;
		this.b = new Node(a.getX() + sideLength, a.getY());
		this.c = new Node(a.getX() + (0.5 * sideLength), a.getY() - (Math.sqrt(3) * 0.5 * sideLength));
	}

	public void setA(Node a) {
		this.a = a;
		this.b = new Node(a.getX() + sideLength, a.getY());
		this.c = new Node(a.getX() + (0.5 * sideLength), a.getY() - (Math.sqrt(3) * 0.5 * sideLength));

	}

	public double getSideLength() {
		return sideLength;
	}

	public void setSideLength(double sideLength) {
		this.sideLength = sideLength;
	}

	public double getHeight() {
		return height;
	}

	public void setHeight(double height) {
		this.height = height;
	}

	public Node getA() {
		return this.a;
	}

	// Since the robot cannot rotate, this method returns Nodes of a "virtual
	// obstacle"
	public static ArrayList<Double> makeVirtualObstacles(Polygon p) {
		ObservableList<Double> list = p.getPoints();
		ArrayList<Node> virt = new ArrayList<Node>();
		Node startingNode = new Node(list.get(0), list.get(1));
		Node node;

		// first Node in virtual obstacle is first Node of original polygon
		virt.add(startingNode);
		Robot robo = new Robot(startingNode);
		// each two Nodes 0,1 2,3 4,5 etc are a set coordinate x,y Nodes
		for (int i = 0; i < list.size() - 2; i += 2) {
			Node current = new Node(list.get(i), list.get(i + 1));
			Node next = new Node(list.get(i + 2), list.get(i + 3));

			if (next.getX() < current.getX() && current.getY() == next.getY()) {
				node = robo.getA();
				// compensate for lack of rotation
				robo.setA(new Node(node.getX() - 0.5 * robo.getSideLength(), node.getY() + robo.getHeight()));
				virt.add(robo.getA());

				// move required horizontal distance
				node = robo.getA();
				robo.setA(new Node(node.getX() - xDistance(current, next), node.getY()));
				virt.add(robo.getA());

			} else if (next.getX() > current.getX() && current.getY() == next.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX() + xDistance(current, next), node.getY()));

			} else if (next.getX() == current.getX() && next.getY() > current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX(), node.getY() + yDistance(current, next)));

			} else if (next.getX() == current.getX() && next.getY() < current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX(), node.getY() - yDistance(current, next)));

			} else if (next.getX() > current.getX() && next.getY() > current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX() + xDistance(current, next), node.getY() + yDistance(current, next)));

			} else if (next.getX() > current.getX() && next.getY() < current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX() + xDistance(current, next), node.getY() - yDistance(current, next)));

			} else if (next.getX() < current.getX() && next.getY() < current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX() - xDistance(current, next), node.getY() - yDistance(current, next)));

			} else if (next.getX() < current.getX() && next.getY() > current.getY()) {
				node = robo.getA();
				robo.setA(new Node(node.getX() - xDistance(current, next), node.getY() + yDistance(current, next)));
			}
			virt.add(robo.getA());
		}
		// move robot to final position
		virt.add(new Node(list.get(list.size() - 2), list.get(list.size() - 1)));

		
		ArrayList<Double> verticesOfVirtualObstacle = new ArrayList<Double>();
		
		for (int i = 0; i < virt.size(); i++) {
			verticesOfVirtualObstacle.add(virt.get(i).getX());
			verticesOfVirtualObstacle.add(virt.get(i).getY());
		}

		return verticesOfVirtualObstacle;

	}

	public static double distance(Node p1, Node p2) {
		return Math.hypot(p1.getX() - p2.getX(), p1.getY() - p2.getY());
	}

	public static double xDistance(Node p1, Node p2) {
		return Math.abs(p1.getX() - p2.getX());
	}

	public static double yDistance(Node p1, Node p2) {
		return Math.abs(p1.getY() - p2.getY());
	}

}
