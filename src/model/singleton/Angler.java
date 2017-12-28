/*
 * Copyright 2017 Kevin Tyrrell
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the "Software"), to deal in the Software without restriction, 
 * including without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the Software is furnished to
 * do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or 
 * substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS
 * FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS 
 * OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY,
 * WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR
 * IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package model.singleton;

import controller.Controller;
import javafx.beans.property.*;
import localization.Macros;
import model.*;
import view.LureButton;

import static localization.Lang.Locale.*;

import java.awt.Color;
import java.awt.MouseInfo;
import java.awt.PointerInfo;
import java.awt.Robot;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.time.LocalTime;
import java.util.OptionalDouble;
import java.util.concurrent.*;

/*
 * Name: Kevin Tyrrell
 * Date: 7/19/2017
 */
public enum Angler
{
    /** Singleton. */
    INSTANCE;

    /** Lure that is currently being used. */
    private final Lure lure = new Lure();
    /** Location of the Fishing Bobber tooltip. */
    private Point ptCalibration = null;
    /** Service which controls the thread the Angler runs on. */
    private ExecutorService exec = null;
    /** Used to know whether to give up on a fishing loop. */
    private BooleanProperty interruptedProperty = new SimpleBooleanProperty();
    
    /** Amount of delay before checking for the Tooltip color. */
    private final LongProperty scanDelayProperty = new SimpleLongProperty();
    /** Amount of sensitivity regarding triggering successful reel-ins. */
    private final DoubleProperty sensitivityProperty = new SimpleDoubleProperty();
    
    /** Computer so we can interface with the hardware. */
    private final Computer comp = Computer.INSTANCE;

    /** Region of the user's screen for scanning for calibration. */
    private final Region calibration = new Region(new model.Point(
            (int)(comp.getScreenWidth() * 0.909375), (int)(comp.getScreenHeight() * 0.873148)),
            (int)(comp.getScreenWidth() * 0.077604), (int)(comp.getScreenHeight() * 0.053703));
    /** Region of the user's screen for scanning for the bobber. */
    private final Region scan = new Region(new model.Point(
            (int)(comp.getScreenWidth() * 0.273958), (int)(comp.getScreenHeight() * 0.512037)),
            (int)(comp.getScreenWidth() * 0.472916), (int)(comp.getScreenHeight() * 0.150000));

    /**
     * Default constructor.
     */
    Angler()
    {
        scanDelayProperty.set(5L); // TODO: Remove this.
        interruptedProperty.addListener((observable, oldValue, newValue) ->
                System.out.println("Changed from " + oldValue + " to " + newValue + "."));
    }

    /**
     * Begins an infinite fishing loop.
     * TODO: Finish documentation.
     */
    public void fish()
    {
        assert isReady();        
        exec = Executors.newSingleThreadExecutor();
        exec.submit(() -> 
        {            
            while (!isInterrupted())
            {
                AlarmClock.nap(TimeUnit.SECONDS, 5);
                
                if (lure.isReady())
                {
                    Controller.INSTANCE.getMainConversation().whisper(
                            String.format(MSGF_LURE_APPLY.get(), lure.getType()));
                    lure.apply();
                }
                
                /* Indicate that we only have 30 seconds find the bobber. */
                final AlarmClock alarm = new AlarmClock(
                        LocalTime.now().plusSeconds(25), () -> interruptedProperty.set(true));
                alarm.start();
                
                Computer.INSTANCE.type(Macros.CAST_FISHING);
                Controller.INSTANCE.getMainConversation().whisper(
                        (scan() ? (reelIn() ? MSG_CAUGHT : MSG_SPLASH404) 
                                : MSG_BOBBER404).get());
                
                alarm.cancel();
                interruptedProperty.set(false);
            }
        });
    }

    /**
     * Checks if the Angler has been calibrated.
     * Calibration means if the program knows where
     * the Fishing Bobber tooltip is.
     * @return - True if the Angler is calibrated.
     */
    public boolean isCalibrated()
    {
        return ptCalibration != null;
    }

    /**
     * Checks if the Angler is isReady to begin fishing.
     * @return - If the Angler is prepared to fish.
     */
    public boolean isReady()
    {
        return exec == null || exec.isTerminated();
    }

    /**
     * Scans the user's screen and searches for the Fishing Bobber.
     * The Fishing Bobber should be located at the calibrated location.
     * The Angler must be calibrated before scanning.
     * @return - True if the Fishing Bobber was located.
     */
    private boolean scan()
    {        
        /* Variables to help us navigate across the user's screen. */
        final int HSTEP = (int)(comp.getScreenWidth() * 0.0238),
                VSTEP = (int)(comp.getScreenHeight() * 0.018518);

        /* For users with slower computers. Their GPU needs time to load the bobber in. */
        AlarmClock.nap(TimeUnit.SECONDS, 2);
        
        final Robot bot = comp.getBot();
        
        try
        {
            /* Stream over most points inside the scan region. */
            return scan.stream(HSTEP, VSTEP)
                    .anyMatch(p ->
                    {
                        if (isInterrupted()) throw new RejectedExecutionException();
                        
                        /* Move the mouse in hopes that we will be over the bobber. */
                        bot.mouseMove(p.getX(), p.getY());
                        /* Wait for the user's GPU to load the tooltip in. */
                        AlarmClock.nap(TimeUnit.MILLISECONDS, scanDelayProperty.get());

                        /* Calibrate automatically. */
                        if (!isCalibrated()) attemptCalibration();

                        /* If we know where the tooltip is, return whether the cursor is over the bobber or not. */
                        return isCalibrated() && tooltipColorMatch(
                                bot.getPixelColor(ptCalibration.getX(), ptCalibration.getY()));
                    });
        }
        catch (final RejectedExecutionException e)
        {
            System.out.println("Rejected Exception!");
            return false;
        }
    }

    /**
     * Attempts to reel in a fish after the bobber was cast into the water.
     * The mouse cursor must be hovering over the bobber.
     * @return - True if a fish was thought to be caught.
     */
    private boolean reelIn()
    {
        assert isCalibrated();

        final PointerInfo mouse = MouseInfo.getPointerInfo();
        /* Location of where the bobber was discovered. */
        final Point bobberLoc = new Point(mouse.getLocation().x, mouse.getLocation().y);
        /* Remember how much blue there was at the start of the reeling. */
        final double controlBlue = gaugeWater(bobberLoc);
        
        /* Continue searching until the cast ends or user stops fishing. */
        while (!isInterrupted())
        {
            /* Sleep to prevent max-CPU usage. */
            AlarmClock.nap(TimeUnit.MILLISECONDS, 25);
            
            /* Percentage between 0 and 1 of change between the control and experimental. */
            final double percentDiff = Math.abs(gaugeWater(bobberLoc) - controlBlue) / controlBlue;
            final double IDEAL_THRESHOLD = 0.08f;
            
            if (percentDiff >= IDEAL_THRESHOLD / 2)
                Controller.INSTANCE.getDebugConversation().whisper(
                        String.format(DEBUGF_SPLASH_DETECTION.get(), percentDiff * 100));
            
            /* Difference is substantial -- bobber might have splashed. */
            // TODO: Hook this up to the front end.
            if (percentDiff >= IDEAL_THRESHOLD)
            {
                final Robot bot = comp.getBot();
                bot.mouseMove(mouse.getLocation().x, mouse.getLocation().y);
                bot.keyPress(KeyEvent.VK_SHIFT);
                bot.mousePress(InputEvent.BUTTON3_DOWN_MASK);
                AlarmClock.nap(TimeUnit.MILLISECONDS, 25);
                bot.mouseRelease(InputEvent.BUTTON3_DOWN_MASK);
                bot.keyRelease(KeyEvent.VK_SHIFT);
                return true;
            }
        }

        return false;
    }

    /**
     * Searches around the cursor and identifies the level of `BLUE` nearby.
     * Taking this reading multiple times allows for easier pixel change detection.
     * @return - Average amount of blue in each pixel near the cursor.
     */
    private double gaugeWater(final Point pt)
    {
        assert pt != null;
        assert pt.getX() <= comp.getScreenWidth();
        assert pt.getY() <= comp.getScreenHeight();
        
        final BufferedImage img = comp.screenshot();

        final int scaledRadius = Math.min(comp.getScreenWidth(), comp.getScreenHeight()) / 2 / 10;
        /* Region of the screen to search for splash detection. */
        final Region searchArea = new Region(new Point(
                Math.max(pt.getX() - scaledRadius, 0), Math.max(pt.getY() - scaledRadius, 0)),
                Math.min(scaledRadius * 2, comp.getScreenWidth()), Math.min(scaledRadius * 2, comp.getScreenHeight()));
        
        /* Average the amount of blue in a region around the mouse. */
        final OptionalDouble val = searchArea.stream()
                .mapToInt(p -> Computer.parseByteColor(img.getRGB(p.getX(), p.getY())).getBlue())
                .average();
        assert val.isPresent();
        return val.getAsDouble();
    }

    /**
     * Attempt to calibrate the Angler by searching for the tooltip.
     * The [Fishing Bobber] tooltip is usually present in the bottom
     * right of the screen and has yellow text. If found, remember
     * the location to allow future fishing methods to work.
     */
    private void attemptCalibration()
    {
        assert isCalibrated();
        final BufferedImage ss = comp.screenshot();
        calibration.stream()
                .filter(pt -> tooltipColorMatch(Computer.parseByteColor(ss.getRGB(pt.getX(), pt.getY()))))
                .findAny()
                .ifPresent(point ->
                {
                    ptCalibration = point;
                    Controller.INSTANCE.getMainConversation().whisper(MSG_CALIBRATED.get());
                });
    }

    /**
     * @return - True if the Angler was interrupted.
     */
    public boolean isInterrupted()
    {
        assert exec != null;
        return exec.isShutdown() || interruptedProperty.get();
    }
    
    public void interrupt()
    {
        assert !isInterrupted();
        exec.shutdownNow();
    }
    
    /**
     * Checks if the given pixel's color matches the tooltip's color.
     * @param pixel - Pixel color to check.
     * @return - True if the specified color matches the tooltip's color.
     */
    private static boolean tooltipColorMatch(final Color pixel)
    {
        assert pixel != null;
        return pixel.getRed() > 200 && pixel.getGreen() > 200 && pixel.getBlue() < 50;
    }
}
