/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EditorTextoHTML;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.text.*;
import java.awt.*;
import java.util.List;

public class SyntaxHighlighter implements DocumentListener {

    private JTextPane textPane;
    private List<String> reservedWords;
    private StyleContext styleContext;
    private AttributeSet keywordStyle;
    private AttributeSet normalStyle;

    public SyntaxHighlighter(JTextPane textPane, List<String> reservedWords) {
        this.textPane = textPane;
        this.reservedWords = reservedWords;
        this.styleContext = StyleContext.getDefaultStyleContext();
        this.keywordStyle = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLUE);
        this.normalStyle = styleContext.addAttribute(styleContext.getEmptySet(), StyleConstants.Foreground, Color.BLACK);
    }

    private void applyHighlighting(Document document) throws BadLocationException {
        String text = document.getText(0, document.getLength());
        StyledDocument styledDocument = textPane.getStyledDocument();
        styledDocument.setCharacterAttributes(0, text.length(), normalStyle, true);

        for (String word : reservedWords) {
            int index = 0;
            while ((index = text.indexOf(word, index)) >= 0) {
                int endIndex = index + word.length();
                if (isWordBoundary(text, index, endIndex)) {
                    styledDocument.setCharacterAttributes(index, word.length(), keywordStyle, true);
                }
                index = endIndex;
            }
        }
        updateLineNumbers();
    }

    private void updateLineNumbers() {
        Document doc = textPane.getDocument();
        int lineCount = doc.getDefaultRootElement().getElementCount();
        StringBuilder sb = new StringBuilder();
        for (int i = 1; i <= lineCount; i++) {
            sb.append(i).append("\n");
        }
        JTextArea lineNumbersTextArea = (JTextArea) ((JScrollPane) textPane.getParent().getParent()).getRowHeader().getView();
        lineNumbersTextArea.setText(sb.toString());
    }

    private boolean isWordBoundary(String text, int startIndex, int endIndex) {
        if (startIndex == 0 && endIndex == text.length()) {
            return true;
        } else if (startIndex == 0) {
            return !Character.isLetterOrDigit(text.charAt(endIndex));
        } else if (endIndex == text.length()) {
            return !Character.isLetterOrDigit(text.charAt(startIndex - 1));
        } else {
            return !Character.isLetterOrDigit(text.charAt(startIndex - 1)) && !Character.isLetterOrDigit(text.charAt(endIndex));
        }
    }

    @Override
    public void insertUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> {
            try {
                applyHighlighting(e.getDocument());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void removeUpdate(DocumentEvent e) {
        SwingUtilities.invokeLater(() -> {
            try {
                applyHighlighting(e.getDocument());
            } catch (BadLocationException ex) {
                ex.printStackTrace();
            }
        });
    }

    @Override
    public void changedUpdate(DocumentEvent e) {
        /* No se usa para documentos de texto sin formato */ }
}
