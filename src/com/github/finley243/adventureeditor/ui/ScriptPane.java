package com.github.finley243.adventureeditor.ui;

import com.github.finley243.adventureengine.script.*;
import com.github.finley243.adventureengine.script.nodes.ASTFile;
import com.github.finley243.adventureengine.script.nodes.ASTNode;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ScriptPane extends JTextPane {

    private boolean isAddingIndentation;
    private final ScriptLexer lexer;
    private final ScriptASTParser parser;
    private final ScriptValidator validator;

    private List<CompileError> activeErrors;

    public ScriptPane() {
        super();
        this.lexer = new ScriptLexer();
        this.parser = new ScriptASTParser();
        this.validator = new ScriptValidator();
        this.setBackground(Color.decode("#1E1E1E"));
        this.setCaretColor(Color.decode("#D4D4D4"));
        this.setSelectionColor(Color.decode("#264F78"));
        this.setSelectedTextColor(Color.decode("#D4D4D4"));
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
                    int caretPos = ScriptPane.this.getCaretPosition();
                    int lineStart = Utilities.getRowStart(ScriptPane.this, caretPos);

                    // Calculate the number of spaces to the left of the caret position
                    int spaceCount = 0;
                    for (int i = lineStart; i < caretPos; i++) {
                        if (getDocument().getText(i, 1).charAt(0) == ' ') {
                            spaceCount++;
                        } else {
                            break;
                        }
                    }

                    // Determine the number of spaces to add to reach the next multiple of 4
                    int spacesToAdd;
                    if (spaceCount % 4 == 0) {
                        spacesToAdd = 4;
                    } else {
                        spacesToAdd = 4 - (spaceCount % 4);
                    }

                    // Insert the spaces
                    if (spacesToAdd > 0) {
                        ScriptPane.this.getDocument().insertString(caretPos, " ".repeat(spacesToAdd), null);
                    }
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
        ToolTipManager.sharedInstance().registerComponent(this);
    }

    @Override
    protected void paintComponent(Graphics g) {
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        super.paintComponent(g2);
    }

    @Override
    public String getToolTipText(MouseEvent e) {
        int offset = viewToModel2D(e.getPoint());
        for (CompileError error : activeErrors) {
            if (offset >= error.range().start() && offset < error.range().end()) {
                return error.message();
            }
        }
        return null;
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
        //StyleConstants.setFontSize(defaultStyle, 14);
        StyleConstants.setFontFamily(defaultStyle, "Noto Sans Mono");
        StyleConstants.setForeground(defaultStyle, Color.decode("#D4D4D4"));
        Style keywordStyle = doc.addStyle("keyword", defaultStyle);
        StyleConstants.setForeground(keywordStyle, Color.decode("#569CD6"));
        //StyleConstants.setBold(keywordStyle, true);
        Style literalStyle = doc.addStyle("literal", defaultStyle);
        StyleConstants.setForeground(literalStyle, Color.decode("#569CD6"));
        //StyleConstants.setBold(literalStyle, true);
        Style stringStyle = doc.addStyle("string", defaultStyle);
        StyleConstants.setForeground(stringStyle, Color.decode("#CE9178"));
        Style numberStyle = doc.addStyle("number", defaultStyle);
        StyleConstants.setForeground(numberStyle, Color.decode("#B5CEA8"));
        Style operatorStyle = doc.addStyle("operator", defaultStyle);
        StyleConstants.setForeground(operatorStyle, Color.decode("#D4D4D4"));
        Style errorStyle = doc.addStyle("error", defaultStyle);
        StyleConstants.setForeground(errorStyle, Color.decode("#F44747"));
        StyleConstants.setUnderline(errorStyle, true);
        //StyleConstants.setBold(errorStyle, true);
        Style commentStyle = doc.addStyle("comment", defaultStyle);
        StyleConstants.setForeground(commentStyle, Color.decode("#6A9955"));
        StyleConstants.setItalic(commentStyle, true);
        Style variableStyle = doc.addStyle("variable", defaultStyle);
        StyleConstants.setForeground(variableStyle, Color.decode("#9CDCFE"));
        Style functionStyle = doc.addStyle("function", defaultStyle);
        StyleConstants.setForeground(functionStyle, Color.decode("#DCDCAA"));
        Style functionDefStyle = doc.addStyle("functionDef", defaultStyle);
        StyleConstants.setForeground(functionDefStyle, Color.decode("#DCDCAA"));
        Style parameterDefStyle = doc.addStyle("parameterDef", defaultStyle);
        StyleConstants.setForeground(parameterDefStyle, Color.decode("#9CDCFE"));
        StyleConstants.setItalic(parameterDefStyle, true);
        Style namedParamStyle = doc.addStyle("namedParam", defaultStyle);
        StyleConstants.setForeground(namedParamStyle, Color.decode("#9CDCFE"));
        StyleConstants.setItalic(namedParamStyle, true);
        Style paramAssignStyle = doc.addStyle("paramAssign", defaultStyle);
        StyleConstants.setForeground(paramAssignStyle, Color.decode("#A9B7C6"));
        Style memberNameStyle = doc.addStyle("memberName", defaultStyle);
        StyleConstants.setForeground(memberNameStyle, Color.decode("#9CDCFE"));

        doc.setCharacterAttributes(0, doc.getLength(), defaultStyle, true);

        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        List<CompileError> errors = new ArrayList<>();
        List<ScriptToken> tokens = lexer.parseToTokens(text, "TEST", errors);
        ASTFile ast = (ASTFile) parser.parse(tokens, errors);
        validator.validate(List.of(ast), errors, Set.of());

        List<HighlightData> highlightData = ast.highlightData();
        highlightData.addAll(lexer.getCommentHighlightData(text, "TEST"));

        this.activeErrors = errors;

        for (HighlightData highlight : highlightData) {
            Style highlightStyle = switch (highlight.type()) {
                case KEYWORD -> keywordStyle;
                case OPERATOR -> operatorStyle;
                case COMMENT -> commentStyle;
                case STRING -> stringStyle;
                case NUMBER -> numberStyle;
                case BOOLEAN, NULL -> literalStyle;
                case VARIABLE -> variableStyle;
                case FUNCTION_CALL -> functionStyle;
                case FUNCTION_DEFINITION_NAME -> functionDefStyle;
                case PARAMETER_DEFINITION -> parameterDefStyle;
                case PARAMETER_ASSIGNMENT -> paramAssignStyle;
                case NAMED_PARAMETER_REFERENCE -> namedParamStyle;
                case MEMBER_NAME -> memberNameStyle;
            };
            doc.setCharacterAttributes(highlight.range().start(), highlight.range().end() - highlight.range().start(), highlightStyle, true);
        }
        for (CompileError error : errors) {
            doc.setCharacterAttributes(error.range().start(), error.range().end() - error.range().start(), errorStyle, true);
        }
    }

    private void highlightStringInstances(String targetString) {
        StyledDocument doc = this.getStyledDocument();
        Style defaultStyle = StyleContext.getDefaultStyleContext().getStyle(StyleContext.DEFAULT_STYLE);
        Style highlightStyle = doc.addStyle("highlight", defaultStyle);
        StyleConstants.setBackground(highlightStyle, Color.decode("#EEEE22"));

        String text;
        try {
            text = doc.getText(0, doc.getLength());
        } catch (BadLocationException e) {
            throw new RuntimeException(e);
        }

        int index = text.indexOf(targetString);
        while (index >= 0) {
            doc.setCharacterAttributes(index, targetString.length(), highlightStyle, true);
            index = text.indexOf(targetString, index + targetString.length());
        }
    }

}
