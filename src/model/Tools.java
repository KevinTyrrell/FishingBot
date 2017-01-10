package model;

import controller.Controller;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;

public final class Tools
{
    /** Prevent instantiation of the class. */
    private Tools() { }

    /** The resolution of the user's main display. */
    public static final DisplayMode USER_MAIN_DISPLAY =
            GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDisplayMode();
    /** Robot to help us communicate with the mouse and keyboard. */
    public static Robot bot;

    private static final int CONTROL_RESOLUTION = 1920 * 1080;

    /**
     * Scales a number based on the resolution on the user's resolution compared
     * to the resolution of what the program was designed for.
     * For example, if the width of the window was designed to be 600px on a 1920x1080 monitor,
     * but is now being loaded on a 1280*800 monitor, then the new `width` would be ~296.
     * @param baseVal - Value it was designed for on 1920x1080.
     * @return - Value scaled based on a previous 1920x1080 value.
     */
    public static double scaleBasedOnRes(final double baseVal)
    {
        return (double) (USER_MAIN_DISPLAY.getWidth() * USER_MAIN_DISPLAY.getHeight()) / CONTROL_RESOLUTION * baseVal;
    }

    /**
     * Searches in a 360 degree sweep of all nearby pixels that
     * are within `RADIUS` units. At each pixel, read the RGB
     * data and sum up only the blue of that pixel. Divide the
     * sum by the total amount of pixels visited and then return
     * the average.
     * @param centerX - X coordinate of the center of the area to check.
     * @param centerY - Y coordinate of the center of the area to check.
     * @return - The average amount of blue in the entire search area.
     */
    public static double avgBlueProximity(final int centerX, final int centerY)
    {
        /* Variables which will help us find the average. */
        int blueSum = 0, pixelCount = 0;
        /* Take a screenshot so we can evaluate the state of the pixels. */
        final BufferedImage ss = Tools.screenshot();

        /* Variables to prevent out of bounds exceptions when looping across the image. */
        final int RADIUS = 75, SQ_R = RADIUS * RADIUS,
                Y_END = Math.min(ss.getHeight() - 1, centerY + RADIUS),
                X_END = Math.min(ss.getWidth() - 1, centerX + RADIUS),
                X_START = Math.max(0, centerX - RADIUS),
                Y_START = Math.max(0, centerY - RADIUS);

        for (int y = Y_START; y <= Y_END; y++)
        {
            /* Y Radius at this given Y coordinate. */
            final int Y_RAD = (y - centerY) * (y - centerY);
            for (int x = X_START; x <= X_END; x++)
            {
                /* Determine if this pixel is inside the circle. */
                if ((x - centerX) * (x - centerX) + Y_RAD <= SQ_R)
                {
                    /* AND with 0xFF will grab only the blue from this pixel. */
                    blueSum += ss.getRGB(x, y) & 0xFF;
                    pixelCount++;
                }
            }
        }

        /* Divide by zero protection. */
        return (pixelCount > 0) ? (double)blueSum / pixelCount : 0;
    }

    /**
     * Returns whether or not it's been long enough from `start` (millis)
     * to this point in time (millis) based on `duration`.
     * @param start - Time in which the duration started.
     * @param duration - Duration of time which is being checked.
     * @return - Whether or not the time duration has passed.
     */
    public static boolean timePassed(final long start, final long duration)
    {
        return System.currentTimeMillis() - start >= duration;
    }

    /**
     * Fluctuate a number randomly based on the FLUCTUATION percentage.
     * @param input - Base number.
     * @return - Fluctuated number based on the parameter.
     */
    public static long fluctuate(long input)
    {
        final float FLUCTUATION = 0.15f;
        return fluctuate(input, FLUCTUATION);
    }

    /**
     * Fluctuate a number either positively or negatively based on
     * the parameter passed in for fluctuation. Fluctuation should
     * be a percentage, so a 15% fluctuation would mean 0.15f.
     * A 15% fluctuation means that an `input` of 100 could yield
     * values between 85 and 115.
     * @param input - Base number.
     * @param fluctuation - Fluctuation percentage.
     * @return - Fluctuated number.
     */
    public static long fluctuate(final long input, final float fluctuation)
    {
        final boolean positive = Math.random() >= 0.5;
        return (long)(input * (1 + (positive ? 1 : -1) * Math.random() * fluctuation));
    }

    /**
     * Sleep function. Notifies the fisherman
     * by changing a flag if the user interrupts sleep.
     * @param milliseconds - Amount of time to sleep for in ms.
     */
    public static void sleep(final long milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            Angler.interrupted = true;
        }
    }

    /**
     * Presses enter, pastes `output` by pressing shift + insert,
     * then presses enter again.
     * @param output - String to output.
     */
    public static void typeStr(final String output)
    {
        /* Delay in-between key press and release. */
        final long PASTE_DELAY = 25;
        try
        {
            Clipboard cb = Toolkit.getDefaultToolkit().getSystemClipboard();
            Transferable data = cb.getContents(null);
            cb.setContents(new StringSelection(output), null);
            bot.keyPress(KeyEvent.VK_ENTER);
            bot.keyRelease(KeyEvent.VK_ENTER);
            sleep(PASTE_DELAY);
            bot.keyPress(KeyEvent.VK_CONTROL);
            bot.keyPress(KeyEvent.VK_V);
            sleep(PASTE_DELAY);
            bot.keyRelease(KeyEvent.VK_V);
            bot.keyRelease(KeyEvent.VK_CONTROL);
            sleep(PASTE_DELAY);
            /* Press enter afterwards. */
            bot.keyPress(KeyEvent.VK_ENTER);
            sleep(PASTE_DELAY);
            bot.keyRelease(KeyEvent.VK_ENTER);
            cb.setContents(data, null);
        }
        catch (IllegalStateException e)
        {
            Controller.sendMessage(e.getMessage());
        }
    }

    /**
     * Check if the specified time is after this time.
     * In addition, double check that this time was no more
     * than 'TIMEFRAME_MINUTES' ago. For example, 11 PM is after
     * 6 AM, but if they are not 'TIMEFRAME_MINUTES' between eachother,
     * then it is not considered 'after'.
     * @param target - Time to check.
     * @return - Whether or not we have passed the target time.
     */
    public static boolean timePassed(final LocalTime target)
    {
        final LocalTime now = LocalTime.now();
        final int TIMEFRAME_MINUTES = 5;
        final long minsDifference = ChronoUnit.MINUTES.between(now, target);
        return now.isAfter(target) && minsDifference <= TIMEFRAME_MINUTES;
    }

    /**
     * Converts a color from byte-code to a Color object.
     * @param color - Byte code of RGBA.
     * @return - Color object.
     */
    public static Color parseByteColor(final int color)
    {
        final int alpha = (color >>> 24) & 0xFF;
        final int red = (color >>> 16) & 0xFF;
        final int green = (color >>> 8) & 0xFF;
        final int blue = color & 0xFF;
        return new Color(red, green, blue, alpha);
    }

    /**
     * Takes a screenshot of the user's primary monitor.
     * @return - BufferedImage Screenshot of the monitor.
     */
    public static BufferedImage screenshot()
    {
        return bot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    /**
     * Opens the browser to a specific URL.
     * @param url - URL to open.
     */
    public static void openWebpage(final String url)
    {
        try
        {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (final IOException e)
        {
            Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
        }
        catch (final URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    public static void email(final String email)
    {
        final String MAIL_TO = "mailto:";
        final Desktop desktop;
        if (Desktop.isDesktopSupported()
                && (desktop = Desktop.getDesktop()).isSupported(Desktop.Action.MAIL))
        {
            final URI target;
            try
            {
                target = new URI(MAIL_TO.concat(email).concat("?subject=Hello%20World!"));
                desktop.mail(target);
            }
            catch (URISyntaxException e)
            {
                e.printStackTrace();
            }
            catch (IOException e)
            {
                Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
            }
        }
        else
        {
            // TODO fallback to some Runtime.exec(..) voodoo?
            throw new RuntimeException("desktop doesn't support mailto; mail is dead anyway ;)");
        }
    }
}