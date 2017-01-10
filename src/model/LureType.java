package model;

/**
 * Enum to represent the constants of the different lures in-game.
 * A LureType has a name, a duration, and an image that corresponds to it.
 *
 * TODO: Add Burning Crusade and Wrath of the Lich King lures.
 */
public enum LureType
{
    SHINY_BAUBLE(Lang.EN_ITEM_SHINY_BAUBLE, 10, "orb.png"),
    NIGHTCRAWLERS(Lang.EN_ITEM_NIGHTCRAWLERS, 10, "worm.png"),
    AQUADYNAMIC_FISH_LENS(Lang.EN_ITEM_AQUADYNAMIC_FISH_LENS, 10, "spyglass.png"),
    BRIGHT_BAUBLES(Lang.EN_ITEM_BRIGHT_BAUBLES, 10, "gems.png"),
    FLESH_EATING_WORM(Lang.EN_ITEM_FLESH_EATING_WORM, 10, "worm.png"),
    AQUADYNAMIC_FISH_ATTRACTOR(Lang.EN_ITEM_AQUADYNAMIC_FISH_ATTRACTOR, 5, "lollipop.png");

    private final String name, path;
    private final long duration;

    LureType(final String name, final long duration, final String path)
    {
        this.name = name;
        this.duration = duration;
        this.path = path;
    }

    public String getName() { return name; }
    public long getDuration() { return duration; }
    public String getPath() { return path; }
}