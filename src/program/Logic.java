package program;

import gui.GUI;

import java.awt.Color;
import java.awt.DisplayMode;
import java.awt.GraphicsEnvironment;
import java.awt.MouseInfo;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Random;

import static java.awt.event.KeyEvent.*;

/**
 * Abstract class which handles all calculations and loops
 * regarding the different phases of fishing.
 * @author Kevin
 */
public abstract class Logic
{
    /** Robot object which allows us to control the mouse, keyboard, etc. */
    public static Robot pc;
    /** Boolean trigger in order to cancel out or begin the fishing loop. */
    public static boolean fishingActive = false;
    /** Random seed to allow for seudo random 'sleep' times after a successful catch. */
    private static final Random generator = new Random();
    /** Time to compare to when the user wants to terminate based on duration. */
    private static long quitTime = System.currentTimeMillis();
    /** The currently selected Lure. */
    public static Lure selectedLure = null;
    /** Timer which logs when a lure was last used. */
    private static long lureTime = System.currentTimeMillis() - 10 * 60 * 1000;
    /** Statement in-game which will pick up your fishing pole. Used to attach lures. */
    private static final String ATTACH_LURE_COMMAND = "/script PickupInventoryItem(16)";
    // Gets the resolution of the user's main display.
    public static final DisplayMode display = GraphicsEnvironment.getLocalGraphicsEnvironment().
            getDefaultScreenDevice().getDisplayMode();
    /** Point set by the user to dermine where the 'Fishing Bobber' frame is. */
    public static Point calibrationPoint;
    /** Two points which represent the scan area of the 'calibrate' method. */
    public static Point topLeft = new Point((int) (display.getWidth() * 0.8), (int) (display.getHeight() * 0.65));
    public static Point bottomRight = new Point((int) display.getWidth(), (int) (display.getHeight() * 0.9));

    /**
     * Begins fishing by creating a new thread.
     * The fishing will continue until the user presses "End".
     */
    public static void startFishing()
    {
        // Assign certain variables to aid other parts of the program.
        quitTime = System.currentTimeMillis();
        fishingActive = true;

        // Loop should be on it's own thread so it doesn't freeze the GUI.
        new Thread(() -> {
            // Give the user time to click into WoW.
            sleep(3000);

            // Continue until told otherwise.
            while (fishingActive)
            {
                // Check if it's time to re-apply a lure.
                if (selectedLure != null && System.currentTimeMillis() - lureTime > selectedLure.getDuration() * 60 * 1000)
                {
                    // Terminate if the user asked us to when we run out of lures.
                    if (GUI.chkLureLogout.isSelected() && Integer.parseInt(GUI.txfLureCount.getText()) == 0)
                        terminate();
                    Say("/use " + selectedLure.getName());
                    Say(ATTACH_LURE_COMMAND);
                    lureTime = System.currentTimeMillis();
                    GUI.txfLureCount.setText("" + (Integer.parseInt(GUI.txfLureCount.getText()) - 1));
                    sleep(10000);
                }
                else if (!GUI.txfTimeLogout.getText().isEmpty() && System.currentTimeMillis() - quitTime >
                        Integer.parseInt(GUI.txfTimeLogout.getText()) * 60 * 1000)
                    terminate();

                // Start fishing.
                Say("/cast Fishing");

                // Scan for the bobber, then reel the fish in. Print errors if something goes wrong.
                GUI.consoleMessage(scanForBobber() ? (attemptToReel() ? "Splash detected, attempting to reel in." :
                        "Located bobber but failed to detect the splash!") : "Failed to detect the bobber!");
            }
        }).start();
    }

    /**
     * Loops through the middle of the user's main display screen
     * in search for the fishing bobber.
     * IMPORTANT: The mouse MUST be moved as the program searches,
     * because in order to locate the bobber, the mouse MUST be
     * hovered over the bobber as that is the only way to display
     * the 'Fishing Bobber' tooltip in the bottom right hand corner.
     * @return whether it was successful or not.
     */
    private static boolean scanForBobber()
    {
        // Reset the mouse position so we do not immediately hover over the bobber.
        pc.mouseMove(0, 0);

        // For users with slower computers. Their GPU needs time to load the Bobber in.
        sleep(2000);

        // Pixel distance to jump each time. Checking every single pixel takes too long.
        final int HORIZONTAL_JUMP_DISTANCE = display.getWidth() / 42;
        final int VERTICAL_JUMP_DISTANCE = display.getHeight() / 72;

        // Loop through a given area of the display, while skipping over a significant amount of pixels.
        for (int i = (int) (display.getWidth() * 0.3); i < display.getWidth() * 0.7; i = i + HORIZONTAL_JUMP_DISTANCE)
        {
            for (int h = (int) (display.getHeight() * 0.5); h < display.getHeight() * 0.7; h = h + VERTICAL_JUMP_DISTANCE)
            {
                // Move the mouse so the bobber tooltip will appear.
                pc.mouseMove(i, h);

                // Strategic pause here to allow computers without GPU's load the tooltip.
                sleep(10);

                // Check the color of the pixel that the user showed us previously by calibrating.
                Color pixel = pc.getPixelColor((int) calibrationPoint.getX(), (int) calibrationPoint.getY());

                // If there is a gold-ish color there, then the tooltip is showing so we must be over the bobber.
                if (pixel.getRed() > 200 && pixel.getGreen() > 200 && pixel.getBlue() < 50)
                {
                    return true;
                }
            }
        }

        // Bobber could not be located.
        sleep(5000);
        return false;
    }

    /**
     * This method, after having found the bobber, rapidly checks
     * for the splash of water that happens when the fish bites the bobber.
     * This method is only called after the bobber is found.
     * @return whether it was successful or not.
     */
    private static boolean attemptToReel()
    {
        // Sensitivity threshold in which we determine there was too much of color change.
        final double clickThreshold = 30.0;

        // The NORMAL amount of blue around the bobber, before it has splashed.
        Point mouse = MouseInfo.getPointerInfo().getLocation();
        double controlBluePerPixel = checkAroundPixel(mouse.getX(), mouse.getY());

        // The time at which we originally found the bobber.
        long startTime = System.currentTimeMillis();

        // As long as the fishing cast is still going.
        while (System.currentTimeMillis() - startTime < 25000)
        {
            // Prevent crazy amounts of CPU from being used.
            sleep(25);

            // Determine the average amount of blue currently in each pixel.
            mouse = MouseInfo.getPointerInfo().getLocation();
            double currentBluePerPixel = checkAroundPixel(mouse.getX(), mouse.getY()) - controlBluePerPixel;

            // If there is a lot more blue than before...
            if (currentBluePerPixel > clickThreshold)
            {
                // Shift right click.
                pc.keyPress(KeyEvent.VK_SHIFT);
                pc.mousePress(BUTTON3_MASK);
                pc.mouseRelease(BUTTON3_MASK);
                pc.keyRelease(KeyEvent.VK_SHIFT);

                // Sleep between 2 and 7 seconds randomly.
                int randomSleep = generator.nextInt(5000);
                sleep(2000 + randomSleep);

                return true;
            }
        }

        return false;
    }

    /**
     * Different users have different displays, different UI's, etc.
     * This program relies on the fishing bobber tooltip so we need to
     * allow the user specifically define where we can find their tooltip.
     * @return whether the calibration was successful or not.
     */
    public static boolean calibrate()
    {
        GUI.consoleMessage("Please hover over the bobber now.");
        sleep(1000);

        // Allow the user to get ready before calibrating.
        int value = 5;
        for (int i = 0; i < value; i++)
        {
            // Warn the user that calibration is about to begin.
            GUI.consoleMessage("Calibrating in " + (value - i) + " seconds.");
            sleep(1000);
        }

        // Take a screenshot.
        BufferedImage screen = screenshot();

        // Search the calibration point.
        for (int i = topLeft.x; i < bottomRight.x; i++)
        {
            for (int j = topLeft.y; j < bottomRight.y; j++)
            {
                // Check every pixel...
                Color pixel = convertColorFromBytes(screen.getRGB(i, j));

                // If the pixel is a goldish color, then we've found the Tooltip.
                if (pixel.getRed() > 200 && pixel.getGreen() > 200 && pixel.getBlue() < 50)
                {
                    // Move the mouse to indicate that we've found it.
                    pc.mouseMove(i, j);
                    calibrationPoint = new Point(i, j);
                    return true;
                }
            }
        }

        // Failed to calibrate.
        return false;
    }

    /**
     * Searches around a given pixel on the screen
     * and returns a Double value for the average
     * amount of BLUE in a given search area.
     * @param x coordinate.
     * @param y coordinate.
     * @return average blue value per pixel.
     */
    private static double checkAroundPixel(double x, double y)
    {
        // Total counter for the amount of BLUE in the search area.
        int total = 0;

        // Amount of pxiels to search = (Radius * 2)^2.
        final int PIXEL_RADIUS = 40;

        // Take a screenshot of the entire display.
        BufferedImage screen = screenshot();

        // Don't allow the loop to go out of bounds.
        int xAxisStart = (int) ((x - PIXEL_RADIUS < 0) ? 0 : x - PIXEL_RADIUS);
        int xAxisEnd = (int) ((x + PIXEL_RADIUS > screen.getWidth()) ? screen.getWidth() : x + PIXEL_RADIUS);
        int yAxisStart = (int) ((y - PIXEL_RADIUS < 0) ? 0 : y - PIXEL_RADIUS);
        int yAxisEnd = (int) ((y + PIXEL_RADIUS > screen.getWidth()) ? screen.getWidth() : y + PIXEL_RADIUS);

        // Loop through the designated pixel area.
        for (int i = xAxisStart; i < xAxisEnd; i++)
        {
            for (int h = yAxisStart; h < yAxisEnd; h++)
            {
                // Total up the amount of BLUE in this area.
                total += (screen.getRGB(i, h)) & 0xFF;
            }
        }

		/*
         * Return the average amount of BLUE per pixel.
		 * Note: '*1.0' exists to force 'double divison'.
		 */
        return total / (PIXEL_RADIUS * PIXEL_RADIUS * 1.0);
    }

    /**
     * Checks if a given process is running.
     * This is possibly window's specific.
     * @param process name.
     * @return whether the process is running or not.
     * @throws IOException
     */
    private static boolean processIsRunning(String process) throws IOException
    {
        String line;
        String pidInfo = "";

        Process p = Runtime.getRuntime().exec(System.getenv("windir") + "\\system32\\" + "tasklist.exe");
        BufferedReader input = new BufferedReader(new InputStreamReader(p.getInputStream()));

        while ((line = input.readLine()) != null)
        {
            pidInfo += line;
        }
        input.close();

        return pidInfo.toLowerCase().contains(process.toLowerCase());
    }

    /**
     * Converts a Color from Byte code to a Java.awt Color.
     * @param color integer.
     * @return Color object.
     */
    private static Color convertColorFromBytes(int color)
    {
        int alpha = (color >>> 24) & 0xFF;
        int red = (color >>> 16) & 0xFF;
        int green = (color >>> 8) & 0xFF;
        int blue = (color >>> 0) & 0xFF;

        return new Color(red, green, blue, alpha);
    }

    /**
     * Takes a BufferedImage screenshot of the current
     * user's primary monitor, in case the user has multiple monitors.
     * @return BufferedImage screenshot.
     */
    private static BufferedImage screenshot()
    {
        return pc.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
    }

    /**
     * Method in order to type in chat.
     * The bot will use this to call '/cast Fishing'
     * @param sentence to say in-game.
     */
    private static void Say(String sentence)
    {
        Press(KeyEvent.VK_ENTER);

        for (int i = 0; i < sentence.length(); i++)
        {
            type(sentence.charAt(i));
        }

        sleep(50);
        Press(KeyEvent.VK_ENTER);
    }

    /**
     * Helper function to press a key on the keyboard.
     * @param key code.
     */
    private static void Press(int key)
    {
        pc.keyPress(key);
        pc.delay(100);
        pc.keyRelease(key);
    }

    /**
     * Given a char value, this method will type
     * that given key via virtual keys.
     * @param character to type.
     */
    private static void type(char character)
    {
        switch (character)
        {
        // @formatter:off
        case 'a': doType(VK_A); break;
        case 'b': doType(VK_B); break;
        case 'c': doType(VK_C); break;
        case 'd': doType(VK_D); break;
        case 'e': doType(VK_E); break;
        case 'f': doType(VK_F); break;
        case 'g': doType(VK_G); break;
        case 'h': doType(VK_H); break;
        case 'i': doType(VK_I); break;
        case 'j': doType(VK_J); break;
        case 'k': doType(VK_K); break;
        case 'l': doType(VK_L); break;
        case 'm': doType(VK_M); break;
        case 'n': doType(VK_N); break;
        case 'o': doType(VK_O); break;
        case 'p': doType(VK_P); break;
        case 'q': doType(VK_Q); break;
        case 'r': doType(VK_R); break;
        case 's': doType(VK_S); break;
        case 't': doType(VK_T); break;
        case 'u': doType(VK_U); break;
        case 'v': doType(VK_V); break;
        case 'w': doType(VK_W); break;
        case 'x': doType(VK_X); break;
        case 'y': doType(VK_Y); break;
        case 'z': doType(VK_Z); break;
        case 'A': doType(VK_SHIFT, VK_A); break;
        case 'B': doType(VK_SHIFT, VK_B); break;
        case 'C': doType(VK_SHIFT, VK_C); break;
        case 'D': doType(VK_SHIFT, VK_D); break;
        case 'E': doType(VK_SHIFT, VK_E); break;
        case 'F': doType(VK_SHIFT, VK_F); break;
        case 'G': doType(VK_SHIFT, VK_G); break;
        case 'H': doType(VK_SHIFT, VK_H); break;
        case 'I': doType(VK_SHIFT, VK_I); break;
        case 'J': doType(VK_SHIFT, VK_J); break;
        case 'K': doType(VK_SHIFT, VK_K); break;
        case 'L': doType(VK_SHIFT, VK_L); break;
        case 'M': doType(VK_SHIFT, VK_M); break;
        case 'N': doType(VK_SHIFT, VK_N); break;
        case 'O': doType(VK_SHIFT, VK_O); break;
        case 'P': doType(VK_SHIFT, VK_P); break;
        case 'Q': doType(VK_SHIFT, VK_Q); break;
        case 'R': doType(VK_SHIFT, VK_R); break;
        case 'S': doType(VK_SHIFT, VK_S); break;
        case 'T': doType(VK_SHIFT, VK_T); break;
        case 'U': doType(VK_SHIFT, VK_U); break;
        case 'V': doType(VK_SHIFT, VK_V); break;
        case 'W': doType(VK_SHIFT, VK_W); break;
        case 'X': doType(VK_SHIFT, VK_X); break;
        case 'Y': doType(VK_SHIFT, VK_Y); break;
        case 'Z': doType(VK_SHIFT, VK_Z); break;
        case '`': doType(VK_BACK_QUOTE); break;
        case '0': doType(VK_0); break;
        case '1': doType(VK_1); break;
        case '2': doType(VK_2); break;
        case '3': doType(VK_3); break;
        case '4': doType(VK_4); break;
        case '5': doType(VK_5); break;
        case '6': doType(VK_6); break;
        case '7': doType(VK_7); break;
        case '8': doType(VK_8); break;
        case '9': doType(VK_9); break;
        case '-': doType(VK_MINUS); break;
        case '=': doType(VK_EQUALS); break;
        case '~': doType(VK_SHIFT, VK_BACK_QUOTE); break;
        case '!': doType(VK_EXCLAMATION_MARK); break;
        case '@': doType(VK_AT); break;
        case '#': doType(VK_NUMBER_SIGN); break;
        case '$': doType(VK_DOLLAR); break;
        case '%': doType(VK_SHIFT, VK_5); break;
        case '^': doType(VK_CIRCUMFLEX); break;
        case '&': doType(VK_AMPERSAND); break;
        case '*': doType(VK_ASTERISK); break;
        case '(': doType(VK_SHIFT, VK_9); break;
        case ')': doType(VK_SHIFT, VK_0); break;
        case '_': doType(VK_UNDERSCORE); break;
        case '+': doType(VK_PLUS); break;
        case '\t': doType(VK_TAB); break;
        case '\n': doType(VK_ENTER); break;
        case '[': doType(VK_OPEN_BRACKET); break;
        case ']': doType(VK_CLOSE_BRACKET); break;
        case '\\': doType(VK_BACK_SLASH); break;
        case '{': doType(VK_SHIFT, VK_OPEN_BRACKET); break;
        case '}': doType(VK_SHIFT, VK_CLOSE_BRACKET); break;
        case '|': doType(VK_SHIFT, VK_BACK_SLASH); break;
        case ';': doType(VK_SEMICOLON); break;
        case ':': doType(VK_COLON); break;
        case '\'': doType(VK_QUOTE); break;
        case '"': doType(VK_QUOTEDBL); break;
        case ',': doType(VK_COMMA); break;
        case '<': doType(VK_SHIFT, VK_COMMA); break;
        case '.': doType(VK_PERIOD); break;
        case '>': doType(VK_SHIFT, VK_PERIOD); break;
        case '/': doType(VK_SLASH); break;
        case '?': doType(VK_SHIFT, VK_SLASH); break;
        case ' ': doType(VK_SPACE); break;
     // @formatter:on
        default:
            throw new IllegalArgumentException("Cannot type character " + character);
        }
    }

    /**
     * Helper method to type multiple keys at once.
     * @param keyCodes to type.
     */
    private static void doType(int... keyCodes)
    {
        doType(keyCodes, 0, keyCodes.length);
    }

    private static void doType(int[] keyCodes, int offset, int length)
    {
        if (length == 0)
        {
            return;
        }

        pc.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        pc.keyRelease(keyCodes[offset]);
    }

    /**
     * Sleep method so I don't have to keep putting 'Try Catches'
     * for the InterruptedException that Thread.sleep() produces.
     * @param milliseconds to sleep for.
     */
    private static void sleep(int milliseconds)
    {
        try
        {
            Thread.sleep(milliseconds);
        }
        catch (InterruptedException e)
        {
            System.out.println("Unable to sleep for " + milliseconds + " milliseconds.");
        }
    }

    private static void terminate()
    {
        Say("/camp");
        System.exit(0);
    }
}
