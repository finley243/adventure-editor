package com.github.finley243.adventureeditor.ui;

import java.awt.*;

public class DarkTheme implements EditorTheme {
    public Color background()          { return Color.decode("#2B2B2B"); }
    public Color backgroundSecondary() { return Color.decode("#323232"); }
    public Color foreground()          { return Color.decode("#c4c4c4"); }
    public Color foregroundSecondary() { return Color.decode("#909090"); }
    public Color inputBackground()     { return Color.decode("#1E1E1E"); }
    public Color buttonBackground()    { return Color.decode("#3C3C3C"); }
    public Color buttonBorder()        { return Color.decode("#555555"); }
    public Color buttonHover()         { return Color.decode("#464646"); }
    public Color buttonPressed()       { return Color.decode("#2E2E2E"); }
    public Color accent()              { return Color.decode("#0078D4"); }
    public Color border()              { return Color.decode("#454545"); }
    public Color selectionBackground() { return Color.decode("#264F78"); }
    public Color selectionForeground() { return Color.decode("#D4D4D4"); }
    public Color scrollThumb()         { return Color.decode("#555555"); }
    public Color scrollThumbHover()    { return Color.decode("#686868"); }
    public Color disabledBackground()  { return Color.decode("#333333"); }
    public Color disabledForeground()  { return Color.decode("#666666"); }
    public Color disabledBorder()      { return Color.decode("#444444"); }
}
