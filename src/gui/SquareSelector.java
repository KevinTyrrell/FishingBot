package gui;

import java.awt.Point;

import program.Logic;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class SquareSelector extends Stage
{
	private Point topLeft, bottomRight;	
	
	public Point getTopLeft()
	{
		return topLeft;
	}
	
	public Point getBottomRight()
	{
		return bottomRight;
	}
	
	public SquareSelector(Window base)
	{
		// Setup how this will be displayed.
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(base);

		Button doneBtn = new Button("Done");
		doneBtn.setOnAction(e -> close());
		
		Rectangle rect = new Rectangle();
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.BLACK);
		rect.setStrokeWidth(10);
		Pane root = new Pane(rect, doneBtn);
		root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");
		

		// When the user first clicks, prepare to create our rectangle.
		root.setOnMousePressed(e ->
		{
			rect.setWidth(0);
			rect.setHeight(0);
			rect.setX(e.getX());
			rect.setY(e.getY());
		});
		
		root.setOnMouseDragged(e ->
		{
			double width = e.getX() - rect.getX();
			double height = e.getY() - rect.getY();
			
			rect.setWidth(width);
			rect.setHeight(height);
		});
		
		Scene scene = new Scene(root, Logic.display.getWidth(), Logic.display.getHeight());
		scene.setFill(Color.TRANSPARENT);
		setScene(scene);

		showAndWait();
	}
}
