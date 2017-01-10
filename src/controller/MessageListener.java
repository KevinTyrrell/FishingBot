package controller;

/** Listener interface for String communication between classes. */
public interface MessageListener
{
    void fire(final String message);
}
