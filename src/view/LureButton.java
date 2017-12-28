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

package view;


import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.property.*;
import javafx.scene.control.Tooltip;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import localization.Lang;
import localization.Macros;
import model.LureType;

public class LureButton extends ImageView
{
    /** The type of lure that this button represents. */
    private final LureType type;

    /** The LureButton that is currently selected. Null if none are selected. */
    private static final ObjectProperty<LureButton> selected = new SimpleObjectProperty<>();
    /** The type of Lure that is currently selected. */
    private static final Binding<LureType> selectedType = Bindings.createObjectBinding(() ->
            selected.get() != null ? selected.get().getType() : null, selected);

    public LureButton(final LureType type)
    {
        assert type != null;
        this.type = type;

        try
        {
            final Image image = new Image(Macros.DIR_IMAGES.concat("/").concat(type.getImage()));
            /* Scale the size of lures down depending on how many lures there are. */
            setFitHeight(Math.min(image.getHeight() * 6 /
                    LureType.values().length, image.getHeight() * 1.25f));
            setImage(image);
        }
        catch (final IllegalArgumentException e)
        {
            e.printStackTrace();
        }

        setPreserveRatio(true);

        /* Upon being pressed, select. */
        setOnMousePressed(e -> select());

        /* Tooltip. */
        final Tooltip ttpName = new Tooltip();
        ttpName.textProperty().bind(Bindings.createStringBinding(
                () -> type.getName().get(), Lang.activeLanguageProperty()));
        Tooltip.install(this, ttpName);
    }

    /**
     * Selects this LureButton.
     */
    private void select()
    {
        final LureButton previous = selected.get();

        if (previous == null)
        {
            setId(Macros.CSS_LURE_GLOW);
            selected.set(this);
        }
        else
        {
            if (previous == this)
            {
                setId(null);
                selected.set(null);
            }
            else
            {
                previous.setId(null);
                setId(Macros.CSS_LURE_GLOW);
                selected.set(this);
            }
        }
    }

    /**
     * @return - Type of this LureButton.
     */
    public LureType getType()
    {
        return type;
    }

    /**
     * @return - A binding to the active LureType.
     */
    public static Binding<LureType> getTypeBinding()
    {
        return selectedType;
    }
}
