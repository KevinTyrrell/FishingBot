package gui;

import javafx.beans.property.*;
import javafx.geometry.*;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.*;

class ConfirmationBox extends Stage
{
	private VBox layout = new VBox();
	private ReadOnlyBooleanWrapper selected = new ReadOnlyBooleanWrapper();

	public boolean isSelected()
	{
		return selected.get();
	}

	ConfirmationBox(Window test, String question)
	{
		// Core functionality of the ConfirmationBox.
		setTitle("Warning");
		initStyle(StageStyle.UTILITY);
		initModality(Modality.APPLICATION_MODAL);
		
		// VERY IMPORTANT. 
		// Without this line, initModality will not work properly with parent windows. 
		initOwner(test);		
		
		setResizable(false);
		layout.setSpacing(10);
		layout.setPadding(new Insets(10));

		createControls();

		// Add the Label and Buttons to the Confirmation Box.
		layout.getChildren().addAll(new Label(question + "\n\n\n"), createControls());

		java.awt.Toolkit.getDefaultToolkit().beep();
		setScene(new Scene(layout));
		sizeToScene();  // workaround because utility stages aren't automatically sized correctly to their scene.
	}

	private HBox createControls()
	{
		final Button ok = new Button("OK");
		ok.setOnAction(e -> {
			selected.set(true);
			close();
		});

		final Button cancel = new Button("Cancel");
		cancel.setOnAction(e -> {
			selected.set(false);
			close();
		});

		final HBox controls = new HBox(10, ok, cancel);
		controls.setAlignment(Pos.CENTER_RIGHT);

		return controls;
	}
}