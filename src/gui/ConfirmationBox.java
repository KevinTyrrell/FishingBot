package gui;

import javafx.beans.property.ReadOnlyBooleanWrapper;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

class ConfirmationBox extends Stage
{
	private VBox layout = new VBox();
	private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();

	public boolean isSelected()
	{
		return selected.get();
	}

	ConfirmationBox(Window base, String message)
	{
		// Core functionality of the ConfirmationBox.
		setTitle("Warning");
		initStyle(StageStyle.UTILITY);
		initModality(Modality.APPLICATION_MODAL);
		 
		// Without this line, initModality will not work properly with parent windows. 
		initOwner(base);		
		
		setResizable(false);
		layout.setSpacing(10);
		layout.setPadding(new Insets(10));
		
		createControls();

		// Add the Label and Buttons to the Confirmation Box.
		Label text = new Label(message + "\n\n");
		text.setWrapText(true);
		text.setMaxWidth(500);
		
		layout.getChildren().addAll(text, createControls());

		java.awt.Toolkit.getDefaultToolkit().beep();
		setScene(new Scene(layout));
		
		// Utility stages aren't sized according to their scene.
		sizeToScene();  
	}

	private HBox createControls()
	{
		final Button ok = new Button("OK");
		ok.setOnAction(e -> 
		{
			selected.set(true);
			close();
		});

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> 
		{
			selected.set(false);
			close();
		});

		final HBox controls = new HBox(10, ok, cancel);
		controls.setAlignment(Pos.CENTER_RIGHT);

		return controls;
	}
}