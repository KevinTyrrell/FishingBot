package view;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Rectangle;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import model.Lang;
import model.Tools;

import java.awt.*;

public class SelectionWindow extends ObtrusiveWindow
{
    /** Area in which the user manipulates. */
	private final Rectangle selection;
    /** The coordinates of the user's mouse press before the drag. */
    private double originX = 0.0, originY = 0.0;

	public SelectionWindow(final Window owner)
	{
        super(owner);

		/* Window is a child of the main window. */
		initStyle(StageStyle.TRANSPARENT);
        setMaximized(true);

        /* Rectangle properties. */
        /* By default, set the rectangle to cover the area where the tooltip USUALLY is. */
        final double DEFAULT_X = Tools.scaleBasedOnRes(1920 * 0.85),
                DEFAULT_Y = Tools.scaleBasedOnRes(1080 * 0.75),
                DEFAULT_WIDTH = Tools.scaleBasedOnRes(1920 * 0.98) - DEFAULT_X,
                DEFAULT_HEIGHT = Tools.scaleBasedOnRes(1080 * 0.95) - DEFAULT_Y;
        selection = new Rectangle(DEFAULT_X, DEFAULT_Y, DEFAULT_WIDTH, DEFAULT_HEIGHT);
        final int SELECTION_STROKE = 8;
        selection.setStrokeWidth(Tools.scaleBasedOnRes(SELECTION_STROKE));
        selection.setFill(Color.TRANSPARENT);

        /* Rainbow gradient color. */
        final LinearGradient rainbow = new LinearGradient(0f, 1f, 1f, 0f, true, CycleMethod.NO_CYCLE,
                new Stop(0, Color.web("#f8bd55")),
                new Stop(0.14, Color.web("#c0fe56")),
                new Stop(0.28, Color.web("#5dfbc1")),
                new Stop(0.43, Color.web("#64c2f8")),
                new Stop(0.57, Color.web("#be4af7")),
                new Stop(0.71, Color.web("#ed5fc2")),
                new Stop(0.85, Color.web("#ef504c")),
                new Stop(1, Color.web("#f2660f")));
        selection.setStroke(rainbow);

        /* Set the anchor point for the selection, so MOUSE_DRAGGED event handler can utilize it. */
        addEventHandler(MouseEvent.MOUSE_PRESSED, e ->
        {
            /* Rick clicks will close the window. */
            if (e.getButton() == MouseButton.SECONDARY)
                close();
            else
            {
                /* Remember where the user FIRST clicked, for later purposes. */
                originX = e.getX();
                originY = e.getY();
                selection.setX(originX);
                selection.setY(originY);
                selection.setWidth(0);
                selection.setHeight(0);
                /* Display the reset rectangle to the user. */
                selection.setStroke(rainbow);
            }

        });

        /* As user drags the square in different ways, mutate the rectangle accordingly. */
        addEventHandler(MouseEvent.MOUSE_DRAGGED, e ->
        {
            final double x, y, width, height;

            /* User dragged mouse to the left of his original mouse press. */
            if (e.getX() <= originX)
            {
                x = e.getX();
                width = originX - e.getX();
            }
            else
            {
                x = originX;
                width = e.getX() - originX;
            }

            /* User dragged mouse above his original mouse press. */
            if (e.getY() <= originY)
            {
                y = e.getY();
                height = originY - e.getY();
            }
            else
            {
                y = originY;
                height = e.getY() - originY;
            }

            /* Update the rectangle so it keeps up with the user's dragging. */
            selection.setWidth(width);
            selection.setHeight(height);
            selection.setX(x);
            selection.setY(y);
        });

        /* Close the window when the user ends the drag. */
        addEventHandler(MouseEvent.MOUSE_RELEASED, e -> close());

        /* Label for displaying information while the screen is visible. */
        final int LABEL_SIZE = (int)Tools.scaleBasedOnRes(160), LABEL_INSETS = -15,
        FONT_SIZE = (int)Tools.scaleBasedOnRes(18), CORNER_CURVE = 10;
        final Label lblInstruction = new Label(Lang.EN_LABEL_SELECT);
        lblInstruction.setTextAlignment(TextAlignment.CENTER);
        lblInstruction.setMaxSize(LABEL_SIZE, LABEL_SIZE);
        lblInstruction.setTextFill(Color.WHITE);
        lblInstruction.setFont(Font.font("Calibri", FONT_SIZE));
        lblInstruction.setBackground(new Background(new BackgroundFill(Color.BLACK,
                new CornerRadii(CORNER_CURVE), new Insets(LABEL_INSETS))));
        lblInstruction.setWrapText(true);

        /* Rectangle needs to exist on a `Pane` but the label needs to be on top, so use StackPane. */
        final StackPane root = new StackPane(new Pane(selection), lblInstruction);
        root.setAlignment(Pos.CENTER);
        root.setStyle("-fx-background-color: rgba(255, 255, 255, 0.3);");

		/* Height will cover the user's entire screen. */
		final Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
	}

    /**
     * Top left point of the selection.
     * @return - Left bound.
     */
    public Point getLeft()
    {
        return new Point((int)selection.getX(), (int)selection.getY());
    }

    /**
     * Bottom right point of the selection.
     * @return - Right bound.
     */
    public Point getRight()
    {
        return new Point((int)(selection.getX() + selection.getWidth()),
                (int)(selection.getY() + selection.getHeight()));
    }

    public Rectangle getRect()
    {
        return selection;
    }
}