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

/*
 * Name: Kevin Tyrrell
 * Date: 7/23/2017
 */
public class Point
{
    /** Coordinates of this point. */
    private int x, y;
    
    public Point(final int x, final int y)
    {
        setX(x);
        setY(y);
    }

    /**
     * @param x - X coordinate of the point.
     */
    public void setX(final int x)
    {
        assert x >= 0;
        this.x = x;
    }

    /**
     * @param y - Y coordinate of the point.
     */
    public void setY(final int y)
    {
        assert y >= 0;
        this.y = y;
    }

    /**
     * @return - X coordinate.
     */
    public int getX()
    {
        return x;
    }

    /**
     * @return - Y coordinate.
     */
    public int getY()
    {
        return y;
    }
}
