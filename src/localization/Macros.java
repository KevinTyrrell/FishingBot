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

package localization;

/*
 * Name: Kevin Tyrrell
 * Date: 7/18/2017
 */
public final class Macros
{
    /** Prevent instantiation of this class. */
    private Macros() { }

    public static final String
            /* Directories. */
            DIR_CSS = "css",
            DIR_IMAGES = "images",

            /* Files. */
            FILE_GEMS = "gems.png",
            FILE_ICON = "icon.png",
            FILE_LOLLIPOP = "lollipop.png",
            FILE_MOUSE = "mouse.png",
            FILE_ORB = "orb.png",
            FILE_QUEST = "quest.gif",
            FILE_SPYGLASS = "spyglass.png",
            FILE_WORM = "worm.png",
            FILE_MAIN_STYLESHEET = "main-window.css",

            /* CSS. */
            CSS_LURE_GLOW = "selected-lure",

            /* Paths. */
            PATH_MAIN_STYLESHEET = DIR_CSS.concat("/").concat(FILE_MAIN_STYLESHEET),

            /// Not yet used.
            LINK_REPORT_SITE = "https://github.com/KevinTyrrell/FishingBot/issues",
            LINK_CONTACT_EMAIL = "kev070892@gmail.com",
            LINK_DONATE_SITE = "https://paypal.me/KevinTearUl",
            CAST_FISHING = "/cast Fishing",
            USE = "/use ",
            LOGOUT = "/logout",
            GRAB_POLE = "/script PickupInventoryItem(16)";
}
