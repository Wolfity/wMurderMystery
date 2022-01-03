package me.wolf.wmurdermystery.role;

import me.wolf.wmurdermystery.utils.Utils;

public enum Role {

    UNASSIGNED("unassigned", Utils.colorize("unassigned")),
    INNOCENT("Innocent", Utils.colorize("&aInnocent")),
    DETECTIVE("Detective", Utils.colorize("&bDetective")),
    MURDERER("Murderer", Utils.colorize("&cMurderer"));

    private final String name, display;


    Role(final String name, final String display) {
        this.name = name;
        this.display = display;

    }


    public String getName() {
        return name;
    }

    public String getDisplay() {
        return display;
    }
}
