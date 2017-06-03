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

        while (currentNode.isRealNode())
            if (currentNode.getKey() < k)
                currentNode = currentNode.getLeft();
            else if (currentNode.getKey() < k)
                currentNode = currentNode.getRight();
            else
                return currentNode; // Match

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

        if (empty()) { // Create the root if the tree is empty
            root = new WAVLNode(k, i, null, null, null);
            System.out.println("Setting New Root");
            return 0;
        }
        return recursiveInsert(root, new WAVLNode(k, i, null, null, null));
    }

    /**
     * Inserts newNode and re-balance tree
     * @param tree The root of this current recursive insert call
     * @param newNode The node to insert
     * @return The number of rotations needed to re-balance the tree after the insert (double rotate is considered as 2 rotations)
     */
    private int recursiveInsert(IWAVLNode tree, IWAVLNode newNode) {

        System.out.println("Calling Recursive Insert: " + newNode.getValue() + " With root: " + tree.getValue());
        System.out.println("" + newNode.getValue() + ": " + newNode.getRank() + ". " + tree.getKey() + ": " + tree.getRank());

        int k = newNode.getKey();
        int tk = tree.getKey();
        int numRotationsNeeded = 0;
        boolean isRoot = this.root == tree;

        if (k < tk) {

            if (tree.getLeft().isRealNode()) {
                numRotationsNeeded = recursiveInsert(tree.getLeft(), newNode);
            } else {
                tree.setLeft(newNode);
            }
            if (tree.getLeft().getRank() - tree.getRight().getRank() == 2) { // Left is deeper, need rotation

                // Need Rotation:
                if (k < tree.getLeft().getKey()) {
                    tree = rightRotate(tree);
                    numRotationsNeeded += 1;
                    tree.getFather().setRank(tree.getFather().getRank() - 1);
                } else {
                    tree = doubleRotateWithRightChild(tree);
                    numRotationsNeeded += 2;
                    tree.getRight().setRank(tree.getRight().getRank() + 1);
                    tree.setRank(tree.getRank() - 1);
                    tree.getFather().setRank(tree.getFather().getRank() - 1);
                }
            }
        } else if (k > tk) {

            if (tree.getRight().isRealNode()) {
                numRotationsNeeded = recursiveInsert(tree.getRight(), newNode);
            } else {
                tree.setRight(newNode);
                System.out.println("No Real Right. Setting to: " + tree.getValue() + " Right Child: " + newNode.getValue());
            }
            if (tree.getRight().getRank() - tree.getLeft().getRank() == 2) { // Right is deeper, need rotation

                System.out.println("Need Rotation For new node: " + newNode.getValue());

                // Need Rotation:
                if (k > tree.getRight().getKey()) {
                    tree = leftRotate(tree);
                    numRotationsNeeded += 1;

                    tree.getFather().setRank(tree.getFather().getRank() - 1);
                } else {
                    tree = doubleRotateWithLeftChild(tree);
                    numRotationsNeeded += 2;
                    tree.getLeft().setRank(tree.getLeft().getRank() + 1);
                    tree.setRank(tree.getRank() - 1);
                    tree.getFather().setRank(tree.getFather().getRank() - 1);
                }
            }
        } else { // Already existing:
            return -1;
        }

        if (isRoot) {
            System.out.println("Assign " + tree.getValue() + " to root");
            this.root = tree;
        }

        return numRotationsNeeded;
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
    private IWAVLNode leftRotate(IWAVLNode node) {

        System.out.println("Left rotate with: " + node.getKey());

        IWAVLNode tempNode = node.getRight();
        node.setRight(tempNode.getLeft());
        tempNode.setLeft(node);

        return tempNode;
    }

    /**
     * Performing a rotation with the right child of node.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private IWAVLNode rightRotate(IWAVLNode node) {

        System.out.println("Right rotate with: " + node.getKey());


        IWAVLNode tempNode = node.getLeft();
        node.setLeft(tempNode.getRight());
        tempNode.setRight(node);

        return tempNode;
    }

    /**
     * Performing a double rotation with left child.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private IWAVLNode doubleRotateWithLeftChild(IWAVLNode node) {
        node.setLeft(rightRotate(node.getLeft()));
        return leftRotate(node);
    }

    /**
     * Performing a double rotation with right child.
     *
     * @return The new node(tree) after the rotation
     * @complexity O(1)
     */
    private IWAVLNode doubleRotateWithRightChild(IWAVLNode node) {
        node.setRight(leftRotate(node.getRight()));
        return rightRotate(node);
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
            while (node.getLeft() != null) {
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
            while (node.getRight() != null) {
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
        return recursiveInOrderTraversal(new IWAVLNode[size()], root, 0);
    }

    /**
     * Continuing the in-order traversal from a given node with a given array of pre-inserted nodes.
     */
    private IWAVLNode[] recursiveInOrderTraversal(IWAVLNode[] insertedNodes, IWAVLNode node, int numInsertedNodes) {
        if (!node.isRealNode()) {
            return insertedNodes;
        }

        insertedNodes = recursiveInOrderTraversal(insertedNodes, node.getLeft(), numInsertedNodes);
        numInsertedNodes += 1;
        insertedNodes[numInsertedNodes] = node;
        insertedNodes = recursiveInOrderTraversal(insertedNodes, node.getRight(), numInsertedNodes);

        return insertedNodes;
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
            this.subTreeSize = this.rightChild.getSubtreeSize() + this.leftChild.getSubtreeSize() + 1;
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

            rightChild.setFather(this);

            if (this.rightChild != null) {

                this.setSubTreeSize(this.getSubtreeSize() - this.getRight().getSubtreeSize());
                //this.setRank(this.getRank() - this.getRight().getRank());
                this.rightChild = rightChild;
                this.setSubTreeSize(this.getSubtreeSize() + this.getRight().getSubtreeSize());
                //this.setRank(this.getRank() + this.getRight().getRank());
            } else {
                this.rightChild = rightChild;
            }
        }

        public void setLeft(IWAVLNode leftChild) {

            leftChild.setFather(this);

            if (this.leftChild != null) {

                this.setSubTreeSize(this.getSubtreeSize() - this.getLeft().getSubtreeSize());
                //this.setRank(this.getRank() - this.getLeft().getRank());
                this.leftChild = leftChild;
                this.setSubTreeSize(this.getSubtreeSize() + this.getLeft().getSubtreeSize());
                //this.setRank(this.getRank() + this.getLeft().getRank());
            } else {
                this.leftChild = leftChild;
            }
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