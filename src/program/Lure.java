package program;

/**
 * Enum to represent the constants of the different lures in-game.
 * A lure has a name, a duration, and an image that corresponds to it.
 */
public enum Lure
{
    SHINY_BAUBLE("Shiny Bauble", 10, "orb.png"),
    NIGHTCRAWLERS("Nightcrawlers", 10, "worm.png"),
    AQUADYNAMIC_FISH_LENS("Aquadynamic Fish Lens", 10, "spyglass.png"),
    BRIGHT_BAUBLES("Bright Baubles", 10, "gems.png"),
    FLESH_EATING_WORM("Flesh Eating Worm", 10, "worm.png"),
    AQUADYNAMIC_FISH_ATTRACTOR("Aquadynamic Fish Attractor", 5, "lollipop.png");

    private String name, image;
    private int duration;

    Lure(String name, int duration, String image)
    {
        this.name = name;
        this.duration = duration;
        this.image = image;
    }

    public String getName() { return name; }

    public int getDuration() { return duration; }

    public String getImage() { return image; }
}
