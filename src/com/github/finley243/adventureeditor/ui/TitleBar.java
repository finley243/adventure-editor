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
            JButton minimize = createTitleBarButton();
            minimize.setIcon(new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(ThemeManager.current().foreground());
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    //g2.drawLine(x + 2, y + 8, x + 14, y + 8);
                    g2.drawLine(x + 2, y + 12, x + 14, y + 12);
                    g2.dispose();
                }
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
            });
            JButton maximize = createTitleBarButton();
            maximize.setIcon(new Icon() {
                public void paintIcon(Component c, Graphics g, int x, int y) {
                    Graphics2D g2 = (Graphics2D) g.create();
                    g2.setColor(ThemeManager.current().foreground());
                    g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                    if (frame.getExtendedState() == Frame.MAXIMIZED_BOTH) {
                        // two lines peeking out behind (top and right edges of back square)
                        g2.drawLine(x + 5, y + 2, x + 14, y + 2);
                        g2.drawLine(x + 14, y + 2, x + 14, y + 11);
                        // front square
                        g2.drawRect(x + 2, y + 4, 10, 10);
                    } else {
                        g2.drawRect(x + 3, y + 3, 10, 10);
                    }
                    g2.dispose();
                }
                public int getIconWidth() { return 16; }
                public int getIconHeight() { return 16; }
            });
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
        JButton close = createTitleBarButton();
        close.setIcon(new Icon() {
            public void paintIcon(Component c, Graphics g, int x, int y) {
                Graphics2D g2 = (Graphics2D) g.create();
                g2.setColor(ThemeManager.current().foreground());
                g2.setStroke(new BasicStroke(1.0f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_ROUND));
                g2.drawLine(x + 3, y + 3, x + 13, y + 13);
                g2.drawLine(x + 13, y + 3, x + 3, y + 13);
                g2.dispose();
            }
            public int getIconWidth() { return 16; }
            public int getIconHeight() { return 16; }
        });
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

    private JButton createTitleBarButton() {
        JButton button = new JButton();
        button.setPreferredSize(new Dimension(24, 24));
        button.setMargin(new Insets(0, 0, 0, 0));
        return button;
    }
}
