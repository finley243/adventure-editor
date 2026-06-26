package com.github.finley243.adventureeditor.ui;

import java.awt.*;

public class SoftLightTheme implements EditorTheme {
    public Color background()          { return Color.decode("#F5F2EE"); }
    public Color backgroundSecondary() { return Color.decode("#EDE9E3"); }
    public Color foreground()          { return Color.decode("#2C2825"); }
    public Color foregroundSecondary() { return Color.decode("#7A7068"); }
    public Color inputBackground()     { return Color.decode("#FFFFFF"); }
    public Color buttonBackground()    { return Color.decode("#E6E0D8"); }
    public Color buttonBorder()        { return Color.decode("#C8BFB5"); }
    public Color buttonHover()         { return Color.decode("#D9D2C8"); }
    public Color buttonPressed()       { return Color.decode("#C9C0B5"); }
    public Color accent()              { return Color.decode("#8B5CF6"); }
    public Color border()              { return Color.decode("#D0C8BE"); }
    public Color selectionBackground() { return Color.decode("#8B5CF6"); }
    public Color selectionForeground() { return Color.decode("#FFFFFF"); }
    public Color scrollThumb()         { return Color.decode("#C0B8AE"); }
    public Color scrollThumbHover()    { return Color.decode("#A89E94"); }
    public Color disabledBackground()  { return Color.decode("#EDEAE5"); }
    public Color disabledForeground()  { return Color.decode("#ADA8A0"); }
    public Color disabledBorder()      { return Color.decode("#D8D2CA"); }
}
