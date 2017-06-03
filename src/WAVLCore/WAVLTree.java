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
    private IWAVLNode root = null;

    /**
     * An external leaf to be used
     */
    private final WAVLNode externalLeaf = new WAVLNode();

    /**
     * public boolean empty()
     * <p>
     * returns true if and only if the tree is empty
     */

    public boolean empty() {
        return root == null;
    }

    /**
     * public String search(int k)
     * <p>
     * returns the info of an item with key k if it exists in the tree
     * otherwise, returns null
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public String search(int k) {

        IWAVLNode node = searchNode(k);
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
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public IWAVLNode searchNode(int k) {
        IWAVLNode currentNode = root;

        if (root == null) { return null; };

        while (currentNode.isRealNode())
            if (k == currentNode.getKey()) { // Match
                return currentNode;

            } else if (k < currentNode.getKey()) {

                if (!currentNode.getLeft().isRealNode()) return currentNode;
                currentNode = currentNode.getLeft();

            } else {

                if (!currentNode.getRight().isRealNode()) return currentNode;
                currentNode = currentNode.getRight();

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
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public int insert(int k, String i) {

        // Create the root if the tree is empty:

        if (empty()) {
            root = new WAVLNode(k, i, null, null, null); // External leaf, no father.
            //System.out.println("Setting New Root");
            return 0;
        }

       // System.out.println("Insert: " + k + " with root: " + root.getKey());

        // Find the father of the insert point:

        IWAVLNode father = searchNode(k);
        int fatherKey = father.getKey();
        if (k == fatherKey) return -1; // Already exists

        // Create the new node
        IWAVLNode newNode = new WAVLNode(k, i, null, null, father);

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
     * @complexity O(log(n)), where n is the number of nodes in this
     */
    private int insertRebalance(IWAVLNode node) {

        if (node.getFather() == null) { // Root, no need to re-balance.
            node.reSetSubtreeSize();
            return 0;
        }

        IWAVLNode father = node.getFather();

        // No need to re-balance since the new inserted node is with a legit rank:

        if(father.getRank() != node.getRank()) {
            reSetSubTreeSizeOfTree(node); // Terminal case, set subtree size all the way to root.
            return 0;
        }

        int fatherRankDif = Math.abs(
                (father.getRank() - father.getLeft().getRank()) -
                (father.getRank() - father.getRight().getRank()));

//        System.out.println("FRD: " + fatherRankDif + " " + father.getKey() + " " + father.getRight().getKey() + " " + father.getLeft().getKey());
//        System.out.println("FRD: " + fatherRankDif + " " + father.getRank() + " " + father.getRight().getRank() + " " + father.getLeft().getRank());

        if (fatherRankDif == 1) { // Case 1: Promote.

            //System.out.println("Promote with: " + node.getKey());

            father.setRank(father.getRank() + 1);
            father.reSetSubtreeSize();

            return 1 + insertRebalance(father);
        }

        else if (fatherRankDif == 2) {

            int nodeRankDif =
                    (node.getRank() - node.getLeft().getRank()) - (node.getRank() - node.getRight().getRank());

            //System.out.println(nodeRankDif + " " + father.getKey() + " " + node.getKey());

            if ((node == father.getRight() && nodeRankDif == 1) || (node == father.getLeft() && nodeRankDif == -1)) { //Case 2: Single rotate

                if (node == father.getRight()) { // Right Child, rotate left
                    leftRotate(node);
                } else {
                    rightRotate(node);
                }

                // Update Rank, terminal case:

                father.setRank(father.getRank() - 1);

                reSetSubTreeSizeOfTree(node); // Terminal case, set subtree size all the way to root.

                //System.out.println("Rotate with: " + node.getKey());

                return 1;

            } else { // Case 3: Double rotate.

                if (node == father.getRight()) { // Right Child, double rotate left

                    IWAVLNode leftChild = node.getLeft();
                    doubleRotateWithLeftChild(leftChild);
                    leftChild.setRank(leftChild.getRank() + 1);

                    //System.out.println("Double rotate with: " + node.getKey());

                } else { // Left child, double rotate right

                    IWAVLNode rightChild = node.getRight();
                    doubleRotateWithRightChild(rightChild);
                    rightChild.setRank(rightChild.getRank() + 1);

                    //System.out.println("Double rotate with: " + node.getKey());

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
    private void reSetSubTreeSizeOfTree(IWAVLNode node) {
        IWAVLNode currentNode = node;
        while (currentNode.getKey() != root.getKey()) {
            currentNode.reSetSubtreeSize();
            //System.out.println("Resetting subtree size: " + currentNode.getSubtreeSize());
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
     * @complexity O(log(n)), where n is the number of nodes in the tree
     */
    public int delete(int k) {
        return 42;
    }

    // Helper Rotation Methods:

    /**
     * Performing a rotation with the left child of node.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void leftRotate(IWAVLNode node) {

        IWAVLNode father = node.getFather();
        IWAVLNode grandfather = father.getFather();
        IWAVLNode leftChild =  node.getLeft();

        if (father == root) {
            root = node;
        }

        if (grandfather != null) {
            if (grandfather.getRight() == father) {
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
    private void rightRotate(IWAVLNode node) {

        IWAVLNode father = node.getFather();
        IWAVLNode grandfather = father.getFather();
        IWAVLNode rightChild = node.getRight();

        if (father == root) {
            root = node;
        }

        if (grandfather != null) {
            if (grandfather.getRight() == father) {
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
    private void doubleRotateWithLeftChild(IWAVLNode node) {
        rightRotate(node);
        leftRotate(node);
    }

    /**
     * Performing a double rotation with right child.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private void doubleRotateWithRightChild(IWAVLNode node) {
        //System.out.println("DR: " + node.getFather().getFather().getRight().getKey());
        leftRotate(node);
        rightRotate(node);
        //System.out.println("DR: " + node.getLeft().getKey());
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
            IWAVLNode node = root;
            while (node.getLeft().isRealNode()) {
                node = node.getLeft();
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
            IWAVLNode node = root;
            while (node.getRight().isRealNode()) {
                node = node.getRight();
            }
            return node.getValue();
        }
    }

    /**
     * @return the in-order traversal of this as an array
     * @complexity O(n), where n is the number of nodes in this tree
     */
    private IWAVLNode[] inOrderTraversal() {

        if (empty()) {
            return new IWAVLNode[0];
        }
        System.out.println("Tree size: " + size());
        IWAVLNode[] inOrder = new IWAVLNode[size()];
        recursiveInOrderTraversal(inOrder, root, 0);
        return inOrder;
    }

    /**
     * Continuing the in-order traversal from a given node with a given array of pre-inserted nodes.
     */
    private int recursiveInOrderTraversal(IWAVLNode[] insertedNodes, IWAVLNode node, int numInsertedNodes) {
        if (!node.isRealNode()) {
            return numInsertedNodes;
        }
        //System.out.println(node.getKey());
        numInsertedNodes = recursiveInOrderTraversal(insertedNodes, node.getLeft(), numInsertedNodes);
        insertedNodes[numInsertedNodes] = node;
        numInsertedNodes += 1;
        numInsertedNodes = recursiveInOrderTraversal(insertedNodes, node.getRight(), numInsertedNodes);

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
        IWAVLNode[] nodes = inOrderTraversal();
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
        IWAVLNode[] nodes = inOrderTraversal();
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
    public IWAVLNode getRoot() {
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

    private IWAVLNode recursiveSelect(IWAVLNode node, int i) {
        int r = node.getLeft().getSubtreeSize();
        if (i == r) {
            return node;
        } else if (i < r) {
            return recursiveSelect(node.getLeft(), i);
        } else {
            return recursiveSelect(node.getRight(), i - r - 1);
        }
    }

    /**
     * public interface IWAVLNode
     * ! Do not delete or modify this - otherwise all tests will fail !
     */
    public interface IWAVLNode {
        public int getKey(); //returns node's key (for virtuval node return -1)

        public String getValue(); //returns node's value [info] (for virtuval node return null)

        public IWAVLNode getLeft(); //returns left child (if there is no left child return null)

        public IWAVLNode getRight(); //returns right child (if there is no right child return null)

        public boolean isRealNode(); // Returns True if this is a non-virtual WAVL node (i.e not a virtual leaf or a sentinal)

        public int getSubtreeSize(); // Returns the number of real nodes in this node's subtree (Should be implemented in O(1))

        public void setLeft(IWAVLNode leftChild);

        public void setRight(IWAVLNode rightChild);

        public int getRank();

        public void setRank(Integer rank);

        public IWAVLNode getFather();

        public void setFather(IWAVLNode father);

        public void reSetSubtreeSize();
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
        private IWAVLNode rightChild;
        /**
         * The left child of this if exists
         */
        private IWAVLNode leftChild;
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
        private IWAVLNode father;
        /**
         * For external leafs only.
         */
        private WAVLNode() {
            this.rank = -1;
            this.subTreeSize = 0;
        }

        public WAVLNode(Integer key, String info, IWAVLNode rightChild, IWAVLNode leftChild, IWAVLNode father) {

            if (key == null) {
                throw new IllegalArgumentException("Key cloud not be null");
            }

            if (rightChild == null) {
                this.rightChild = WAVLTree.this.externalLeaf;
            } else {
                this.rightChild = rightChild;
            }
            if (leftChild == null) {
                this.leftChild = WAVLTree.this.externalLeaf;
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

        public IWAVLNode getLeft() {
            return this.leftChild;
        }

        public IWAVLNode getRight() {
            return this.rightChild;
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

        public void setRight(IWAVLNode rightChild) {

            this.rightChild = rightChild;
        }

        public void setLeft(IWAVLNode leftChild) {

            this.leftChild = leftChild;
        }

        public void setSubTreeSize(Integer subTreeSize) {
            this.subTreeSize = subTreeSize;
        }

        public void setRank(Integer rank) {
            this.rank = rank;
        }

        public IWAVLNode getFather() {
            return this.father;
        }

        public void setFather(IWAVLNode father) {
            this.father = father;
        }

        /**
         * Resetting the subtree size var according to the right and the left children
         * @Complexity O(1)
         */
        public void reSetSubtreeSize() {
            this.setSubTreeSize(getRight().getSubtreeSize() + getLeft().getSubtreeSize() + 1);
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