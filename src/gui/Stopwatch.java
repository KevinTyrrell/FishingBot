package gui;

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;

public class Stopwatch extends Region
{

	private enum StopWatchStatus
	{
		STOPPED, RUNNING, PAUSE
	}

	StopwatchWorker stopwatchWorker;
	StopWatchStatus currentStatus = StopWatchStatus.STOPPED;

	public Stopwatch()
	{
		Label l = new Label("00:00:00.000");

		Button startStop = new Button("START");
		ToggleButton pause = new ToggleButton();
		pause.setDisable(true);
		Button reset = new Button("RESET");

		HBox hBox = new HBox();
		hBox.setSpacing(5);
		hBox.getChildren().addAll(startStop, pause, reset);
		HBox.setHgrow(startStop, Priority.ALWAYS);
		HBox.setHgrow(pause, Priority.ALWAYS);
		HBox.setHgrow(reset, Priority.ALWAYS);
		startStop.setMaxWidth(Double.MAX_VALUE);
		pause.setMaxWidth(Double.MAX_VALUE);
		reset.setMaxWidth(Double.MAX_VALUE);

		VBox vBox = new VBox();
		vBox.setSpacing(5d);
		vBox.getChildren().addAll(l, hBox);

		hBox.prefWidthProperty().bind(vBox.widthProperty());
		this.getChildren().add(vBox);

		startStop.setOnAction(arg0 -> {
			if (currentStatus == StopWatchStatus.STOPPED)
			{
				pause.setDisable(false);
				currentStatus = StopWatchStatus.RUNNING;
				stopwatchWorker = new StopwatchWorker();
				pause.selectedProperty().bindBidirectional(
						stopwatchWorker.pauseProperty());
				Thread t = new Thread(stopwatchWorker);
				l.textProperty().bind(stopwatchWorker.messageProperty());
				t.setDaemon(true);
				t.start();
				return;
			}

			if (currentStatus == StopWatchStatus.RUNNING)
			{
				pause.setDisable(true);
				stopwatchWorker.stop();
				stopwatchWorker = null;
				currentStatus = StopWatchStatus.STOPPED;
			}
		});

		reset.setOnAction(arg0 -> {
			l.textProperty().unbind();
			l.setText("00:00:00.000");
			if (currentStatus == StopWatchStatus.RUNNING)
			{
				pause.setDisable(true);
				stopwatchWorker.stop();
				stopwatchWorker = null;
				currentStatus = StopWatchStatus.STOPPED;
			}
		}

		);

	}
}
