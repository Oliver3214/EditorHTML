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

public class TextSearcher {

    private JTextPane textPane;

    public TextSearcher(JTextPane textPane) {
        this.textPane = textPane;
    }

    public void buscarEnTexto(String searchTerm) {
        Document document = textPane.getDocument();
        int startIndex = 0;
        try {
            while (startIndex + searchTerm.length() <= document.getLength()) {
                String text = document.getText(startIndex, searchTerm.length());
                if (text.equalsIgnoreCase(searchTerm)) {
                    textPane.setCaretPosition(startIndex);
                    textPane.moveCaretPosition(startIndex + searchTerm.length());
                    textPane.requestFocusInWindow();
                    return;
                }
                startIndex++;
            }
            JOptionPane.showMessageDialog(textPane, "Texto no encontrado.");
        } catch (BadLocationException e) {
            e.printStackTrace();
        }
    }
}
