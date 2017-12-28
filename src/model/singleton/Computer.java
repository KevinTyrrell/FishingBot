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

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.TimeUnit;

/*
 * Name: Kevin Tyrrell
 * Date: 7/19/2017
 */
public enum Computer
{
    INSTANCE;

    /** Robot to help us communicate with the mouse and keyboard. */
    private final Robot bot = createRobot();
    /** Toolkit for interfacing with the operating system.. */
    private final Toolkit TOOLKIT = Toolkit.getDefaultToolkit();
    /** The user's clipboard for copy pasting. */
    private final Clipboard CLIPBOARD = TOOLKIT.getSystemClipboard();
    /** The user's main display. */
    private final Dimension MAIN_DISPLAY = TOOLKIT.getScreenSize();
    /** The width of the user's main display. */
    private final int screenWidth = (int)MAIN_DISPLAY.getWidth();
    /** The height of the user's main display. */
    private final int screenHeight = (int)MAIN_DISPLAY.getHeight();

    /** Delay between a key press and a key release. */
    private final long KEY_PRESS_BUFFER = 20;

    /**
     * Types a specified String into the current window.
     * The `Enter` key must be pressed twice in order to 
     * open and close the in-game chat window.
     * @param msg - Message to type.
     */
    public void type(final String msg)
    {
        assert msg != null;
        
        /* Stores the clipboards data -- parameter is currently unused. */
        final Transferable prevData = CLIPBOARD.getContents(null);
        CLIPBOARD.setContents(new StringSelection(msg), null);
        
        /* Copy paste the message into the in-game chat. */
        keyPush(KeyEvent.VK_ENTER);
        bot.keyPress(KeyEvent.VK_CONTROL);
        keyPush(KeyEvent.VK_V);
        bot.keyRelease(KeyEvent.VK_CONTROL);
        keyPush(KeyEvent.VK_ENTER);
        
        /* Place the previous clipboard data back into the clipboard. */
        CLIPBOARD.setContents(prevData, null);
    }

    /**
     * Takes a screenshot of the user's primary monitor.
     * @return - BufferedImage Screenshot of the monitor.
     */
    public BufferedImage screenshot()
    {
        return bot.createScreenCapture(new Rectangle(MAIN_DISPLAY));
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
     * Opens the browser to a specific URL.
     * @param url - URL to open.
     */
    public void openWebpage(final String url)
    {
        assert url != null;
        try
        {
            Desktop.getDesktop().browse(new URI(url));
        }
        catch (IOException | URISyntaxException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * Email a given address using the mailto protocol.
     * @param address - Email address to mail.
     */
    public void email(final String address)
    {
        assert address != null;
        final String emailFormat = ".+@.+\\..+";
        assert address.matches(emailFormat);
        final String protocol = "mailto:";
        
        /* Abandon if their platform does not support emailing. */
        if (Desktop.isDesktopSupported()) return;

        final Desktop desktop = Desktop.getDesktop();
        if (desktop.isSupported(Desktop.Action.MAIL))
            try
            {
                final URI target = new URI(protocol.concat(address));
                try
                {
                    desktop.mail(target);
                }
                catch (final IOException e)
                {
                    e.printStackTrace();
                }
            }
            catch (final URISyntaxException e)
            {
                e.printStackTrace();
            }
    }

    /**
     * Presses and releases a given key with a small delay buffer.
     * @param keyCode - Key to press.
     */
    private void keyPush(final int keyCode)
    {
        bot.keyPress(keyCode);
        try
        {
            TimeUnit.MILLISECONDS.sleep(KEY_PRESS_BUFFER);
        }
        catch (InterruptedException e)
        {
            e.printStackTrace();
        }
        bot.keyRelease(keyCode);
    }

    /**
     * Creates a Robot which has access to the screen.
     * @return - The created robot.
     */
    private Robot createRobot()
    {
        Robot r = null;
        try
        {
            r = new Robot();
        }
        catch (AWTException e)
        {
            e.printStackTrace();
        }
        return r;
    }

    /**
     * @return - Retrieves the Robot.
     */
    public Robot getBot()
    {
        return bot;
    }

    /**
     * @return - Width of the screen.
     */
    public int getScreenWidth()
    {
        return screenWidth;
    }

    /**
     * @return - Height of the screen.
     */
    public int getScreenHeight()
    {
        return screenHeight;
    }
}
