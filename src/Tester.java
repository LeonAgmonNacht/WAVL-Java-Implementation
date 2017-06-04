import WAVLCore.WAVLTree;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Created by Leon on 06/05/2017.
 */
public class Tester {


    public void testWAVLTreeClass() {}

    public static void main(String[] args) {

        WAVLTree tree = new WAVLTree();

        boolean testClass = true; // Should we test the class functionality or the asymptotic performance

        // WAVLTree Test:

        if (testClass) {

            WAVLClassTester tester = new WAVLClassTester();
            try {
                tester.run(true);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }
        else { // Tests to submit
//            AsymptoticTests tester = new AsymptoticTests();
//            tester.run();

            TreePrint p = new TreePrint();

            tree.insert(56, "56");
            tree.insert(51, "51");
            tree.insert(27, "27");
            tree.insert(70, "70");
            tree.insert(35, "35");

            p.printNode(tree.getRoot());

            tree.delete(51);
            tree.delete(56);
            p.printNode(tree.getRoot());
            tree.delete(35);



            p.printNode(tree.getRoot());

        }

    }

    static class TreePrint {

        public <T extends Comparable<?>> void printNode(
                WAVLTree.WAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternal(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternal(
                List<WAVLTree.WAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.WAVLNode> newNodes = new ArrayList<WAVLTree.WAVLNode>();
            for (WAVLTree.WAVLNode node : list) {
                if (node != null) {
                    System.out.print(node.getKey());
                    newNodes.add(node.getLeft());
                    newNodes.add(node.getRight());
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    System.out.print(" ");
                }

                printWhitespaces(betweenSpaces);
            }
            System.out.println("");

            for (int i = 1; i <= endgeLines; i++) {
                for (int j = 0; j < list.size(); j++) {
                    printWhitespaces(firstSpaces - i);
                    if (list.get(j) == null) {
                        printWhitespaces(endgeLines + endgeLines + i
                                + 1);
                        continue;
                    }

                    if (list.get(j).getLeft() != null)
                        System.out.print("/");
                    else
                        printWhitespaces(1);

                    printWhitespaces(i + i - 1);

                    if (list.get(j).getRight() != null)
                        System.out.print("\\");
                    else
                        printWhitespaces(1);

                    printWhitespaces(endgeLines + endgeLines - i);
                }

                System.out.println("");
            }

            printNodeInternal(newNodes, level + 1, maxLevel);
        }

        public <T extends Comparable<?>> void printNodeRank(
                WAVLTree.WAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternalRank(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternalRank(
                List<WAVLTree.WAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.WAVLNode> newNodes = new ArrayList<WAVLTree.WAVLNode>();
            for (WAVLTree.WAVLNode node : list) {
                if (node != null) {
                    System.out.print(node.getRank());
                    newNodes.add(node.getLeft());
                    newNodes.add(node.getRight());
                } else {
                    newNodes.add(null);
                    newNodes.add(null);
                    System.out.print(" ");
                }

                printWhitespaces(betweenSpaces);
            }
            System.out.println("");

            for (int i = 1; i <= endgeLines; i++) {
                for (int j = 0; j < list.size(); j++) {
                    printWhitespaces(firstSpaces - i);
                    if (list.get(j) == null) {
                        printWhitespaces(endgeLines + endgeLines + i
                                + 1);
                        continue;
                    }

                    if (list.get(j).getLeft() != null)
                        System.out.print("/");
                    else
                        printWhitespaces(1);

                    printWhitespaces(i + i - 1);

                    if (list.get(j).getRight() != null)
                        System.out.print("\\");
                    else
                        printWhitespaces(1);

                    printWhitespaces(endgeLines + endgeLines - i);
                }

                System.out.println("");
            }

            printNodeInternalRank(newNodes, level + 1, maxLevel);
        }

        private void printWhitespaces(int count) {
            for (int i = 0; i < count; i++)
                System.out.print(" ");
        }

        private <T extends Comparable<?>> int maxLevel(WAVLTree.WAVLNode root) {
            if (root == null)
                return 0;
            System.out.print("Max Level: " + root.getKey() + " " + root.getRealLeft().getKey() + " ||| ");

            return Math.max(maxLevel(root.getLeft()),
                    maxLevel(root.getRight())) + 1;
        }

        private <T> boolean isAllElementsNull(List<T> list) {
            for (Object object : list) {
                if (object != null)
                    return false;
            }

            return true;
        }

    }

}
