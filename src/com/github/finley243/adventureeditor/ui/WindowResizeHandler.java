package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class WindowResizeHandler extends MouseAdapter {
    private static final int BORDER = 6;
    private final Window window;
    private Point dragStart;
    private Rectangle startBounds;
    private int resizeDir;

    private static final int NONE = 0, N = 1, S = 2, W = 4, E = 8,
            NW = N|W, NE = N|E, SW = S|W, SE = S|E;

    public WindowResizeHandler(Window window) {
        this.window = window;
    }

    /*public void install() {
        window.addMouseListener(this);
        window.addMouseMotionListener(this);
    }*/

    public void install() {
        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (!(event instanceof MouseEvent e)) return;
            Component source = (Component) e.getSource();
            if (!SwingUtilities.isDescendingFrom(source, window) && source != window) return;
            switch (e.getID()) {
                case MouseEvent.MOUSE_MOVED -> mouseMoved(e);
                case MouseEvent.MOUSE_PRESSED -> mousePressed(e);
                case MouseEvent.MOUSE_DRAGGED -> mouseDragged(e);
                case MouseEvent.MOUSE_RELEASED -> mouseReleased(e);
            }
        }, AWTEvent.MOUSE_EVENT_MASK | AWTEvent.MOUSE_MOTION_EVENT_MASK);
    }

    private int getDirection(MouseEvent e) {
        Point screenPos = e.getLocationOnScreen();
        Rectangle bounds = window.getBounds();
        int x = screenPos.x - bounds.x;
        int y = screenPos.y - bounds.y;
        int w = bounds.width;
        int h = bounds.height;
        int dir = NONE;
        if (x <= BORDER) dir |= W;
        else if (x >= w - BORDER) dir |= E;
        if (y <= BORDER) dir |= N;
        else if (y >= h - BORDER) dir |= S;
        return dir;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        int dir = getDirection(e);
        Cursor cursor = switch (dir) {
            case N, S -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case W, E -> Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            case NW, SE -> Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            case NE, SW -> Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            default -> Cursor.getDefaultCursor();
        };
        window.setCursor(cursor);
    }

    @Override
    public void mousePressed(MouseEvent e) {
        resizeDir = getDirection(e);
        if (resizeDir != NONE) {
            dragStart = e.getLocationOnScreen();
            startBounds = window.getBounds();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        if (resizeDir == NONE || dragStart == null) return;
        Point cur = e.getLocationOnScreen();
        int dx = cur.x - dragStart.x;
        int dy = cur.y - dragStart.y;
        Rectangle b = new Rectangle(startBounds);
        if ((resizeDir & W) != 0) { b.x += dx; b.width -= dx; }
        if ((resizeDir & E) != 0) { b.width += dx; }
        if ((resizeDir & N) != 0) { b.y += dy; b.height -= dy; }
        if ((resizeDir & S) != 0) { b.height += dy; }
        Dimension min = window.getMinimumSize();
        if (b.width < min.width) {
            if ((resizeDir & W) != 0) b.x = startBounds.x + startBounds.width - min.width;
            b.width = min.width;
        }
        if (b.height < min.height) {
            if ((resizeDir & N) != 0) b.y = startBounds.y + startBounds.height - min.height;
            b.height = min.height;
        }
        window.setBounds(b);
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        resizeDir = NONE;
        dragStart = null;
        startBounds = null;
    }
}
