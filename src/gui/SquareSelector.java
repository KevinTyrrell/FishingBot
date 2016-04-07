package gui;

import java.awt.Point;

import program.Logic;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.input.KeyCode;
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
	/** The coordinates which correspond to the location of the user drawn square. */
	private Point topLeft, bottomRight;	
	
	/**
	 * Gets the top left coordinate of the square.
	 * @return top left point.
	 */
	public Point getTopLeft()
	{
		return topLeft;
	}
	
	/**
	 * Gets the bottom reft coordinate of the square.
	 * @return bottom right point.
	 */
	public Point getBottomRight()
	{
		return bottomRight;
	}
	
	/**
	 * Creates and runs a frame where the user can draw one square to select a 
	 * certain zone in which the calling class can use to perform  functions on.
	 * @param owner window.
	 */
	public SquareSelector(Window owner)
	{
		// Setup how this will be displayed.
		initStyle(StageStyle.TRANSPARENT);
		initModality(Modality.APPLICATION_MODAL);
		initOwner(owner);

		topLeft = new Point(0, 0);
		bottomRight = new Point(0, 0);

		Rectangle rect = new Rectangle();
		// The origin which gets reset on every NEW click.
        Point origin = new Point(0,0);
		rect.setFill(Color.TRANSPARENT);
		rect.setStroke(Color.BLACK);
		// Dynamic stroke width based on monitor size.
		rect.setStrokeWidth(Logic.display.getWidth() / 320);
		Pane root = new Pane(rect);
		root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");

		// When the user first clicks, prepare to create our rectangle.
		root.setOnMousePressed(e ->
		{
            // Erase the previous Rectangle.
			rect.setWidth(0);
			rect.setHeight(0);
			// Set the anchor point for our rectangle.
			origin.x = (int) e.getX();
            origin.y = (int) e.getY();
		});
		
		// As the user drags the rectangle.
		root.setOnMouseDragged(e ->
		{
            // Quadrant four.
            if (e.getX() > origin.getX() && e.getY() > origin.getY())
            {
                modifyRectangle(rect, origin.getX(), origin.getY(), e.getX() - origin.getX(), e.getY() - origin.getY());
            }
            // Quadrant one.
            else if (e.getX() > origin.getX() && e.getY() < origin.getY())
            {
                modifyRectangle(rect, origin.getX(), e.getY(), e.getX() - origin.getX(), origin.getY() - e.getY());
            }
            // Quadrant two.
            else if (e.getX() < origin.getX() && e.getY() < origin.getY())
            {
                modifyRectangle(rect, e.getX(), e.getY(), origin.getX() - e.getX(), origin.getY() - e.getY());
            }
            // Quadrant three.
            else
            {
                modifyRectangle(rect, e.getX(), origin.getY(), origin.getX() - e.getX(), e.getY() - origin.getY());
            }
		});
		
		// When user lets go, finalize the rectangle.
		root.setOnMouseReleased(e ->
		{
			// Set the coordinates that the user drew.
			topLeft.x = (int) rect.getX();
			topLeft.y = (int) rect.getY();
			bottomRight.x = (int) rect.getX() + (int) rect.getWidth();
			bottomRight.y = (int) rect.getY() + (int) rect.getHeight();
			// Close the square selector.
            close();
		});
		
		Scene scene = new Scene(root, Logic.display.getWidth(), Logic.display.getHeight());
		scene.setFill(Color.TRANSPARENT);
		setScene(scene);

		// Display the Scene and wait for a close request to occur.
		showAndWait();
	}

    /**
     * Convenience method to assist in changing the location and direction of rectangles.
     * @param shape provided.
     * @param x coordinate.
     * @param y coordinate.
     * @param width of the Rectangle.
     * @param height of the Rectangle.
     */
    private void modifyRectangle(Rectangle shape, double x, double y, double width, double height)
    {
        shape.setX(x);
        shape.setY(y);
        shape.setWidth(width);
        shape.setHeight(height);
    }
}