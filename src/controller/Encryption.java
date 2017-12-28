package controller;

import localization.Lang;

import java.io.*;

/**
 * Project: FishingBot
 * Author: User
 * Created: December 19, 2016
 */
public final class Encryption
{
    /** Prevent instantiation of the class. */
    private Encryption() { }

    /**
     * Encrypts a given file.
     * Data inside the file can be restored through the decryptFile method.
     * @param f - File to encrypt.
     */
    public static void encryptFile(final File f)
    {
        if (!f.exists()) return;
        final byte[] scanned = readFile(f);
        if (scanned == null) return;
        final byte[] encrypted = encryptBytes(scanned);
        writeToFile(f, encrypted);
    }

    /**
     * Decrypts a given file using the `decrypt` method.
     * @param f - File to decrypt.
     */
    public static void decryptFile(final File f)
    {
        if (!f.exists()) return;
        final byte[] scanned = readFile(f);
        if (scanned == null) return;
        final byte[] decrypted = decryptBytes(scanned);
        writeToFile(f, decrypted);
    }

    /**
     * Encrypts the byte array.
     * @param bytes - Array to encrypt.
     * @return - Encrypted array.
     */
    private static byte[] encryptBytes(final byte[] bytes)
    {
        final byte[] encrypted = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            encrypted[i] = encrypt(bytes[i]);
        return encrypted;
    }

    /**
     * Decrypts the given byte array by returning the values to their original value.
     * @param bytes - Array to decrypt.
     * @return - Decrypted array.
     */
    private static byte[] decryptBytes(final byte[] bytes)
    {
        final byte[] decrypted = new byte[bytes.length];
        for (int i = 0; i < bytes.length; i++)
            decrypted[i] = decrypt(bytes[i]);
        return decrypted;
    }

    /**
     * Writes bytes from the given array into the given file.
     * @param f - File to write to.
     * @param bytes - Bytes to write to the file.
     */
    private static void writeToFile(final File f, final byte[] bytes)
    {
        try
        {
            final FileOutputStream fos = new FileOutputStream(f);
            try
            {
                fos.write(bytes);
            }
            catch (IOException e)
            {
                Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
            }
            finally
            {
                try
                {
                    fos.close();
                }
                catch (IOException e)
                {
                    Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
                }
            }
        }
        catch (FileNotFoundException e)
        {
            Controller.sendMessage(Lang.EN_DEBUG_FILE_NOT_FOUND.concat(f.getName()));
        }
    }

    /**
     * Reads the bytes within the given file and returns them as an array.
     * @param f - File to read from.
     * @return - Bytes contained within the file.
     */
    private static byte[] readFile(final File f)
    {
        try
        {
            final FileInputStream fis = new FileInputStream(f);
            try
            {
                final byte[] ar = new byte[(int)f.length()];
                fis.read(ar, 0, ar.length);
                return ar;
            }
            catch (IOException e)
            {
                Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
            }
            finally
            {
                try
                {
                    fis.close();
                }
                catch (IOException e)
                {
                    Controller.sendMessage(Lang.EN_DEBUG_IO_EXCEPTION.concat(e.getMessage()));
                }
            }
        }
        catch (FileNotFoundException e)
        {
            Controller.sendMessage(Lang.EN_DEBUG_FILE_NOT_FOUND.concat(f.getName()));
        }

        return null;
    }

    /**
     * Encrypts the given byte to a different byte value.
     * The encrypted value can be decrypted back into the original byte.
     * @param b - Byte to encrypt.
     * @return - Encrypted byte.
     */
    private static byte encrypt(final byte b)
    {
        return (byte)((b >= 0 ? Byte.MAX_VALUE : Byte.MIN_VALUE - 1) - b);
    }

    /**
     * Decrypts the given byte back to it's original byte value.
     * @param b - Byte to be decrypted.
     * @return - Decrypted byte.
     */
    private static byte decrypt(final byte b)
    {
        if (b >= 0)
            return (byte)(Math.abs(b - Byte.MAX_VALUE));
        else
            return (byte)(Byte.MIN_VALUE - b - 1);
    }
}
