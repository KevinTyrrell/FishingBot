package view;

import javafx.event.Event;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import model.Tools;

/**
 * Project Name: FishingBot
 * Author: Kevin
 * Date: Dec 23, 2016
 * Description:
 * Used for the additional stages FishingBot requires.
 * Protects the program from actions like unintended closes,
 * resizing, clicking-through, etc.
 */
public abstract class ObtrusiveWindow extends Stage
{
    public ObtrusiveWindow(final Window owner)
    {
        /* Considers both windows as 'FishingBot' */
        initOwner(owner);
        initStyle(StageStyle.TRANSPARENT);
        /* Doesn't allow the user to click out of the stage. */
        initModality(Modality.APPLICATION_MODAL);
        /* Do not allow the user to close the stage. */
        setOnCloseRequest(Event::consume);
        /* User should not be able to resize our stage. */
        setResizable(false);

        /* Center the window. */
        setX(Tools.USER_MAIN_DISPLAY.getWidth() / 2 - 200);
        setY(Tools.USER_MAIN_DISPLAY.getHeight() / 2 - 200);
    }
}
