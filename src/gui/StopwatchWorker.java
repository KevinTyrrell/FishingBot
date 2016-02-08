package gui;

import java.time.Duration;
import java.time.LocalDateTime;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.concurrent.Task;

public class StopwatchWorker extends Task<Void>
{

	private BooleanProperty stop = new SimpleBooleanProperty(false);
	private LocalDateTime StartDateTime;
	private LocalDateTime StopDateTime;
	private BooleanProperty pause = new SimpleBooleanProperty(false);

	@Override protected Void call() throws Exception
	{

		StartDateTime = LocalDateTime.now();
		while (!stop.getValue())
		{
			if (!pause.getValue())
			{
				StopDateTime = LocalDateTime.now();
				Duration d = Duration.between(StartDateTime, StopDateTime);

				long hours = Math.max(0, d.toHours());
				long minutes = Math.max(0, d.toMinutes() - 60 * d.toHours());
				long seconds = Math.max(0, d.getSeconds() - 60 * d.toMinutes());
				long millis = Math.max(0, d.toMillis() - d.getSeconds() * 1000);

				updateMessage(String.format("%02d", hours) + ":"
						+ String.format("%02d", minutes) + ":"
						+ String.format("%02d", seconds) + "."
						+ String.format("%03d", millis));
			}
			Thread.sleep(3);
		}
		return null;
	}

	public Boolean getPause()
	{
		return pause.getValue();
	}

	public LocalDateTime getStartDateTime()
	{
		return StartDateTime;
	}

	public Boolean getStop()
	{
		return stop.getValue();
	}

	public LocalDateTime getStopDateTime()
	{
		return StopDateTime;
	}

	public BooleanProperty pauseProperty()
	{
		return this.pause;
	}

	public void setPause(Boolean pause)
	{
		this.pause.setValue(pause);
	}

	public void setStop(Boolean stop)
	{
		this.stop.setValue(stop);
	}

	public void stop()
	{
		setStop(true);
	}

	public BooleanProperty stopProperty()
	{
		return this.stop;
	}

}
