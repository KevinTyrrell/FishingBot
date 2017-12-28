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

package model;

import controller.Controller;
import localization.Lang;

import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.IllegalFormatException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

/*
 * Name: Kevin Tyrrell
 * Date: 7/18/2017
 */
public class AlarmClock
{
    /** Time in which the alarm will go off. */
    private final LocalTime time;
    /** What to do when the alarm triggers.. */
    private final Listener listener;
    /** Name of the alarm clock. */
    private final String name;
    
    /** Thread pool which this alarm clock manages. */
    private final ExecutorService exec = Executors.newSingleThreadExecutor();

    /**
     * @param time - Time in which the alarm will go off.
     * @param listener - What to do when the alarm triggers.
     * @param name - Name of the alarm clock.
     */
    public AlarmClock(final LocalTime time, final Listener listener, final String name)
    {
        assert time != null;
        assert listener != null;
        assert time.isAfter(LocalTime.now());
        this.time = time;
        this.listener = listener;
        this.name = name;
    }

    /**
     * @param time - Time in which the alarm will go off.
     * @param listener - What to do when the alarm triggers.
     */
    public AlarmClock(final LocalTime time, final Listener listener)
    {
        this(time, listener, null);
    }

    /**
     * Starts the alarm clock.
     * The alarm clock will trigger once the amount of time set is over.
     * The listener will be notified when the time is up.
     */
    public void start()
    {
        assert !exec.isShutdown();
        assert time.isAfter(LocalTime.now());
        
        exec.submit(() -> 
        {
            AlarmClock.nap(TimeUnit.SECONDS, 
                    LocalTime.now().until(time, ChronoUnit.SECONDS));
            listener.fire();
        });
        exec.shutdown();
    }

    /**
     * Cancels the Alarm from continuing.
     * The listener will not be notified.
     */
    public void cancel()
    {
        exec.shutdownNow();
    }

    /**
     * Puts the current thread into a nap.
     * The thread may sleep slightly longer or shorter
     * than the specified time, dictated pseudo-randomly.
     * @param unit - Unit of time.
     * @param duration - Duration of that unit to nap for.
     */
    public static void nap(final TimeUnit unit, final long duration)
    {
        assert unit != null;
        assert duration > 0;
        
        if (unit == TimeUnit.MILLISECONDS)
        {
            final float FLUCTUATION = 0.25f;
            final long lower_bound = (long)((1f - FLUCTUATION) * duration), 
                    upper_bound = (long)((1f + FLUCTUATION) * duration),
                    sleep_time = lower_bound + (long)(Math.random() * (upper_bound - lower_bound)),
                    start_ts = System.currentTimeMillis();
            
            if (sleep_time >= 500) // TODO: Remove
                Controller.INSTANCE.getDebugConversation().whisper(
                        String.format(Lang.Locale.DEBUGF_SLEEP_MS.get(), sleep_time));
            try
            {
                TimeUnit.MILLISECONDS.sleep(sleep_time);
            }
            catch (final InterruptedException e)
            {
                assert e != null;
                final long t = System.currentTimeMillis();
                Controller.INSTANCE.getDebugConversation().whisper(
                        String.format(Lang.Locale.DEBUGF_INTERRUPTED.get(),
                                sleep_time, sleep_time - (t - start_ts)));
            }
        }
        else nap(TimeUnit.MILLISECONDS, unit.toMillis(duration));
    }
}
