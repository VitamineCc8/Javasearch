package com.javasearch.www.util;

import lombok.Getter;
import lombok.Setter;

public class SearchBstTree<K, V> {

    private TreeNode<K, V> root;
    private final CompareInterface<K> compareImpl;
    private int nodeCount = 0;

    public int getNodeCount(TreeNode root) {
        if (root == null) {
            return 0;
        }
        return nodeCount;
    }

    public int getMaxHeight(TreeNode<K, V> root) {
        if (root == null) {
            return 0;
        }
        return Math.max(getHeight(root.left), getHeight(root.right));
    }

    public SearchBstTree(CompareInterface<K> compareImpl) {
        this.compareImpl = compareImpl;
    }

    private TreeNode<K, V> leftRotation(TreeNode<K, V> node) {
        TreeNode<K, V> rc = node.right;
        node.right = rc.left;
        rc.left = node;
        node.height = Math.max(
                getHeight(node.left),
                getHeight(node.right)
        ) + 1;
        rc.height = Math.max(
                getHeight(rc.left),
                getHeight(rc.right)
        ) + 1;
        return rc;
    }

    private TreeNode<K, V> rightRotation(TreeNode<K, V> node) {
        TreeNode lc = node.left;
        node.left = lc.right;
        lc.right = node;
        node.height = Math.max(
                getHeight(node.left),
                getHeight(node.right)
        ) + 1;
        lc.height = Math.max(
                getHeight(lc.left),
                getHeight(lc.right)
        ) + 1;
        return lc;
    }

    private TreeNode<K, V> rightAndLeft(TreeNode<K, V> node) {
        node.right = rightRotation(node.right);
        return leftRotation(node);
    }

    private TreeNode<K, V> leftAndRight(TreeNode<K, V> node) {
        node.left = leftRotation(node.left);
        return rightRotation(node);
    }

    private int getHeight(TreeNode<K, V> node) {
        if (node == null) {
            return 0;
        }
        return node.height;
    }

    public void insert(K key, V value) {
        TreeNode<K, V> target = new TreeNode<>(null, null, 1, key, value);
        root = packInsert(root, target);
    }


    private TreeNode<K, V> packInsert(TreeNode<K, V> node, TreeNode<K, V> target) {
        if (node == null) {
            node = target;
            this.nodeCount++;
        } else if (compareImpl.compare(target.key, node.key) > 0) {
            node.right = packInsert(node.right, target);
            if (getHeight(node.right) - getHeight(node.left) == 2) {
                if (compareImpl.compare(target.key, node.right.key) > 0) {
                    node = leftRotation(node);
                } else if (compareImpl.compare(target.key, node.right.key) < 0) {
                    node = rightAndLeft(node);
                }
            }
        } else if (compareImpl.compare(target.key, node.key) < 0) {
            node.left = packInsert(node.left, target);
            if (getHeight(node.left) - getHeight(node.right) == 2) {
                if (compareImpl.compare(target.key, node.left.key) < 0) {
                    node = rightRotation(node);
                } else if (compareImpl.compare(target.key, node.left.key) > 0) {
                    node = leftAndRight(node);
                }
            }
        }
        node.height = Math.max(getHeight(node.left), getHeight(node.right)) + 1;
        return node;
    }


    public V get(K key) {
        if (key == null) {
            return null;
        }
        TreeNode<K, V> run = root;
        while (run != null) {
            if (compareImpl.compare(key, run.key) > 0) {
                run = run.right;
            } else if (compareImpl.compare(key, run.key) < 0) {
                run = run.left;
            } else {
                return run.value;
            }
        }
        return null;
    }

}

@Getter
@Setter
class TreeNode<K, V> {
    public TreeNode<K, V> left;
    public TreeNode<K, V> right;
    public int height;

    public K key;
    public V value;

    public TreeNode() {
    }

    public TreeNode(TreeNode<K, V> left, TreeNode<K, V> right, int height, K key, V value) {
        this.left = left;
        this.right = right;
        this.height = height;
        this.key = key;
        this.value = value;
    }
}

