package program;

import java.awt.AWTException;
import java.awt.Color;
import java.awt.GraphicsDevice;
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
import java.time.LocalDateTime;
import java.util.Random;

import static java.awt.event.KeyEvent.*;
import Exceptions.ApplicationNotFound;

public class Logic
{
	private Robot pc;
	private boolean fishingActive;
	private Random generator;
	
	// Gets the resolution of the user's main display.
	GraphicsDevice gd = GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();	
	
	// Set by the user to calibrate the fishing.
	private Point calibrationPoint;

	public Logic()
	{
		fishingActive = false;
		generator = new Random();
		
		try
		{
			pc = new Robot();
		}
		catch (AWTException e)
		{
			// I am not sure how this would occur.
			System.out.println("Unable to communicate with client controls, exiting.");
			System.exit(1);
		}
	}
	
	/**
	 * Look for the "Fishing Bobber" tooltip in the bottom right hand corner.
	 * If yellow pixels are present, return true that the cursor must be over the bobber.
	 * Otherwise, return false indiciating that it was not found.
	 * @return
	 */
	public boolean scanForBobber()
	{			
		for (int i = (int) (gd.getDisplayMode().getWidth() * 0.4); i < gd.getDisplayMode().getWidth() * 0.6; i = i + 45)
		{
			for (int h = (int) (gd.getDisplayMode().getHeight() * 0.3); h < gd.getDisplayMode().getHeight() * 0.7; h = h + 15)
			{
				// The mouse must be over the bobber for the in-game tooltip to pop up.
				pc.mouseMove(i, h);	
				
				// Color pixel = new Color(screenshot().getRGB(1802, 920));
				Color pixel = pc.getPixelColor((int) calibrationPoint.getX(), (int) calibrationPoint.getY());
				
				if (pixel.getRed() > 200 && pixel.getGreen() > 200 && pixel.getBlue() < 50)	// Yellowish color.
				{
					return true;
				}
			}
		}
		
		System.out.println("ERROR Bobber could not be found." );
		
		sleep(5000);		
		
		return false;
	}
	
	public boolean attemptToReel()
	{
		double clickThreshhold = 30.0;
		
		try
		{
			double regular = checkAroundPixel((int) MouseInfo.getPointerInfo().getLocation().getX(), (int) MouseInfo.getPointerInfo().getLocation().getY());
			long startTime = System.currentTimeMillis();
			
			while (System.currentTimeMillis() - startTime < 30000)
			{
				sleep(25);
				
				double value = checkAroundPixel((int) MouseInfo.getPointerInfo().getLocation().getX(), 
						(int) MouseInfo.getPointerInfo().getLocation().getY()) - regular;
				System.out.println("Pixel Avg is : " + (int) value + ", Thresh : " + clickThreshhold);
				if (value > clickThreshhold)
				{				
					pc.keyPress(KeyEvent.VK_SHIFT);
					pc.mousePress(BUTTON3_MASK);
					pc.mouseRelease(BUTTON3_MASK);
					pc.keyRelease(KeyEvent.VK_SHIFT);
					
					int randomSleep = generator.nextInt(5000);
					System.out.println(currentTime()+ " Looted. Sleeping now for " + ((2000 + randomSleep) / 1000) + " seconds.");
					sleep(2000 + randomSleep);	
					
					pc.mouseMove(200, 200);
					
					return true;
				}
			}
			
			System.out.println("ERROR: " + ((System.currentTimeMillis() - startTime) / 1000) + " seconds have passed, no fish found." );
			
			pc.mouseMove(200, 200);
			
			return false;
		}
		// Array is the dimension of the primary monitor.
		catch (java.lang.ArrayIndexOutOfBoundsException e)
		{
			System.out.println("Mouse cursor exited outside of the primary monitor.");
			
			return false;
		}
	}
	
	public boolean calibrate()
	{
		try
		{
			Thread.sleep(3000);
		
			BufferedImage screen = screenshot();
			
			for (int i = (int) (screen.getWidth() * .8); i < screen.getWidth(); i++)
			{
				for (int j = (int) (screen.getHeight() * .8); j < screen.getHeight(); j++)
				{
					Color pixel = convertColorFromBytes(screen.getRGB(i, j));
					
					if (pixel.getRed() > 200 && pixel.getGreen() > 200 && pixel.getBlue() < 50)	// Yellowish color.
					{
						System.out.println("CALIBRATED");
						pc.mouseMove(i, j);
						calibrationPoint = new Point(i, j);
						return true;
					}
				}
			}
		}
		catch (InterruptedException e)
		{
			// TODO Auto-generated catch block
			System.err.println("Could not sleep!");
			e.printStackTrace();
		}		
		
		System.out.println("COULD NOT CALIBRATE");
		
		return false;
	}
	
	private double checkAroundPixel(int x, int y) throws java.lang.ArrayIndexOutOfBoundsException
	{
		int total = 0;
		final int PIXEL_RADIUS = 40;
		
		BufferedImage screen = screenshot();

		for (int i = x - PIXEL_RADIUS; i < x + PIXEL_RADIUS; i++)
		{
			for (int h = y - PIXEL_RADIUS; h < y + PIXEL_RADIUS; h++)
			{
				total += (screen.getRGB(i, h))&0xFF;
			}
		}		
		
		System.out.println(currentTime() + " Searched around pixel.");;
		return total / (PIXEL_RADIUS*PIXEL_RADIUS*1.0);
	}
	
	/**
	 * Checks if specified application is running.
	 * @param process
	 * @return
	 * @throws ApplicationNotFound 
	 * @throws IOException 
	 * @throws Exception
	 */
	public boolean processIsRunning(String process) throws ApplicationNotFound, IOException
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
		
		if (pidInfo.contains(process))
		{
			return true;
		}
		else 
		{
			throw new ApplicationNotFound();
		}
	}
	
	/**
	 * Converts a Color from Byte code to a Java.awt Color.
	 * @param color integer.
	 * @return Color object.
	 */
	private Color convertColorFromBytes(int color)
	{
		int alpha = (color >>> 24) & 0xFF;
		int red   = (color >>> 16) & 0xFF;
		int green = (color >>>  8) & 0xFF;
		int blue  = (color >>>  0) & 0xFF;
		
		return new Color(red, green, blue, alpha);
	}

	/**
	 * Takes a BufferedImage screenshot of the current
	 * user's primary monitor, incase the user has multiple monitors.
	 * @return BufferedImage screenshot.
	 */
	public BufferedImage screenshot()
	{	
		return pc.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
	}

	public void Say(String sentence)
	{
		Press(KeyEvent.VK_ENTER);

		for (int i = 0; i < sentence.length(); i++)
		{
			type(sentence.charAt(i));
		}

		Press(KeyEvent.VK_ENTER);
	}

	private void Press(int key)
	{
		pc.keyPress(key);
		pc.delay(100);
		pc.keyRelease(key);
	}

	public void type(char character) 
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
        case '(': doType(VK_LEFT_PARENTHESIS); break;
        case ')': doType(VK_RIGHT_PARENTHESIS); break;
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

    private void doType(int... keyCodes) 
    {
        doType(keyCodes, 0, keyCodes.length);
    }

    private void doType(int[] keyCodes, int offset, int length) 
    {
    	if (length == 0) 
       	{
            return;
        }

        pc.keyPress(keyCodes[offset]);
        doType(keyCodes, offset + 1, length - 1);
        pc.keyRelease(keyCodes[offset]);
    }
    
    public static void sleep(int miliseconds)
    {
    	try
		{
			Thread.sleep(miliseconds);
		}
		catch (InterruptedException e)
		{
			System.out.println("Unable to sleep for " + miliseconds + " miliseconds.");
		}
    }
    
    /**
     * Prints out the exact time of the day in a 12 hour scale.
     * Does not specify AM/PM.
     * Format is HOUR:MINUTE:SECOND
     * @return String representation of the time of day.
     */
    public static String currentTime()
    {
    	return "" + Math.abs(LocalDateTime.now().getHour() - 12) + ":" + LocalDateTime.now().getMinute() + ":" + LocalDateTime.now().getSecond();
    }

	public boolean isFishingActive()
	{
		return fishingActive;
	}

	public void setFishingActive(boolean fishingActive)
	{
		this.fishingActive = fishingActive;
	}

	public Point getCalibrationPoint()
	{
		return calibrationPoint;
	}

	public void setCalibrationPoint(Point calibrationPoint)
	{
		this.calibrationPoint = calibrationPoint;
	}
}
