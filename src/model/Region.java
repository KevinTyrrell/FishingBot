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

import java.util.function.Function;
import java.util.stream.IntStream;
import java.util.stream.Stream;

/*
 * Name: Kevin Tyrrell
 * Date: 7/23/2017
 */
public class Region
{
    /** The origin point of this rectangular region. */
    private Point origin;
    /** Dimensions of the region. */
    private int width, height;

    public Region(final Point origin, final int width, final int height)
    {
        setOrigin(origin);
        setWidth(width);
        setHeight(height);
    }

    /**
     * Streams the points inside this region.
     * There are infinite points inside a region, so only a small
     * subset are actually streamed. Higher hsteps and vsteps means
     * less total points inside the stream.
     * @param hstep - Amount of horizontal space between points.
     * @param vstep - Amount of vertical space between points.
     * @return - Stream of points inside the region.
     */
    public Stream<Point> stream(final int hstep, final int vstep)
    {
        assert hstep > 0;
        assert vstep > 0;
        final int originX = origin.getX(), originY = origin.getY();
        return IntStream.range(0, height / vstep)
                .mapToObj(y -> IntStream.range(0, width / hstep)
                        .mapToObj(x -> new Point(originX + x * hstep, originY + y * vstep)))
                .flatMap(Function.identity());
    }

    /**
     * Streams all points inside this region.
     * @return - Stream of all points inside the region.
     */
    public Stream<Point> stream()
    {
        return stream(1, 1);
    }

    /**
     * @return - Width of the Region.
     */
    public int getWidth()
    {
        return width;
    }

    /**
     * @param width - Width of the region.
     */
    public void setWidth(final int width)
    {
        assert width >= 0;
        this.width = width;
    }

    /**
     * @return - Height of the region.
     */
    public int getHeight()
    {
        return height;
    }

    /**
     * @param height - Height of the region.
     */
    public void setHeight(final int height)
    {
        assert height >= 0;
        this.height = height;
    }

    /**
     * @return - Origin of the region.
     */
    public Point getOrigin()
    {
        return origin;
    }

    /**
     * @param origin - Origin of the region.
     */
    public void setOrigin(final Point origin)
    {
        assert origin != null;
        this.origin = origin;
    }
}
