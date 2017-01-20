package model;

/** Storage class for output Strings. */
public final class Lang
{
    /** Prevent instantiation of the class. */
    private Lang() { }

    /**
     * X.Y.Z.G
     * X: Stable, major releases.
     * Y: Stable, new features.
     * Z: Stable, bug fixes.
     * G: Unstable, bug fixes.
     */
    private static final String VERSION_NUMBER = "2.0.1.0";

    /** English. */
    public static final String
            CSS_LURE_GLOW = "selected-lure",
            EN_TITLE = "FishingBot ".concat(VERSION_NUMBER).concat(" by KevinTyrrell"),
            EN_NODE_CONSOLE = "Console",
            EN_NODE_START = "Start",
            EN_NODE_STOP = "Stop",
            EN_NODE_CALIBRATE = "Calibrate",
            EN_NODE_EXTRA_CLOSE = "Close",
            EN_LABEL_SELECT = "Use your mouse to draw a small rectangle around the area where your [Fishing Bobber] tooltip will appear.",
            EN_LABEL_SPEED = "Scan Speed",
            EN_LABEL_ON_TOP = "Always On Top",
            EN_LABEL_SENSITIVITY = "Splash Sensitivity",
            EN_LABEL_DEBUG = "Debug Mode",
            EN_LABEL_LURES_LEFT = "Lures Remaining:",
            EN_LABEL_QUIT_TIME = "Stop/Logout at certain time?",
            EN_LABEL_QUIT_EMPTY = "Quit at zero lures?",
            EN_TOOLTIP_START = "Starts the fishing cycle.\nFishingBot MUST be calibrated beforehand.",
            EN_TOOLTIP_STOP = "Stops the fishing cycle.",
            EN_TOOLTIP_CALIBRATE = "Configures FishingBot by locating your in-game [Fishing Bobber] tooltip.\nAfter pressing, cast fishing in-game and then hover over the bobber.\n" + "Right click to setup a user-defined search area.",
            EN_TOOLTIP_ON_TOP = "Enabling this means FishingBot will be on-top of other windows.",
            EN_TOOLTIP_DEBUG = "Enabling this allows you to view developer messages in the console.",
            EN_TOOLTIP_EXTRA_OPTIONS = "Extra Options Menu",
            EN_CAST_FISHING = "/cast Fishing",
            EN_USE = "/use ",
            EN_LOGOUT = "/logout",
            EN_GRAB_POLE = "/script PickupInventoryItem(16)",
            EN_ITEM_SHINY_BAUBLE = "Shiny Bauble",
            EN_ITEM_NIGHTCRAWLERS = "Nightcrawlers",
            EN_ITEM_AQUADYNAMIC_FISH_LENS = "Aquadynamic Fish Lens",
            EN_ITEM_BRIGHT_BAUBLES = "Bright Baubles",
            EN_ITEM_FLESH_EATING_WORM = "Flesh Eating Worm",
            EN_ITEM_AQUADYNAMIC_FISH_ATTRACTOR = "Aquadynamic Fish Attractor",
            EN_MSG_FISH_CAUGHT = "Fish caught successfully.",
            EN_MSG_FISHING_START = "Fishing will now begin. Please avoid using your mouse/keyboard.",
            EN_MSG_FISHING_END = "Fishing will now end. This may take a few seconds.",
            EN_MSG_LURE_APPLY = "Attempting to apply the following lure: ",
            EN_MSG_LURE_SET = "Lure set. Next lure cycle will now use: ",
            EN_MSG_LOGOUT_CONFIRM = "Logout condition met. Ending fishing cycle and logging out!",
            EN_MSG_CALIBRATE_START = "Hover over the Fishing Bobber in-game while the program calibrates.",
            EN_MSG_CALIBRATE_RUNNING = "Attempting to locate tooltip in: ",
            EN_MSG_CALIBRATE_FAIL = "Calibration failed. Unable to locate your Fishing Bobber tooltip.",
            EN_MSG_CALIBRATE_SUCCESS = "Calibration successful. You may now start.",
            EN_MSG_LURE_CHANGED = "Lure changed. Next lure cycle will now use: ",
            EN_MSG_LURE_REMOVE = "Lure removed. No longer attempting to use any lure.",
            EN_ERROR_BOBBER_MISSING = "Failed to locate the bobber!",
            EN_ERROR_SPLASH_MISSING = "Failed to detect the bobber's splash!",
            EN_ERROR_NOT_CALIBRATED = "You cannot start fishing until the program is calibrated!",
            EN_ERROR_ALREADY_FISHING = "You cannot start fishing while you are already fishing.",
            EN_ERROR_NOT_FISHING = "You cannot stop fishing until you begin fishing.",
            EN_EXCEPTION_ROBOT = "Failed to communicate with your mouse/keyboard via Java.Robot: ",
            EN_DEBUG_FILE_NOT_FOUND = "[DEBUG] Unable to locate the file: ",
            EN_DEBUG_IO_EXCEPTION = "[DEBUG] IO Exception encountered: ",
            EN_DEBUG_COLOR_THRESH = "[DEBUG] Scanned Value: %1, Value Needed: %2",
            EN_LINK_REPORT_TEXT = "Report Issue",
            EN_LINK_REPORT_SITE = "https://github.com/KevinTyrrell/FishingBot/issues",
            EN_LINK_CONTACT_TEXT = "Contact Author",
            EN_LINK_CONTACT_EMAIL = "kev070892@gmail.com",
            EN_LINK_DONATE_TEXT = "Donate",
            EN_LINK_DONATE_SITE = "https://paypal.me/KevinTearUl";
}
