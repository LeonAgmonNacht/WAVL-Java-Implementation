package WAVLCore;

import com.sun.javaws.exceptions.InvalidArgumentException;

/**
 *
 * WAVLTree
 *
 * An implementation of a WAVL Tree with
 * distinct integer keys and info
 *
 */
public class WAVLTree {

    /**
     * The root node of the tree, in an empty tree this is null.
     */
    private WAVLNode root = null;

    /**
     * An external leaf to be used
     */
    private final WAVLNode externalLeaf = new WAVLNode();

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     * @Complexity O(1)
     */

    public boolean empty() {
        return root == null;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     * @Complexity O(log(n)), where n is the number of nodes in the tree (calls searchNode).
     */
    public String search(int k) {

        WAVLNode node = searchNode(k);
        if (node != null) {
            return node.getValue();
        }
        else {
            return null;
        }
    }

    /**
     *
     * returns the node with key k if it exists in the tree
     * otherwise, returns null
     * @complexity O(log(n)), where n is the number of nodes in the tree (travels all the way to a node in the w.c).
     */
    public WAVLNode searchNode(int k) {
        WAVLNode currentNode = root;

        if (root == null) { return null; };

        while (currentNode.isRealNode())
            if (k == currentNode.getKey()) { // Match
                return currentNode;

            } else if (k < currentNode.getKey()) {

                if (!currentNode.getRealLeft().isRealNode()) return currentNode;
                currentNode = currentNode.getRealLeft();

            } else {

                if (!currentNode.getRealRight().isRealNode()) return currentNode;
                currentNode = currentNode.getRealRight();

            }

        return null; // No match
    }

    /**
     * public int insert(int k, String i)
     * <p>
     * inserts an item with key k and info i to the WAVL tree.
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were necessary.
     * returns -1 if an item with key k already exists in the tree.
     * @complexity O(log(n)), where n is the number of nodes in the tree (calls searchNode, insertRebalance).
     */
    public int insert(int k, String i) {

        // Create the root if the tree is empty:

        if (empty()) {
            root = new WAVLNode(k, i, null, null, null); // External leaf, no father.
            return 0;
        }

        // Find the father of the insert point:

        WAVLNode father = searchNode(k);
        int fatherKey = father.getKey();
        if (k == fatherKey) return -1; // Already exists

        // Create the new node
        WAVLNode newNode = new WAVLNode(k, i, null, null, father);

        if (k < fatherKey) {
            father.setLeft(newNode);
        } else {
            father.setRight(newNode);
        }

        father.reSetSubtreeSize();

        // Re-balance tree if needed:
        return insertRebalance(newNode);
    }

    /**
     * Re-balance the tree defined by node after an insert.
     * returns the number of re-balancing operations preformed.
     * @complexity O(log(n)), where n is the number of nodes in this (in w.c we promote until the root, we also
     * need to adjust the subtree size in O(log(n)).
     */
    private int insertRebalance(WAVLNode node) {

        if (node.getFather() == null) { // Root, no need to re-balance.
            node.reSetSubtreeSize();
            return 0;
        }

        WAVLNode father = node.getFather();

        // No need to re-balance since the new inserted node is with a legit rank:

        if(father.getRank() != node.getRank()) {
            reSetSubTreeSizeOfTree(node); // Terminal case, set subtree size all the way to root.
            return 0;
        }

        int fatherRankDif = Math.abs(father.getRankDifferenceFromLeft() - father.getRankDifferenceFromRight());

        if (fatherRankDif == 1) { // Case 1: Promote.

            father.setRank(father.getRank() + 1);
            father.reSetSubtreeSize();

            return 1 + insertRebalance(father);
        }

        else if (fatherRankDif == 2) {

            int nodeRankDif = (node.getRankDifferenceFromLeft()) - (node.getRankDifferenceFromRight());

            if ((node.isRightChild() && nodeRankDif == 1) || (node.isLeftChild() && nodeRankDif == -1)) { //Case 2: Single rotate

                if (node.isRightChild()) { // Right Child, rotate left
                    leftRotate(node);
                } else {
                    rightRotate(node);
                }

                // Update Rank, terminal case:

                father.setRank(father.getRank() - 1);

                reSetSubTreeSizeOfTree(node); // Terminal case, set subtree size all the way to root.

                return 1;

            } else { // Case 3: Double rotate.

                if (node.isRightChild()) { // Right Child, double rotate left

                    WAVLNode leftChild = node.getRealLeft();
                    doubleRotateWithLeftChild(leftChild);
                    leftChild.setRank(leftChild.getRank() + 1);

                } else { // Left child, double rotate right

                    WAVLNode rightChild = node.getRealRight();
                    doubleRotateWithRightChild(rightChild);
                    rightChild.setRank(rightChild.getRank() + 1);

                }

                // Update ranks:

                node.setRank(node.getRank() - 1);
                father.setRank(father.getRank() - 1);

                reSetSubTreeSizeOfTree(node); // Terminal case, set subtree size all the way to root.

                return 2;

            }
        }

        return -1; // Can't get here...
    }

    /**
     * Recursively resetting the subtree size starting from node going up until te root
     * @Complexity O(log(n)), where n is the number of nodes in this
     */
    private void reSetSubTreeSizeOfTree(WAVLNode node) {
        WAVLNode currentNode = node;
        while (currentNode.getKey() != root.getKey()) {
            currentNode.reSetSubtreeSize();
            currentNode = currentNode.getFather();
        }
        currentNode.reSetSubtreeSize();
    }

    /**
     * public int delete(int k)
     * <p>
     * deletes an item with key k from the binary tree, if it is there;
     * the tree must remain valid (keep its invariants).
     * returns the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * returns -1 if an item with key k was not found in the tree.
     * @complexity O(log(n)), where n is the number of nodes in the tree (calls deleteNode)
     */
    public int delete(int k) {
        WAVLNode nodeToDelete = searchNode(k);
        if (nodeToDelete == null) {
            return -1;
        } else {
            return deleteNode(nodeToDelete);
        }

    }

    /**
     * @param wavlNode the node to delete
     * @return the number of rebalancing operations, or 0 if no rebalancing operations were needed.
     * @complexity O(log(n)), where n is the number of nodes in the tree (calls postDeletionRebalancing, getSuccessor).
     */
    public int deleteNode(WAVLNode wavlNode) {

        WAVLNode wavlNodeAncestor = wavlNode.getFather();

        if (wavlNode.isLeaf()) {
            if (wavlNode.getFather() == null) // one node in tree
            {
                root = null;
                return 0;
            } else if (wavlNode.isLeftChild()) // left child
            {
                wavlNodeAncestor.setLeft(null);
            } else { //right child
                wavlNodeAncestor.setRight(null);
            }
            return postDeletionRebalancing(wavlNodeAncestor); // rebalancing from ancestor
        } else if (wavlNode.getRealLeft().isRealNode() && !wavlNode.getRealRight().isRealNode()) { // unary with left node
            replaceWith(wavlNode, wavlNode.getRealLeft());
            return postDeletionRebalancing(wavlNodeAncestor); // rebalancing from ancestor

        } else if ((!wavlNode.getRealLeft().isRealNode()) && wavlNode.getRealRight().isRealNode()) { // unary with right node
            replaceWith(wavlNode, wavlNode.getRealRight());
            return postDeletionRebalancing(wavlNodeAncestor); // rebalancing from ancestor

        } else { //binary node
            WAVLNode successor = getSuccessor(wavlNode);
            WAVLNode successorAncestor = successor.getFather();
            replaceWith(wavlNode, successor); // replace successor with node to delete and balance
            if (successorAncestor == wavlNode) {
                return postDeletionRebalancing(successor); // rebalancing from successor in case the node deleted was his ancestor
            }
            return postDeletionRebalancing(successorAncestor); // rebalancing tree from successor's ancestor
        }
    }

    /**
     * rebalance the tree after deletion from a certain point matching the ways we showed at class
     *
     * @param node the node to rebalance the wavl tree from
     * @return the number of rebalancing operations
     * @complexity O(log(n)), where n is the number of nodes in this tree. (in the w.c we will travel all the way to the root
     * preforming O(1) operations in each step).
     */
    private int postDeletionRebalancing(WAVLNode node) {

        if (node == null) { // We got all the way to the top
            return 0;
        }

        // Leaf has rank of 0, rank differences must be 1,1
        if (node.isLeaf()) {
            Integer x = postDeletionRebalancingForLeaf(node);
            if (x != null) {
                reSetSubTreeSizeOfTree(node);
                return x;
            }

        }
        // if false no further actions required.
        if (node.getRankDifferenceFromRight() <= 2 && node.getRankDifferenceFromLeft() <= 2) {
            reSetSubTreeSizeOfTree(node);
            return 0;
        }

        // Needs rebalancing.
        // Left rank difference == 3
        if (node.getRankDifferenceFromLeft() == 3) {

            if (node.getRankDifferenceFromRight() == 2) {// Demote
                node.setRank(node.getRank() - 1);
                node.reSetSubtreeSize();
                return 1 + postDeletionRebalancing(node.getFather());
            }
            if (node.getRankDifferenceFromRight() == 1) {
                WAVLNode rightChild = node.getRealRight();

                if (rightChild.getRankDifferenceFromLeft() == 2 && rightChild.getRankDifferenceFromRight() == 2) { // DoubleDemote
                    rightChild.setRank(rightChild.getRank() - 1);
                    node.setRank(node.getRank() - 1);
                    node.reSetSubtreeSize();
                    return 2 + postDeletionRebalancing(node.getFather());
                }

                if (rightChild.getRankDifferenceFromRight() == 1) { // Rotate Left
                    leftRotate(rightChild);
                    rightChild.setRank(rightChild.getRank() + 1);
                    node.setRank(node.getRank() - 1);
                    reSetSubTreeSizeOfTree(node);
                    if (node.isLeaf()) {
                        node.setRank(node.getRank() - 1);
                        return 2;
                    } else
                        return 1;

                }

                if (rightChild.getRankDifferenceFromRight() == 2) { // Double Rotate
                    WAVLNode left = rightChild.getRealLeft();

                    doubleRotateWithLeftChild(left);

                    left.setRank(left.getRank() + 2);
                    rightChild.setRank(rightChild.getRank() - 1);
                    node.setRank(node.getRank() - 2);
                    reSetSubTreeSizeOfTree(node);
                    return 2;
                }
            }
        } else { // Right rank difference == 3

            if (node.getRankDifferenceFromLeft() == 2) {// Demote
                node.setRank(node.getRank() - 1);
                node.reSetSubtreeSize();
                return 1 + postDeletionRebalancing(node.getFather());
            }
            if (node.getRankDifferenceFromLeft() == 1) {
                WAVLNode leftChild = node.getRealLeft();

                if (leftChild.getRankDifferenceFromLeft() == 2 && leftChild.getRankDifferenceFromRight() == 2) { // DoubleDemote
                    leftChild.setRank(leftChild.getRank() - 1);
                    node.setRank(node.getRank() - 1);
                    node.reSetSubtreeSize();
                    return 2 + postDeletionRebalancing(node.getFather());
                }

                if (leftChild.getRankDifferenceFromLeft() == 1) { // Rotate Right
                    rightRotate(leftChild);
                    leftChild.setRank(leftChild.getRank() + 1);
                    node.setRank(node.getRank() - 1);
                    reSetSubTreeSizeOfTree(node);
                    if (node.isLeaf()) {
                        node.setRank(node.getRank() - 1);
                        return 2;
                    } else
                        return 1;
                }

                if (leftChild.getRankDifferenceFromLeft() == 2) { // Double Rotate
                    WAVLNode right = leftChild.getRealRight();

                    doubleRotateWithRightChild(right);

                    right.setRank(right.getRank() + 2);
                    leftChild.setRank(leftChild.getRank() - 1);
                    node.setRank(node.getRank() - 2);
                    reSetSubTreeSizeOfTree(node);
                    return 2;
                }
            }
        }
        return 0;
    }

    /**
     * checking base cases for rebalancing leafs
     * @return the number of rebalancing operations needed
     * @complexity O(log(n)), where n is the number of nodes in the tree. (might call postDeletionRebalancing)
     */
    private Integer postDeletionRebalancingForLeaf(WAVLNode node) {
        if (node.getRankDifferenceFromLeft() == 2 && node.getRankDifferenceFromRight() == 2) {
            node.setRank(node.getRank() - 1);
            return 1 + postDeletionRebalancing(node.getFather());
        } else if (node.getRankDifferenceFromLeft() == 1 && node.getRankDifferenceFromRight() == 1) return 0;
        return null;
    }

    /**
     * get successor to a given node
     *
     * @param node a node to find successor to
     * @return successor to a given node, null if it doesn't have one(largest key in tree)
     * @complexity O(log(n)), where n is the number of nodes in this. might traverse all the way to root.
     */
    private WAVLNode getSuccessor(WAVLNode node) {
        if (node == null) {
            return null;
        }

        WAVLNode searchNode = node;

        if (node.getRight() != null) {// if i have a node that is my right child, the most left node in his subtree is my successor
            searchNode = node.getRealRight();
            while (searchNode.getLeft() != null) {
                searchNode = searchNode.getRealLeft();
            }
            return searchNode;
        }

        if (node.getFather() == null) // if i am the root and i didn't find successor by now, it's null
            return null;

        if (node.isLeftChild()) // if i'm left to a node he is my successor
            return node.getFather();

        while (searchNode.isRightChild()) { // going up the tree
            searchNode = node.getFather();
        }

        if (searchNode.getFather() != null)
            return searchNode.getFather();

        return null;
    }

    /**
     * private void replaceWith(WAVLNode firstNode, WAVLNode secondNode)
     * replace a node with another node
     * Gets two WAVLnodes, firstNode to replace and secondNode to replace it with.
     * the function place secondNode in place of firstNode according to the algorithm presented to us in class.
     * and from that can be derived we treat correctly only the following cases:
     * firstNode is Unary or firstNode is Binary and secondNode is either unary or a leaf.
     * @complexity O(1).
     */
    /**
     * we replace a node with other node. we use this for deleting
     *
     * @param firstNode  the node to replace(unary or binary)
     * @param secondNode the node to replace with(unary or leaf)
     */
    private void replaceWith(WAVLNode firstNode, WAVLNode secondNode) {
        WAVLNode firstNodeAncestor = firstNode.getFather();

        WAVLNode secondNodeRightBeforeSwitch = secondNode.getRealRight();
        WAVLNode secondNodeLeftBeforeSwitch = secondNode.getRealLeft();
        WAVLNode secondNodeAncestorBeforeSwitch = secondNode.getFather();

        if (firstNode.getRight() != null && firstNode.getLeft() != null) {
            secondNode.setRank(firstNode.getRank());
            if (firstNode.getRealLeft() != secondNode) {
                secondNode.setLeft(firstNode.getLeft());
                firstNode.getRealLeft().setFather(secondNode);
            }
            if (firstNode.getRealRight() != secondNode) {
                secondNode.setRight(firstNode.getRight());
                firstNode.getRealRight().setFather(secondNode);
            }

            // Detaching secondNode from it's old position attention if secondNode is firstNode's son.
            if (firstNode.getRight() != secondNode && firstNode.getLeft() != secondNode) {
                if (!secondNodeLeftBeforeSwitch.isRealNode() && !secondNodeRightBeforeSwitch.isRealNode()) {
                    if (secondNode.isLeftChild()) {
                        secondNodeAncestorBeforeSwitch.setLeft(null);
                    } else {
                        secondNodeAncestorBeforeSwitch.setRight(null);
                    }
                } else if (secondNodeLeftBeforeSwitch.isRealNode() && !secondNodeRightBeforeSwitch.isRealNode()) {
                    if (secondNode.isLeftChild()) {
                        secondNodeAncestorBeforeSwitch.setLeft(secondNodeLeftBeforeSwitch);
                        secondNodeLeftBeforeSwitch.setFather(secondNodeAncestorBeforeSwitch);
                    } else {
                        secondNodeAncestorBeforeSwitch.setRight(secondNodeLeftBeforeSwitch);
                        secondNodeLeftBeforeSwitch.setFather(secondNodeAncestorBeforeSwitch);
                    }
                } else if (!secondNodeLeftBeforeSwitch.isRealNode() && secondNodeRightBeforeSwitch.isRealNode()) {
                    if (secondNode.isLeftChild()) {
                        secondNodeAncestorBeforeSwitch.setLeft(secondNodeRightBeforeSwitch);
                        secondNodeRightBeforeSwitch.setFather(secondNodeAncestorBeforeSwitch);
                    } else {
                        secondNodeAncestorBeforeSwitch.setRight(secondNodeRightBeforeSwitch);
                        secondNodeRightBeforeSwitch.setFather(secondNodeAncestorBeforeSwitch);
                    }
                }
            }

        }

        // Attaching secondNode to it's new parent firstNode's father.
        if (firstNode.getFather() != null) {
            secondNode.setFather(firstNodeAncestor);
            if (firstNode.isRightChild())
                firstNodeAncestor.setRight(secondNode);
            else
                firstNodeAncestor.setLeft(secondNode);
        } else {
            // Special case if firstNode is root.
            this.root = secondNode;
            secondNode.setFather(null);
        }
    }

    // Helper Rotation Methods:

    /**
     * Performing a rotation with the left child of node.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void leftRotate(WAVLNode node) {

        WAVLNode father = node.getFather();
        WAVLNode grandfather = father.getFather();
        WAVLNode leftChild =  node.getRealLeft();

        if (father == root) {
            root = node;
        }

        if (grandfather != null) {
            if (grandfather.getRealRight() == father) {
                grandfather.setRight(node);
            } else {
                grandfather.setLeft(node);
            }
        }
        node.setFather(grandfather);
        node.setLeft(father);

        father.setFather(node);
        father.setRight(leftChild);

        if (leftChild.isRealNode()) {
            leftChild.setFather(father);
        }

        // Re-set subtree size:

        father.reSetSubtreeSize();
        node.reSetSubtreeSize();
        if (grandfather != null) { grandfather.reSetSubtreeSize(); }
    }


    /**
     * Performing a rotation with the right child of node.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void rightRotate(WAVLNode node) {

        WAVLNode father = node.getFather();
        WAVLNode grandfather = father.getFather();
        WAVLNode rightChild = node.getRealRight();

        if (father == root) {
            root = node;
        }

        if (grandfather != null) {
            if (grandfather.getRealRight() == father) {
                grandfather.setRight(node);
            } else {
                grandfather.setLeft(node);
            }
        }
        node.setFather(grandfather);
        node.setRight(father);

        father.setFather(node);
        father.setLeft(rightChild);

        if (rightChild.isRealNode()) {
            rightChild.setFather(father);
        }

        // Re-set subtree size:

        father.reSetSubtreeSize();
        node.reSetSubtreeSize();
        if (grandfather != null) { grandfather.reSetSubtreeSize(); }

    }

    /**
     * Performing a double rotation with left child.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void doubleRotateWithLeftChild(WAVLNode node) {
        rightRotate(node);
        leftRotate(node);
    }

    /**
     * Performing a double rotation with right child.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void doubleRotateWithRightChild(WAVLNode node) {
        leftRotate(node);
        rightRotate(node);
    }

    /**
     * public String min()
     * <p>
     * Returns the info of the item with the smallest key in the tree,
     * or null if the tree is empty
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public String min() {
        if (empty()) {
            return null;
        } else {
            WAVLNode node = root;
            while (node.getRealLeft().isRealNode()) {
                node = node.getRealLeft();
            }
            return node.getValue();
        }
    }

    /**
     * public String max()
     * <p>
     * Returns the info of the item with the largest key in the tree,
     * or null if the tree is empty
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public String max() {
        if (empty()) {
            return null;
        } else {
            WAVLNode node = root;
            while (node.getRealRight().isRealNode()) {
                node = node.getRealRight();
            }
            return node.getValue();
        }
    }

    /**
     * @return the in-order traversal of this as an array
     * @complexity O(n), where n is the number of nodes in this tree.
     */
    private WAVLNode[] inOrderTraversal() {

        if (empty()) {
            return new WAVLNode[0];
        }
        WAVLNode[] inOrder = new WAVLNode[size()];
        recursiveInOrderTraversal(inOrder, root, 0);
        return inOrder;
    }

    /**
     * Continuing the in-order traversal from a given node with a given array of pre-inserted nodes.
     */
    private int recursiveInOrderTraversal(WAVLNode[] insertedNodes, WAVLNode node, int numInsertedNodes) {
        if (!node.isRealNode()) {
            return numInsertedNodes;
        }

        numInsertedNodes = recursiveInOrderTraversal(insertedNodes, node.getRealLeft(), numInsertedNodes);
        insertedNodes[numInsertedNodes] = node;
        numInsertedNodes += 1;
        numInsertedNodes = recursiveInOrderTraversal(insertedNodes, node.getRealRight(), numInsertedNodes);

        return numInsertedNodes;
    }


    /**
     * public int[] keysToArray()
     * <p>
     * Returns a sorted array which contains all keys in the tree,
     * or an empty array if the tree is empty.
     * @complexity O(n), where n is the number of nodes in the tree. (calls inOrderTraversal).
     */
    public int[] keysToArray() {
        WAVLNode[] nodes = inOrderTraversal();
        int[] keys = new int[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            keys[i] = nodes[i].getKey();
        }
        return keys;
    }

    /**
     * public String[] infoToArray()
     * <p>
     * Returns an array which contains all info in the tree,
     * sorted by their respective keys,
     * or an empty array if the tree is empty.
     * @complexity O(n), where n is the number of nodes in the tree. (calls inOrderTraversal).
     */
    public String[] infoToArray() {
        WAVLNode[] nodes = inOrderTraversal();
        String[] info = new String[nodes.length];
        for (int i = 0; i < nodes.length; i++) {
            info[i] = nodes[i].getValue();
        }
        return info;
    }

    /**
     * public int size()
     * <p>
     * Returns the number of nodes in the tree.
     * <p>
     * precondition: none
     * postcondition: none
     */
    public int size() {
        return root.getSubtreeSize();
    }

    /**
     * public int getRoot()
     * <p>
     * Returns the root WAVL node, or null if the tree is empty
     * <p>
     * precondition: none
     * postcondition: none
     */
    public WAVLNode getRoot() {
        return root;
    }

    /**
     * public int select(int i)
     * <p>
     * Returns the value of the i'th smallest key (return -1 if tree is empty)
     * Example 1: select(1) returns the value of the node with minimal key
     * Example 2: select(size()) returns the value of the node with maximal key
     * Example 3: select(2) returns the value 2nd smallest minimal node, i.e the value of the node minimal node's successor
     * <p>
     * precondition: size() >= i > 0
     * postcondition: none
     */
    public String select(int i) {
        return recursiveSelect(root, i).getValue();
    }

    private WAVLNode recursiveSelect(WAVLNode node, int i) {
        int r = node.getRealLeft().getSubtreeSize();
        if (i == r) {
            return node;
        } else if (i < r) {
            return recursiveSelect(node.getRealLeft(), i);
        } else {
            return recursiveSelect(node.getRealRight(), i - r - 1);
        }
    }

    /**
     * public interface IWAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IWAVLNode{
        public int getKey(); //returns node's key (for virtuval node return -1)
        public String getValue(); //returns node's value [info] (for virtuval node return null)
        public IWAVLNode getLeft(); //returns left child (if there is no left child return null)
        public IWAVLNode getRight(); //returns right child (if there is no right child return null)
        public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
        public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))
    }

    /**
     * public class WAVLNode
     * <p>
     * If you wish to implement classes other than WAVLTree
     * (for example WAVLNode), do it in this file, not in
     * another file.
     * This class can and must be modified.
     * (It must implement IWAVLNode)
     */

    public class WAVLNode implements IWAVLNode {

        /**
         * The right child of this if exists
         */
        private WAVLNode rightChild;
        /**
         * The left child of this if exists
         */
        private WAVLNode leftChild;
        /**
         * The info (String) of this
         */
        private String info;
        /**
         * The key (Integer) of this
         */
        private Integer key;
        /**
         * The number of nodes in the tree defined by this as its root.
         */
        private Integer subTreeSize;
        /**
         * The rank of this
         */
        private Integer rank;

        /**
         * The father of this
         */
        private WAVLNode father;
        /**
         * For external leafs only.
         */
        private WAVLNode() {
            this.rank = -1;
            this.subTreeSize = 0;
        }

        public WAVLNode(Integer key, String info, WAVLNode rightChild, WAVLNode leftChild, WAVLNode father) {

            if (key == null) {
                throw new IllegalArgumentException("Key cloud not be null");
            }

            if (rightChild == null) {
                this.rightChild = externalLeaf;
            } else {
                this.rightChild = rightChild;
            }
            if (leftChild == null) {
                this.leftChild = externalLeaf;
            } else {
                this.leftChild = leftChild;
            }

            this.info = info;
            this.key = key;
            reSetSubtreeSize();
            this.rank = max(this.rightChild.getRank(), this.leftChild.getRank()) + 1;
            this.father = father;
        }

        public int getKey() {
            if (isRealNode()) {
                return this.key;
            } else {
                return -1;
            }
        }

        public String getValue() {
            if (isRealNode()) {
                return this.info;
            } else {
                return null;
            }
        }

        /**
         * get the real right node, not null for external but the static external node
         *
         * @return the real right node, not null for external but the static external node
         */
        public WAVLNode getRealRight() {
            return this.rightChild;
        }

        /**
         * get the real left node, not null for external but the static external node
         *
         * @return the real left node, not null for external but the static external node
         */
        public WAVLNode getRealLeft() {
            return this.leftChild;
        }


        public WAVLNode getLeft() {
            return this.getRealLeft() == externalLeaf ? null : this.getRealLeft();
        }

        public WAVLNode getRight() {
            return this.getRealRight() == externalLeaf ? null : this.getRealRight();
        }

        /**
         * @return True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)
         */
        public boolean isRealNode() {
            return this.key != null;
        }

        public int getSubtreeSize() {
            return subTreeSize;
        }

        public int getRank() {
            return this.rank;
        }

        public void setRight(WAVLNode rightChild) {

            if (rightChild == null) {
                this.rightChild = externalLeaf;
            }
            else {
                this.rightChild = rightChild;
            }
        }

        public void setLeft(WAVLNode leftChild) {
            if (leftChild == null) {
                this.leftChild = externalLeaf;
            }
            else {
                this.leftChild = leftChild;
            }
        }

        public void setSubTreeSize(Integer subTreeSize) {
            this.subTreeSize = subTreeSize;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public WAVLNode getFather() {
            return this.father;
        }

        public void setFather(WAVLNode father) {
            this.father = father;
        }

        /**
         * @return boolean to tell if the node is leaf or not
         */
        public boolean isLeaf() {
            return ((getLeft() == null) && (getRight() == null));
        }

        /**
         * check if a node is a left child
         *
         * @return true if a node is a left child, false if it's not
         */
        public boolean isLeftChild() {
            return ((this.getFather() != null) && (this.getFather().getLeft() == this));
        }

        /**
         * check if node is a right child
         *
         * @return true if a node is a right child, false if it's not
         */
        public boolean isRightChild() {
            return ((this.getFather() != null) && (this.getFather().getRight() == this));
        }

        /**
         * get rank difference from right node
         *
         * @return rank difference from right node
         */
        private int getRankDifferenceFromRight() {
            return this.getRank() - this.getRealRight().getRank();
        }

        /**
         * get rank difference from left node
         *
         * @return rank  difference from left node
         */
        private int getRankDifferenceFromLeft() {
            return this.getRank() - this.getRealLeft().getRank();
        }

        /**
         * Resetting the subtree size var according to the right and the left children
         * @Complexity O(1)
         */
        public void reSetSubtreeSize() {
            this.setSubTreeSize(getRealRight().getSubtreeSize() + getRealLeft().getSubtreeSize() + 1);
        }

        /**
         * @return the bigger between x and y
         */
        private int max(int x, int y) {
            if (x > y) {
                return x;
            } else {
                return y;
            }
        }

    }
}