package com.github.finley243.adventureeditor.ui;

import java.awt.*;

public class SoftDarkTheme implements EditorTheme {
    public Color background()          { return Color.decode("#383838"); }
    public Color backgroundSecondary() { return Color.decode("#404040"); }
    public Color foreground()          { return Color.decode("#E0E0E0"); }
    public Color foregroundSecondary() { return Color.decode("#A0A0A0"); }
    public Color inputBackground()     { return Color.decode("#2E2E2E"); }
    public Color buttonBackground()    { return Color.decode("#4A4A4A"); }
    public Color buttonBorder()        { return Color.decode("#5E5E5E"); }
    public Color buttonHover()         { return Color.decode("#525252"); }
    public Color buttonPressed()       { return Color.decode("#3E3E3E"); }
    public Color accent()              { return Color.decode("#C8956C"); }
    public Color border()              { return Color.decode("#555555"); }
    public Color selectionBackground() { return Color.decode("#7A5040"); }
    public Color selectionForeground() { return Color.decode("#E0E0E0"); }
    public Color scrollThumb()         { return Color.decode("#5A5A5A"); }
    public Color scrollThumbHover()    { return Color.decode("#6E6E6E"); }
    public Color disabledBackground()  { return Color.decode("#424242"); }
    public Color disabledForeground()  { return Color.decode("#707070"); }
    public Color disabledBorder()      { return Color.decode("#505050"); }
}
