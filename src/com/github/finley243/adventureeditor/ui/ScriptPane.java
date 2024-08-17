package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptPane extends JTextPane {

    private static final Set<String> KEYWORDS = Set.of(
            "if", "else", "while", "for", "return", "break", "continue", "func", "var"
    );

    private boolean isAddingIndentation;

    public ScriptPane() {
        super();
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                queueIndentation(e);
                queueHighlightUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                queueHighlightUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "tabInsert");
        this.getActionMap().put("tabInsert", new AbstractAction("tabInsert"){
            public void actionPerformed(ActionEvent e){
                try {
                    ScriptPane.this.getDocument().insertString(ScriptPane.this.getCaretPosition(), "    ", null);
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
        this.getInputMap().put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, InputEvent.SHIFT_DOWN_MASK), "tabDelete");
        this.getActionMap().put("tabDelete", new AbstractAction("tabDelete"){
            public void actionPerformed(ActionEvent e){
                try {
                    int caretPos = getCaretPosition();
                    int lineStart = Utilities.getRowStart(ScriptPane.this, caretPos);

                    // Calculate the number of spaces to the left of the caret position
                    int spaceCount = 0;
                    for (int i = caretPos - 1; i >= lineStart; i--) {
                        if (getDocument().getText(i, 1).charAt(0) == ' ') {
                            spaceCount++;
                        } else {
                            break;
                        }
                    }

                    // Determine the number of spaces to delete
                    int spacesToDelete;
                    if (spaceCount == 0) {
                        spacesToDelete = 0;
                    } else if (spaceCount % 4 == 0) {
                        spacesToDelete = 4;
                    } else {
                        spacesToDelete = spaceCount % 4;
                    }

                    // Delete the spaces
                    if (spacesToDelete > 0) {
                        getDocument().remove(caretPos - spacesToDelete, spacesToDelete);
                    }
                } catch (BadLocationException ex) {
                    ex.printStackTrace();
                }
            }
        });
    }

    public void queueIndentation(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> handleIndentation(e));
    }

    private void handleIndentation(DocumentEvent e) {
        if (isAddingIndentation) return;
        try {
            isAddingIndentation = true;
            int offset = e.getOffset();
            if (offset == 0 || getDocument().getText(offset, 1).charAt(0) != '\n') {
                return;
            }

            int lineStart = Utilities.getRowStart(this, offset - 1);
            String previousLine = getDocument().getText(lineStart, offset - lineStart);

            String indent = getLeadingWhitespace(previousLine);
            if (getDocument().getText(offset - 1, 1).equals("{")) {
                indent += "    ";
            }

            getDocument().insertString(offset + 1, indent, null);
        } catch (BadLocationException ex) {
            ex.printStackTrace();
        } finally {
            SwingUtilities.invokeLater(() -> isAddingIndentation = false);
        }
    }

    private String getLeadingWhitespace(String str) {
        int len = str.length();
        int whiteSpaceCount = 0;
        while (whiteSpaceCount < len && Character.isWhitespace(str.charAt(whiteSpaceCount)) && str.charAt(whiteSpaceCount) != '\n') {
            whiteSpaceCount++;
        }
        return str.substring(0, whiteSpaceCount);
    }

    private void queueHighlightUpdate() {
        SwingUtilities.invokeLater(this::updateHighlights);
    }

    private void updateHighlights() {
        StyledDocument doc = this.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        StyleConstants.setFontSize(defaultStyle, 12);
        StyleConstants.setFontFamily(defaultStyle, "Monospaced");
        Style keywordStyle = doc.addStyle("keyword", defaultStyle);
        StyleConstants.setForeground(keywordStyle, Color.decode("#000080"));
        StyleConstants.setBold(keywordStyle, true);
        Style literalStyle = doc.addStyle("literal", defaultStyle);
        StyleConstants.setForeground(literalStyle, Color.decode("#800080"));
        StyleConstants.setBold(literalStyle, true);
        Style stringStyle = doc.addStyle("string", defaultStyle);
        StyleConstants.setForeground(stringStyle, Color.decode("#008000"));
        Style numberStyle = doc.addStyle("number", defaultStyle);
        StyleConstants.setForeground(numberStyle, Color.decode("#008080"));
        Style operatorStyle = doc.addStyle("operator", defaultStyle);
        StyleConstants.setForeground(operatorStyle, Color.decode("#FF8C00"));
        Style errorStyle = doc.addStyle("error", defaultStyle);
        StyleConstants.setForeground(errorStyle, Color.decode("#DC143C"));
        StyleConstants.setBold(errorStyle, true);
        Style commentStyle = doc.addStyle("comment", defaultStyle);
        StyleConstants.setForeground(commentStyle, Color.decode("#808080"));

        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        String combinedPattern = getString();
        Pattern pattern = Pattern.compile(combinedPattern);
        Matcher matcher = pattern.matcher(text);

        while (matcher.find()) {
            Style matchStyle;
            if (matcher.group(1) != null) {
                matchStyle = keywordStyle;
            } else if (matcher.group(2) != null) {
                matchStyle = literalStyle;
            } else if (matcher.group(3) != null) {
                matchStyle = stringStyle;
            } else if (matcher.group(4) != null) {
                matchStyle = numberStyle;
            } else if (matcher.group(5) != null) {
                matchStyle = operatorStyle;
            } else if (matcher.group(6) != null) {
                matchStyle = errorStyle;
            } else if (matcher.group(7) != null) {
                matchStyle = commentStyle;
            } else {
                matchStyle = defaultStyle;
            }
            doc.setCharacterAttributes(matcher.start(), matcher.end() - matcher.start(), matchStyle, true);
        }
    }

    private static String getString() {
        String keywordPattern = "\\b(?:" + String.join("|", KEYWORDS) + ")\\b";
        String literalPattern = "\\btrue\\b|\\bfalse\\b|\\bnull\\b";
        String stringPattern = "\"[^\"]*\"|'[^']*'";
        String numberPattern = "\\b\\d+\\.\\d+f?\\b|\\b\\d+\\b";
        String operatorPattern = "\\+|-|\\*|/|%|==|!=|<|>|<=|>=|&&|\\|\\||!|\\?|:|\\+=|-=|\\*=|/=|%=|=|&|\\||\\^";
        String errorPattern = "\\berror\\b";
        String commentPattern = "//[^\n]*|/\\*(?:.|\\R)*?(?:\\*/|$)";

        return String.format("(%s)|(%s)|(%s)|(%s)|(%s)|(%s)|(%s)", keywordPattern, literalPattern, stringPattern, numberPattern, operatorPattern, errorPattern, commentPattern);
    }

}
