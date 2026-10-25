package com.vibe.client;

public enum Category {
    RENDER("Render"),
    WORLD("World"),
    PLAYER("Player");

    public final String label;
    Category(String label) { this.label = label; }
}
