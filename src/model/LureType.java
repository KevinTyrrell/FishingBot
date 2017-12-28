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

import localization.Lang.Locale;

/*
 * Name: Kevin Tyrrell
 * Date: 7/17/2017
 */
public enum LureType
{
    SHINY_BAUBLE(Locale.SHINY_BAUBLE, 10, "orb.png"),
    NIGHTCRAWLERS(Locale.NIGHTCRAWLERS, 10, "worm.png"),
    AQUADYNAMIC_FISH_LENS(Locale.AQUADYNAMIC_FISH_LENS, 10, "lollipop.png"),
    BRIGHT_BAUBLES(Locale.BRIGHT_BAUBLES, 10, "orb.png"),
    FLESH_EATING_WORM(Locale.FLESH_EATING_WORM, 10, "worm.png"),
    AQUADYNAMIC_FISH_ATTRACTOR(Locale.AQUADYNAMIC_FISH_ATTRACTOR, 5, "lollipop.png");
    
    private final Locale name;
    private final long duration;
    private final String image;

    LureType(final Locale name, final long duration, final String image)
    {
        assert name != null;
        assert duration >= 0;
        assert image != null;
        this.name = name;
        this.duration = duration;
        this.image = image;
    }

    public Locale getName()
    {
        return name;
    }

    public long getDuration()
    {
        return duration;
    }

    public String getImage()
    {
        return image;
    }

    @Override public String toString()
    {
        return name.get();
    }
}
