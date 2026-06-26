package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.event.WindowEvent;

public class TitleBar extends JPanel {
    private final JLabel titleLabel;
    private Point dragOffset;

    public TitleBar(Window window, String title) {
        setLayout(new BorderLayout());
        setPreferredSize(new Dimension(0, 32));
        setBackground(ThemeManager.current().backgroundSecondary());

        // title label
        this.titleLabel = new JLabel(" " + title);
        titleLabel.setForeground(ThemeManager.current().foreground());
        add(titleLabel, BorderLayout.CENTER);

        // window buttons
        JPanel buttons = new JPanel(new FlowLayout(FlowLayout.RIGHT, 4, 4));
        buttons.setOpaque(false);
        if (window instanceof JFrame frame) {
            JButton minimize = new JButton("-");
            JButton maximize = new JButton("[]");
            minimize.addActionListener(e -> frame.setState(Frame.ICONIFIED));
            maximize.addActionListener(e -> {
                if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                    frame.setExtendedState(Frame.NORMAL);
                } else {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                }
            });
            buttons.add(minimize);
            buttons.add(maximize);
        }
        JButton close = new JButton("X");
        close.addActionListener(e -> window.dispatchEvent(new WindowEvent(window, WindowEvent.WINDOW_CLOSING)));
        buttons.add(close);
        add(buttons, BorderLayout.EAST);

        // drag to move
        addMouseListener(new MouseAdapter() {
            public void mousePressed(MouseEvent e) {
                dragOffset = e.getPoint();
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            public void mouseDragged(MouseEvent e) {
                Point loc = window.getLocation();
                window.setLocation(loc.x + e.getX() - dragOffset.x,
                        loc.y + e.getY() - dragOffset.y);
            }
        });
    }

    public void setTitle(String title) {
        titleLabel.setText(" " + title);
    }
}
