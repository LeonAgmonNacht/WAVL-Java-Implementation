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

            tree.insert(49, "49");
            tree.insert(78, "78");
            tree.insert(65, "65");
            tree.insert(85, "85");

            tree.insert(13, "13");
            TreePrint p = new TreePrint();
            p.printNode(tree.getRoot());

            tree.insert(25, "25");
            tree.insert(89, "89");

            tree.insert(8, "8");

        }

    }

    static class TreePrint {

        public <T extends Comparable<?>> void printNode(
                WAVLTree.IWAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternal(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternal(
                List<WAVLTree.IWAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.IWAVLNode> newNodes = new ArrayList<WAVLTree.IWAVLNode>();
            for (WAVLTree.IWAVLNode node : list) {
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
                WAVLTree.IWAVLNode root) {
            int maxLevel = maxLevel(root);

            printNodeInternalRank(Collections.singletonList(root), 1, maxLevel);
        }

        private <T extends Comparable<?>> void printNodeInternalRank(
                List<WAVLTree.IWAVLNode> list, int level, int maxLevel) {
            if (list.isEmpty() || isAllElementsNull(list))
                return;

            int floor = maxLevel - level;
            int endgeLines = (int) Math.pow(2, (Math.max(floor - 1, 0)));
            int firstSpaces = (int) Math.pow(2, (floor)) - 1;
            int betweenSpaces = (int) Math.pow(2, (floor + 1)) - 1;

            printWhitespaces(firstSpaces);

            List<WAVLTree.IWAVLNode> newNodes = new ArrayList<WAVLTree.IWAVLNode>();
            for (WAVLTree.IWAVLNode node : list) {
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

        private <T extends Comparable<?>> int maxLevel(WAVLTree.IWAVLNode root) {
            if (root == null)
                return 0;

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
