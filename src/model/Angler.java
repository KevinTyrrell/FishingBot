package model;

import controller.Controller;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.*;
import view.GUI;

import java.awt.*;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.util.concurrent.TimeUnit;

public final class Angler implements Runnable
{
    /** Flag indicating the user interrupted fishing. */
    public static boolean interrupted = false;
    /** Lure which enables control over what Lure the in-game character is using. */
    private final Lure lure;
    /** Property which represents if we are currently fishing or not. */
    private final BooleanProperty fishing = new SimpleBooleanProperty(false);
    private final BooleanProperty lureQuit;
    /** Point which is where a pixel of the Fishing Bobber tooltip is. */
    public static Point pntCalibration = null;
    /** Amount of delay before checking for the Tooltip color. */
    private final DoubleProperty scanSpeedProperty = new SimpleDoubleProperty();
    /** Amount of sensitivity regarding triggering successful reel-ins. */
    private final DoubleProperty sensitivityProperty = new SimpleDoubleProperty();
    /** The time the user wishes to quit at. NULL if doesn't want to quit. */
    private final ObjectProperty<LocalTime> timeProperty = new SimpleObjectProperty<>();

    /** Initialize the robot so we can use it. */
    public Angler(final DoubleBinding bndScanDelay, final DoubleBinding bndSensitivity,
            final BooleanProperty btnDisableProperty, final ObjectProperty<LureType> lureProperty,
            final ObjectProperty<Integer> quantityProperty, final ObjectBinding<LocalTime> timeBinding,
            final BooleanProperty lureQuit)
    {
        try
        {
            Tools.bot = new Robot();
        }
        catch (AWTException e)
        {
            Controller.sendMessage(Lang.EN_EXCEPTION_ROBOT + e.getMessage());
        }
        /* Set up connections with the front end. */
        lure = new Lure(quantityProperty);
        scanSpeedProperty.bind(bndScanDelay);
        sensitivityProperty.bind(bndSensitivity);
        btnDisableProperty.bind(fishingProperty());
        lure.typeProperty().bind(lureProperty);
        this.timeProperty.bind(timeBinding);
        this.lureQuit = lureQuit;
    }

    @Override
    public void run()
    {
        fish();
    }

    /**
     * Primary fishing loop.
     * Continue infinitely if no max time is specified.
     * Attempt to cast your fishing rod and reel in a fish if one exists.
     * Output to the console if the fishing was successful or not.
     * Sleep for a random amount of time between (BASE_SLEEP / 1000)
     * and (BASE_SLEEP / 1000) + DELAY_VARIANCE amount of seconds.
     */
    private void fish()
    {
        fishing.set(true);
        final int DELAY_VARIANCE = 5000, BASE_SLEEP = 2000;

        Tools.sleep(BASE_SLEEP);
        while (!interrupted)
        {
            /* Logout if either the user set a logout time or we ran out of lures. */
            final LocalTime logTime = timeProperty.get();
            if ((logTime != null && Tools.timePassed(logTime))
                    || (lureQuit.get() && lure.isOutOfLures()))
            {
                Tools.typeStr(Lang.EN_LOGOUT);
                Controller.sendMessage(Lang.EN_MSG_LOGOUT_CONFIRM);
                break;
            }

            /* If a lure needs to be re-applied, use one. */
            if (lure.shouldApply()) lure.apply();

            Tools.typeStr(Lang.EN_CAST_FISHING);
            if (scan())
                Controller.sendMessage(reelIn() ? Lang.EN_MSG_FISH_CAUGHT
                                                : Lang.EN_ERROR_SPLASH_MISSING);
            else
                Controller.sendMessage(Lang.EN_ERROR_BOBBER_MISSING);
            /* Sleep for at least BASE_SLEEP plus an additional random amount. */
            Tools.sleep(BASE_SLEEP + Tools.fluctuate((long) (DELAY_VARIANCE * Math.random())));
        }
        fishing.set(false);
    }

    /**
     * Search the user's display for the fishing bobber.
     * Grab the mouse and move it across a grid of x,y locations.
     * If a tooltip appears, that must mean we have hovered over
     * the fishing bobber. Calibration must be correct for this to work.
     * @return - True if the bobber was located, false if otherwise.
     */
    private boolean scan()
    {
        final double RANGE_SCALE = 0.3, HALF = 0.5;

        /* Variables to help us navigate across the user's screen. */
        final int DELAY_TIME = 2000,
                /* Loop through RANGE_SCALE% of the user's display. */
                WIDTH = Tools.USER_MAIN_DISPLAY.getWidth(), HEIGHT = Tools.USER_MAIN_DISPLAY.getHeight(),
                Y_START = (int)(HEIGHT * HALF), Y_END = (int)(HEIGHT * (1 - RANGE_SCALE)),
                X_START = (int)(WIDTH * RANGE_SCALE), X_END = (int)(WIDTH * (1 - RANGE_SCALE)),
                X_PIX_SKIP = WIDTH / 42, Y_PIX_SKIP = HEIGHT / 72;

        /* Reset the mouse position so we don't accidentally hover over the bobber again. */
        Tools.bot.mouseMove(0, 0);
        /* For users with slower computers. Their GPU needs time to load the bobber in. */
        Tools.sleep(DELAY_TIME);

        /* Loop through the center portion of the user's screen. */
        for (int y = Y_START; y < Y_END; y += Y_PIX_SKIP)
            for (int x = X_START; x < X_END; x += X_PIX_SKIP)
            {
                if (interrupted) return false;
                /* Move the mouse in hopes that we will be over the bobber. */
                Tools.bot.mouseMove(x, y);
                /* Wait for the user's GPU to load the tooltip in. */
                Tools.sleep((long) scanSpeedProperty.get());
                /* If the color at the tooltip location matches what colors that
                   we know the tooltip is, then we found the bobber. */
                final Color pixelColor = Tools.bot.getPixelColor(pntCalibration.x, pntCalibration.y);
                if (colorIsTooltip(pixelColor))
                    return true;
            }

        return false;
    }

    /**
     * Called when we know the mouse is right on-top of the fishing bobber.
     * Scan the around around the mouse cursor and find the average amount of
     * blue in a circular radius. If the blue ever changes too much from what
     * it was originally, the splash may have occured, so attempt to loot the fish.
     * @return - True if a splash was detected, false if the function gave up after GIVE_UP_TS.
     */
    private boolean reelIn()
    {
        final Point mouse = MouseInfo.getPointerInfo().getLocation();
        final long START_TS = System.currentTimeMillis(), GIVE_UP_TS = 26000;
        final int CPU_DELAY = 25;
        /* If the user moves his mouse, then we will still have memory of the right coordinates. */
        final int MOUSE_X = mouse.x, MOUSE_Y = mouse.y;

        /* Determine how much blue there WAS at the start of this cycle. */
        final double ctrlBlue = Tools.avgBlueProximity(MOUSE_X, MOUSE_Y);

        /* As long as the in-game cast is still going, there's hope of catching the fish. */
        while (!interrupted && !Tools.timePassed(START_TS, GIVE_UP_TS))
        {
            /* Sleep to prevent max-CPU usage. */
            Tools.sleep(CPU_DELAY);
            /* Find the average blue where the mouse is */
            final double avgBlue = Tools.avgBlueProximity(MOUSE_X, MOUSE_Y);
            final double diff = Math.abs(ctrlBlue - avgBlue);
            if (Controller.debugMode.get())
                Controller.sendMessage(Lang.EN_DEBUG_COLOR_THRESH.replaceFirst("%1",
                        String.format("%.2f", diff))
                        .replaceFirst("%2", String.format("%.2f", sensitivityProperty.get())));

            /* If the difference in blue changed enough, the bobber just splashed! */
            if (Math.abs(ctrlBlue - avgBlue) >= sensitivityProperty.get())
            {
                /* Shift right click to loot the fish. */
                Tools.bot.mouseMove(MOUSE_X, MOUSE_Y);
                Tools.bot.keyPress(KeyEvent.VK_SHIFT);
                Tools.bot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                Tools.bot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                Tools.bot.keyRelease(KeyEvent.VK_SHIFT);
                return true;
            }
        }

        return false;
    }

    /**
     * Checks if the Angler knows where the Fishing Bobber tooltip is.
     * @return - If the user has previously calibrated the program.
     */
    public boolean isCalibrated()
    {
        return pntCalibration != null;
    }

    /**
     * Attempts to calibrate the program by locating the Fishing Bobber tooltip.
     * If found, the point at which it was found at is saved and thus the program is calibrated.
     * @return - Whether or not the Tooltip was found.
     */
    public boolean calibrate()
    {
        final int PREP_TIME = 2000, COUNTDOWN_TIME = 5;

        /* Countdown and inform the user that we are about to calibrate. */
        Controller.sendMessage(Lang.EN_MSG_CALIBRATE_START);
        Tools.sleep(PREP_TIME);
        for (int i = COUNTDOWN_TIME; i > 0; i--)
        {
            Controller.sendMessage(Lang.EN_MSG_CALIBRATE_RUNNING + i);
            Tools.sleep(TimeUnit.SECONDS.toMillis(1));
        }

        /* Capture the user's screen so we can search for the tooltip. */
        final BufferedImage ss = Tools.screenshot();

        final Point left = GUI.winSelect.getLeft(), right = GUI.winSelect.getRight();
        for (int y = left.y; y < right.y; y++)
            for (int x = left.x; x < right.x; x++)
            {
                Color pixelColor = Tools.parseByteColor(ss.getRGB(x, y));
                if (colorIsTooltip(pixelColor))
                {
                    /* Set this location as the location of the Fishing Bobber tooltip. */
                    pntCalibration = new Point(x, y);
                    /* Move the mouse there to inform the user of where the program found that color. */
                    Tools.bot.mouseMove(x, y);
                    return true;
                }
            }

        return false;
    }

    /**
     * Test if a given color meets the conditions for what the Fishing Bobber tooltip SHOULD have.
     * @param c - Color in which we are testing.
     * @return - Whether or not the color matches the tooltip.
     */
    private boolean colorIsTooltip(final Color c)
    {
        return c.getRed() > 200 && c.getGreen() > 200 && c.getBlue() < 50;
    }

    public BooleanProperty fishingProperty()
    {
        return fishing;
    }

    public Lure getLure()
    {
        return lure;
    }

    public ObjectProperty<LocalTime> timeProperty()
    {
        return timeProperty;
    }
}
