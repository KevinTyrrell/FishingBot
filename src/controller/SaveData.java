package controller;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import model.Angler;
import model.Lang;

import java.awt.*;
import java.io.*;

/**
 * Project: FishingBot
 * Author: User
 * Created: December 19, 2016
 */
public final class SaveData implements Serializable
{
    private final Point ptCalibration;
    /** Amount of delay for the scanning procedure. */
    private final double scanSpeed;
    /** Amount of sensitivity for the splashing procedure. */
    private final double sensitivity;
    /** Whether or not the user wants his application always on top of other windows.  */
    private final boolean onTop;
    /** Variable to toggle debug mode on or off. */
    private final boolean debug;

    private static final String SAVE_DATA_FILENAME = "FishingBot_Saved_Data";

    public SaveData(final DoubleProperty prpSpeed, final DoubleProperty prpSensitivity,
                    final BooleanProperty prpOnTop, final BooleanProperty prpDebug)
    {
        ptCalibration = Angler.pntCalibration;
        scanSpeed = prpSpeed.get();
        sensitivity = prpSensitivity.get();
        onTop = prpOnTop.get();
        debug = prpDebug.get();
    }

    public void sync(final DoubleProperty prpSpeed, final DoubleProperty prpSensitivity,
                     final BooleanProperty prpOnTop, final BooleanProperty prpDebug)
    {
        Angler.pntCalibration = ptCalibration;
        prpSpeed.set(scanSpeed);
        prpSensitivity.set(sensitivity);
        prpOnTop.set(onTop);
        prpDebug.set(debug);
    }

    /**
     * Saves the given SaveData object to a file.
     * @param data - SaveData object to be saved.
     */
    public static void save(final SaveData data)
    {
        final File f = new File(SAVE_DATA_FILENAME);

        try
        {
            final FileOutputStream fos = new FileOutputStream(f);
            final ObjectOutputStream oos;
            try
            {
                oos = new ObjectOutputStream(fos);
                oos.writeObject(data);
                oos.close();
                Encryption.encryptFile(f);
            }
            catch (IOException e)
            {
                try
                {
                    fos.close();
                }
                catch (IOException e1)
                {
                    Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e1.getMessage()));
                }
                Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
            }
        }
        catch (FileNotFoundException e)
        {
            Controller.sendMessage(Lang.EN_DEBUG_FILE_NOT_FOUND.concat(f.getName()));
        }
    }

    /**
     * Loads a SaveData object from a file.
     * If the file does not exist, abandon the load.
     * @return - SaveData object from the File.
     */
    public static SaveData load()
    {
        final File f = new File(SAVE_DATA_FILENAME);
        if (!f.exists()) return null;

        /* Decrypt the file. */
        Encryption.decryptFile(f);
        /* The data being read from the file. */
        SaveData data = null;

        try
        {
            final FileInputStream fis = new FileInputStream(f);
            final ObjectInputStream ois;
            try
            {
                ois = new ObjectInputStream(fis);
                try
                {
                    data = (SaveData)ois.readObject();
                }
                catch (ClassNotFoundException e)
                {
                    /* This should be impossible to occur. */
                    e.printStackTrace();
                }
                finally
                {
                    ois.close();
                }
            }
            catch (IOException e)
            {
                try
                {
                    fis.close();
                }
                catch (IOException e1)
                {
                    Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e1.getMessage()));
                }
                Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
            }
        }
        catch (FileNotFoundException e)
        {
            Controller.sendMessage(Lang.EN_DEBUG_FILE_NOT_FOUND.concat(f.getName()));
        }

        /* Re-encrypt the file. */
        Encryption.encryptFile(f);

        return data;
    }
}
