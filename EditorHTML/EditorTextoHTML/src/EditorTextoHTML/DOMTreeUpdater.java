/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package EditorTextoHTML;

/**
 *
 * @author edrei
 */
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.JTree;

public class DOMTreeUpdater {

    private JTree domTree;

    public DOMTreeUpdater(JTree domTree) {
        this.domTree = domTree;
    }

    public void updateTree(String html) {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("html");
        DefaultMutableTreeNode currentParent = root;
        DefaultTreeModel treeModel = new DefaultTreeModel(root);
        domTree.setModel(treeModel);

        int startIndex = html.indexOf('<');
        while (startIndex >= 0) {
            int endIndex = html.indexOf('>', startIndex);
            if (endIndex >= 0) {
                String tag = html.substring(startIndex + 1, endIndex);
                if (tag.startsWith("/")) {
                    currentParent = (DefaultMutableTreeNode) currentParent.getParent();
                } else {
                    DefaultMutableTreeNode node = new DefaultMutableTreeNode(tag);
                    treeModel.insertNodeInto(node, currentParent, currentParent.getChildCount());
                    currentParent = node;
                }
                startIndex = html.indexOf('<', endIndex);
            } else {
                break;
            }
        }
    }
}
