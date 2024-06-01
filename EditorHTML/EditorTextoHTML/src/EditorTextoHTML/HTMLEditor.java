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
import javax.swing.tree.TreeSelectionModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.print.PrinterException;
import java.io.*;
import java.util.Arrays;
import java.util.List;

public class HTMLEditor extends JFrame implements ActionListener {

    private JTextPane textPane;
    private JTextArea lineNumbersTextArea;
    private JFileChooser fileChooser;
    private JTree domTree;
    private List<String> reservedWords = Arrays.asList(
            "html", "head", "body", "title", "div", "p", "span", "a", "img", "table",
            "tr", "td", "ul", "ol", "li", "form", "input", "button", "h1", "h2", "h3",
            "h4", "h5", "h6"
            );

    public HTMLEditor() {
        setTitle("HTML Editor");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

        // Crear el área de texto y configurar el resaltado de sintaxis y el listener de etiquetas de cierre
        textPane = new JTextPane();
        textPane.setFont(new Font("Monospaced", Font.PLAIN, 13));
        textPane.setBackground(Color.gray);
        textPane.getDocument().addDocumentListener(new SyntaxHighlighter(textPane, reservedWords));


        // Crear el árbol del DOM
        domTree = new JTree();
        domTree.setFont(new Font("Monospaced", Font.PLAIN, 13));
        domTree.getSelectionModel().setSelectionMode(TreeSelectionModel.DISCONTIGUOUS_TREE_SELECTION);

        // Añadir el listener de etiquetas de cierre pasando el JTree
        textPane.addKeyListener(new KeyClosingTagListener(textPane, domTree));

        // Crear el área de texto para los números de línea
        lineNumbersTextArea = new JTextArea("1");
        lineNumbersTextArea.setBackground(Color.black);
        lineNumbersTextArea.setForeground(Color.WHITE);
        lineNumbersTextArea.setEditable(false);
        lineNumbersTextArea.setFont(new Font("Monospaced", Font.PLAIN, 13));

        // Crear el JScrollPane y establecer su tamaño preferido
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setRowHeaderView(lineNumbersTextArea);

        // Crear el JScrollPane para el árbol del DOM
        JScrollPane treeScrollPane = new JScrollPane(domTree);
        treeScrollPane.setPreferredSize(new Dimension(200, 0));

        // Crear el menú y los elementos
        JMenuBar menuBar = new JMenuBar();
        JMenu fileMenu = new JMenu("Archivo");
        String[] menuItems = {"Nuevo", "Abrir", "Guardar", "Guardar Como", "Buscar", "Imprimir", "Salir"};
        for (String item : menuItems) {
            JMenuItem menuItem = new JMenuItem(item);
            menuItem.addActionListener(this);
            fileMenu.add(menuItem);
            if (item.equals("Salir")) {
                fileMenu.addSeparator();
            }
        }
        menuBar.add(fileMenu);

        // Obtener el contenedor principal y configurar la interfaz
        Container container = getContentPane();
        container.setLayout(new BorderLayout());
        container.add(scrollPane, BorderLayout.CENTER);
        container.add(treeScrollPane, BorderLayout.EAST);
        setJMenuBar(menuBar);

        setVisible(true);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(HTMLEditor::new);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String command = e.getActionCommand();
        switch (command) {
            case "Nuevo":
                nuevoDocumento();
                break;
            case "Abrir":
                abrirDocumento();
                break;
            case "Guardar":
                guardarDocumento();
                break;
            case "Guardar Como":
                guardarDocumentoComo();
                break;
            case "Buscar":
                buscarTexto();
                break;
            case "Imprimir":
                imprimirDocumento();
                break;
            case "Salir":
                salir();
                break;
        }
    }

    // Métodos para cada acción del menú
    private void nuevoDocumento() {
        textPane.setText("");
        updateDOMTree();
    }

    private void abrirDocumento() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        int result = fileChooser.showOpenDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                StringBuilder sb = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    sb.append(line).append("\n");
                }
                textPane.setText(sb.toString());
                updateDOMTree();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void guardarDocumento() {
        guardarDocumentoComo();
    }

    private void guardarDocumentoComo() {
        if (fileChooser == null) {
            fileChooser = new JFileChooser();
        }
        int result = fileChooser.showSaveDialog(this);
        if (result == JFileChooser.APPROVE_OPTION) {
            File file = fileChooser.getSelectedFile();
            try (BufferedWriter writer = new BufferedWriter(new FileWriter(file))) {
                writer.write(textPane.getText());
                updateDOMTree();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void imprimirDocumento() {
        try {
            textPane.print();
        } catch (PrinterException e) {
            e.printStackTrace();
        }
    }

    private void salir() {
        System.exit(0);
    }

    private void updateDOMTree() {
        String html = textPane.getText();
        new DOMTreeUpdater(domTree).updateTree(html);
    }

    private void buscarTexto() {
        String searchTerm = JOptionPane.showInputDialog(this, "Ingrese el texto a buscar:");
        if (searchTerm != null && !searchTerm.isEmpty()) {
            new TextSearcher(textPane).buscarEnTexto(searchTerm);
        }
    }
}
