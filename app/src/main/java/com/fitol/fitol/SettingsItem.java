package com.fitol.fitol;

public class SettingsItem {
    private String title;
    private int icon;

    public SettingsItem(String title, int icon) {
        this.title = title;
        this.icon = icon;
    }

    public String getTitle() {
        return title;
    }

    public int getIcon() {
        return icon;
    }
}
