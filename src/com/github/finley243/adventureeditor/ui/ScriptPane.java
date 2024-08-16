package com.github.finley243.adventureeditor.ui;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptPane extends JTextPane {

    private static final Set<String> KEYWORDS = Set.of(
            "if", "else", "while", "for", "return", "break", "continue", "func", "var"
    );

    public ScriptPane() {
        super();
        this.getDocument().addDocumentListener(new DocumentListener() {
            @Override
            public void insertUpdate(DocumentEvent e) {
                queueHighlightUpdate();
            }

            @Override
            public void removeUpdate(DocumentEvent e) {
                queueHighlightUpdate();
            }

            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
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
