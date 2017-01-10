package view;

import javafx.geometry.HPos;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Hyperlink;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.paint.Color;
import javafx.stage.Modality;
import javafx.stage.Window;
import model.Lang;
import model.Tools;

/**
 * Project Name: FishingBot
 * Author: Kevin
 * Date: Dec 23, 2016
 * Description:
 * Window to select the lure options.
 */
public final class ExtraOptionWindow extends ObtrusiveWindow
{
    /* Convenience variables. */
    final int WINDOW_WIDTH = 300, WINDOW_HEIGHT = 280;

    /* Nodes. */
    final Label lblLureCount = new Label(Lang.EN_LABEL_LURES_LEFT),
            lblQuitTime = new Label(Lang.EN_LABEL_QUIT_TIME),
            lblQuitLure = new Label(Lang.EN_LABEL_QUIT_EMPTY);
    final NumberField numLures = new NumberField(0, 999, null);
    final TimePicker tpkLogoutTime = new TimePicker();
    /*final Button btnQuit = new Button(Lang.EN_NODE_EXTRA_CLOSE);*/
    final CheckBox chkQuitLures = new CheckBox(),
            chkQuitTime = new CheckBox();
    final Hyperlink lnkContact = new Hyperlink(Lang.EN_LINK_CONTACT_TEXT),
            lnkIssue = new Hyperlink(Lang.EN_LINK_REPORT_TEXT),
            lnkDonate = new Hyperlink(Lang.EN_LINK_DONATE_TEXT);

    /* Panes. */
    final GridPane grdControls = new GridPane();
    final ColumnConstraints colOne = new ColumnConstraints(),
            colTwo = new ColumnConstraints();
    final HBox hbxHyperlinks = new HBox(lnkIssue, lnkContact);

    public ExtraOptionWindow(Window owner)
    {
        super(owner);
        initModality(Modality.NONE);

        /* Column setup. */
        colOne.setPercentWidth(80.0);
        colTwo.setPercentWidth(100.0 - colOne.getPercentWidth());
        colOne.setHalignment(HPos.CENTER);
        colTwo.setHalignment(HPos.CENTER);
        grdControls.getColumnConstraints().addAll(colOne, colTwo);

        /* Add all Nodes into the grid. */
        int row = 0;
        grdControls.addRow(row++, lblLureCount, numLures);
        grdControls.addRow(row++, lblQuitLure, chkQuitLures);
        grdControls.add(lblQuitTime, 0, row++, 2, 1);
        GridPane.setHalignment(lblQuitTime, HPos.CENTER);
        grdControls.addRow(row++, tpkLogoutTime, chkQuitTime);
        /*grdControls.add(btnQuit, 0, row++, 2, 1);
        GridPane.setHalignment(btnQuit, HPos.CENTER);*/
        grdControls.add(hbxHyperlinks, 0, row++, 2, 1);
        grdControls.add(lnkDonate, 0, row, 2, 1);
        GridPane.setHalignment(lnkDonate, HPos.CENTER);


        /* TimePicker settings. */
        tpkLogoutTime.setId("time-picker");
        tpkLogoutTime.disableProperty().bind(chkQuitTime.selectedProperty().not());

        /* Hyperlink settings. */
        lnkIssue.setOnAction(e -> Tools.openWebpage(Lang.EN_LINK_REPORT_SITE));
        lnkContact.setOnAction(e -> Tools.email(Lang.EN_LINK_CONTACT_EMAIL));
        lnkDonate.setOnAction(e -> Tools.openWebpage(Lang.EN_LINK_DONATE_SITE));

        /* Functionality for the user to close the window. */
        /*btnQuit.setOnAction(e -> close());
        addEventHandler(KeyEvent.KEY_PRESSED, e -> {
            if (e.getCode().equals(KeyCode.ESCAPE))
                close();
        });*/

        focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue)
                close();
        });

        final Scene scene = new Scene(grdControls, WINDOW_WIDTH, WINDOW_HEIGHT);
        scene.getStylesheets().add("extra.css");
        scene.setFill(Color.TRANSPARENT);
        setScene(scene);
    }

    @Override
    public void showAndWait()
    {
        /* Place the window in the middle of the previous Stage. */
        setX(getOwner().getX() + getOwner().getWidth() / 2 - WINDOW_WIDTH / 2.0);
        setY(getOwner().getY() + getOwner().getHeight() / 2 - WINDOW_HEIGHT / 2.0);
        super.showAndWait();
    }
}
