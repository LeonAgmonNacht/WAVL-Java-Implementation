import WAVLCore.WAVLTree;

/**
 * Created by Leon on 06/05/2017.
 */
public class Tester {

    public static void main(String[] args) {

        WAVLTree tree = new WAVLTree();

        // Preform tests:

        tree.insert(5, "5");
        tree.insert(6, "6");
        tree.insert(7, "7");
        System.out.println("Result: ");
        System.out.println(tree.getRoot().getRight().getValue());
        System.out.println(tree.getRoot().getLeft().getValue());
        System.out.println("Root rank: " + tree.getRoot().getRank() + " subtree size: " + tree.getRoot().getSubtreeSize());


    }
}
