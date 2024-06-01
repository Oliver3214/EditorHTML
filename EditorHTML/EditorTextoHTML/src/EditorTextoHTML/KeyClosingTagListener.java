/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EditorTextoHTML;

/**
 *
 * @author edrei
 */
import javax.swing.*;
import javax.swing.text.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyClosingTagListener extends KeyAdapter {

    private JTextPane textPane;
    private JTree domTree; // Añadir JTree como miembro
    private static final String[] SELF_CLOSING_TAGS = {"br", "hr", "img", "input", "link", "meta"};

    public KeyClosingTagListener(JTextPane textPane, JTree domTree) {
        this.textPane = textPane;
        this.domTree = domTree; // Inicializar JTree
    }

    @Override
    public void keyTyped(KeyEvent e) {
        if (e.getKeyChar() == '>') {
            insertClosingTag();
            new DOMTreeUpdater(domTree).updateTree(textPane.getText()); // Usar domTree aquí
        }
    }

    private void insertClosingTag() {
        int caretPosition = textPane.getCaretPosition();
        Document doc = textPane.getDocument();
        StringBuilder sb = new StringBuilder();
        try {
            sb.append(doc.getText(0, caretPosition));
            String lastOpenedTag = getLastOpenedTag(sb.toString());
            if (isSelfClosingTag(lastOpenedTag)) {
                return;
            }
            String tagName = getTagName(lastOpenedTag);
            String closingTag = "</" + tagName;
            sb.append(">").append(closingTag);
            sb.append(doc.getText(caretPosition, doc.getLength() - caretPosition));
            doc.remove(0, doc.getLength());
            doc.insertString(0, sb.toString(), null);

            int newCaretPosition = caretPosition + closingTag.length() + 1; // Ajuste de posición correcto
            if (newCaretPosition > doc.getLength()) {
                newCaretPosition = doc.getLength();
            }
            textPane.setCaretPosition(newCaretPosition);

        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }

    private String getLastOpenedTag(String text) {
        int lastIndex = text.lastIndexOf('<');
        int nextSpaceIndex = text.indexOf(' ', lastIndex);
        if (nextSpaceIndex != -1) {
            return text.substring(lastIndex + 1, nextSpaceIndex);
        } else {
            return text.substring(lastIndex + 1);
        }
    }

    private String getTagName(String tag) {
        int index = tag.indexOf('>');
        if (index != -1) {
            return tag.substring(0, index);
        } else {
            return tag;
        }
    }

    private boolean isSelfClosingTag(String tag) {
        for (String selfClosingTag : SELF_CLOSING_TAGS) {
            if (selfClosingTag.equalsIgnoreCase(tag)) {
                return true;
            }
        }
        return false;
    }
}
