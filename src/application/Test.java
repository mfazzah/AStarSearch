package application;

import java.util.ArrayList;
import java.util.List;

import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.geometry.Insets;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Border;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.BorderWidths;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;

public class Test extends Application {

	static Circle cirStart;
	static Circle cirEnd;
	static Polygon polygon1;
	static Polygon polygon2;

	Button btObstacles, btRun, btReset;
	Pane paneAStar = new Pane();

	public static Polygon getPolygon1() {
		return polygon1;
	}

	public static Polygon getPolygon2() {
		return polygon2;
	}

	public void start(Stage primaryStage) {
		try {
			primaryStage.setTitle("A Star Pathfinding");
			BorderPane rootPane = new BorderPane();
			Pane paneButtons = new Pane();

			paneAStar.setBorder(new Border(
					new BorderStroke(Color.BLACK, BorderStrokeStyle.SOLID, CornerRadii.EMPTY, BorderWidths.DEFAULT)));

			paneAStar.setPrefSize(300, 300);

			// start and end points
			cirStart = new Circle(10, 10, 5, Color.GREEN);
			cirEnd = new Circle(245, 275, 5, Color.RED);

			// Polygon obstacles
			polygon1 = new Polygon();
			polygon2 = new Polygon();
			polygon1.getPoints()
					.addAll(new Double[] { 235.0, 139.0, 235.0, 261.0, 165.0, 261.0, 130.0, 200.0, 165.0, 139.0 });

			polygon2.getPoints().addAll(
					new Double[] { 120.0, 40.0, 140.0, 80.0, 120.0, 120.0, 80.0, 120.0, 60.0, 80.0, 80.0, 40.0 });

			paneAStar.getChildren().addAll(cirStart, cirEnd, polygon1, polygon2);

			VBox vb = new VBox();
			btObstacles = new Button("Find Virtual Obstacles");
			btRun = new Button("Run");
			btReset = new Button("Reset");
			vb.getChildren().addAll(btObstacles, btReset, btRun);
			vb.setPadding(new Insets(2, 15, 15, 15));
			vb.setSpacing(10);
			paneButtons.getChildren().add(vb);

			rootPane.setLeft(paneAStar);
			rootPane.setRight(paneButtons);
			Scene scene = new Scene(rootPane, 450, 300);
			primaryStage.setScene(scene);
			primaryStage.setResizable(false);
			primaryStage.show();
		} catch (Exception e) {
			e.printStackTrace();
		}

		// EVENT HANDLING

		// start and end point are not static
		final Delta dragDelta = new Delta();
		cirStart.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				dragDelta.x = cirStart.getLayoutX() - mouseEvent.getSceneX();
				dragDelta.y = cirStart.getLayoutY() - mouseEvent.getSceneY();
				cirStart.setCursor(Cursor.HAND);
			}
		});

		cirStart.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

				// Sets the drag boundaries limit
				double newX = mouseEvent.getSceneX();
				double newY = mouseEvent.getSceneY();

				cirStart.setCenterX(newX);
				cirStart.setCenterY(newY);
			}
		});

		cirEnd.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				dragDelta.x = cirEnd.getLayoutX() - mouseEvent.getSceneX();
				dragDelta.y = cirEnd.getLayoutY() - mouseEvent.getSceneY();
				cirEnd.setCursor(Cursor.HAND);
			}
		});

		cirEnd.setOnMouseDragged(new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent mouseEvent) {

				// Sets the drag boundaries limit
				double newX = mouseEvent.getSceneX();
				double newY = mouseEvent.getSceneY();

				cirEnd.setCenterX(newX);
				cirEnd.setCenterY(newY);
			}
		});

		btObstacles.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				ArrayList<Double> list1 = new ArrayList<Double>();
				ArrayList<Double> list2 = new ArrayList<Double>();
				list1 = Robot.makeVirtualObstacles(polygon1);
				list2 = Robot.makeVirtualObstacles(polygon2);

				polygon1 = new Polygon();
				polygon2 = new Polygon();
				polygon1.getPoints().addAll(list1);
				polygon2.getPoints().addAll(list2);
				polygon1.setFill(Color.RED);
				polygon2.setFill(Color.RED);
				paneAStar.getChildren().addAll(polygon1, polygon2);

			}
		});

		btReset.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				paneAStar.getChildren().clear();
				start(primaryStage);
			}
		});

		btRun.setOnMousePressed(new EventHandler<MouseEvent>() {
			public void handle(MouseEvent mouseEvent) {
				List<Node> list = runAStar();
				Node n; 
				
				//draw path robot takes 
				Polyline polyline = new Polyline();
				for (int i = 0; i < list.size(); i++) {
					System.out.println(list.get(i));
					n = list.get(i);
					polyline.getPoints().addAll(new Double[] { n.getX(), n.getY() });
				}
				Group lines = new Group(polyline); 
				paneAStar.getChildren().addAll(lines); 
			}
		});
	}

	public List<Node> runAStar() {
		Node startNode = new Node(Math.floor(cirStart.getCenterX()), Math.floor(cirStart.getCenterY()));
		Node goalNode = new Node(Math.floor(cirEnd.getCenterX()), Math.floor(cirEnd.getCenterY()));
		int width = (int) (paneAStar.getWidth());
		int height = (int) (paneAStar.getHeight());

		AStar astar = new AStar(width, height, startNode, goalNode);

		astar.getNodesToCheck().add(startNode);
		astar.getNodesToCheck().add(goalNode);

		astar.setObstacles(polygon1);
		astar.setObstacles(polygon2);

		List<Node> nds = astar.getNodesToCheck();
		List<Node> apath = astar.buildPath();

		return apath;

	}

	public static void main(String[] args) {
		launch(args);

	}

	class Delta {
		double x, y;
	}
}