package hu.ait.android.travetalia.data;

/**
 * Created by lanle on 5/9/18.
 */

public class Country {
    private String name;
    private boolean clicked;

    public Country(String name, boolean clicked) {
        this.name = name;
        this.clicked = clicked;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isClicked() {
        return clicked;
    }

    public void setClicked(boolean clicked) {
        this.clicked = clicked;
    }
}

