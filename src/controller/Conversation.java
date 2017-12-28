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

package controller;

import java.util.LinkedHashSet;
import java.util.Set;

/*
 * Name: Kevin Tyrrell
 * Date: 7/22/2017
 */
public class Conversation
{
    /** Listeners who are listening in on this conversation. */
    private final Set<MessageListener> listeners = new LinkedHashSet<>();

    /**
     * Whispers a message to the conversation, but is heard
     * by any listeners of the conversation.
     * @param message - Message to be heard by the listeners.
     */
    public void whisper(final String message)
    {
        assert message != null;
        listeners.forEach(m -> m.fire(message));
    }

    /**
     * Have this listener listen in on the conversation.
     * @param listener - Listener who wants to listen in.
     */
    public void listenIn(final MessageListener listener)
    {
        assert listener != null;
        assert !listeners.contains(listener);
        listeners.add(listener);
    }

    /**
     * Removes a listener from listening to this conversation.
     * @param listener - Listener which no longer wishes to listen in.
     */
    public void stopListening(final MessageListener listener)
    {
        assert listener != null;
        assert listeners.contains(listener);
        listeners.remove(listener);
    }

    /**
     * Interface used for listening in on conversations.
     */
    public interface MessageListener
    {
        /**
         * Informs this listener of a message that was received.
         * @param message - Message that has been received.
         */
        void fire(final String message);
    }
}
