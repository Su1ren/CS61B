package bstmap;

import java.util.*;

public class BSTMap<K extends Comparable<K> , V> implements Map61B<K, V> {
    private class BSTNode {
        public K key;
        public V value;
        public BSTNode left, right;
        public BSTNode(K key, V value) {
            this.key = key;
            this.value = value;
            this.left = this.right = null;
        }
    }

    private BSTNode root;
    private int size;

    public BSTMap() {
        root = null;
        size = 0;
    }

    @Override
    public int size() {
        return size;
    }

    @Override
    public void clear() {
        root = null;
        size = 0;
    }

    @Override
    public boolean containsKey(K key) {
        return this.containsHelper(root, key);
    }

    private boolean containsHelper(BSTNode cur, K key) {
        if (cur == null) {
            return false;
        } else if (key.compareTo(cur.key) == 0) {
            return true;
        } else if (key.compareTo(cur.key) < 0) {
            return containsHelper(cur.left, key);
        } else {
            return containsHelper(cur.right, key);
        }
    }

    private BSTNode putHelper(BSTNode cur, K key, V value) {
        if (cur == null) {
            ++size;
            return new BSTNode(key, value);
        } else if (key.compareTo(cur.key) == 0) {
            cur.value = value;
        } else if (key.compareTo(cur.key) < 0) {
            cur.left = putHelper(cur.left, key, value);
        } else {
            cur.right = putHelper(cur.right, key, value);
        }
        return cur;
    }


    @Override
    public void put(K key, V value) {
        root = putHelper(root, key, value);
    }

    @Override
    public V get(K key) {
        return getHelper(root, key);
    }

    public void printInOrder() {
        printHelper(root);
    }

    private void printHelper(BSTNode cur) {
        if (cur != null) {
            printHelper(cur.left);
            System.out.println(cur.key + "\t" + cur.value);
            printHelper(cur.right);
        }
    }

    private V getHelper(BSTNode cur, K key) {
        if (cur == null) {
            return null;
        } else if (key.compareTo(cur.key) == 0) {
            return cur.value;
        } else if (key.compareTo(cur.key) < 0) {
            return getHelper(cur.left, key);
        } else {
            return getHelper(cur.right, key);
        }
    }

    @Override
    public Set<K> keySet() {
        if (root == null) {
            return new HashSet<>();
        }

        Set<K> re = new HashSet<>();
        Queue<BSTNode> q = new ArrayDeque<>();
        q.add(root);
        while (!q.isEmpty()) {
            BSTNode cur = q.remove();
            re.add(cur.key);
            if (cur.left != null) {
                q.add(cur.left);
            }
            if (cur.right != null) {
                q.add(cur.right);
            }
        }
        return re;
    }

    @Override
    public V remove(K key) {
        if (!containsKey(key)) {
            return null;
        }

        V re = get(key);
        root = removeHelper(root, key);
        --size;
        return re;
    }

    /**
     * Remove the key Node in the BST.
     * 1. If the dst Node doesn't have left, return right.
     * 2. If the dst Node doesn't have right, return left.
     * 3. If the dst Node has both left and right, find the right most Node in its right subtree.
     *
     * @param cur BSTNode
     * @param key the key to delete
     * @return the new Node to take place after remove.
     */
    private BSTNode removeHelper(BSTNode cur, K key) {
        if (cur == null) {
            return null;
        }
        if (key.compareTo(cur.key) < 0) {
            cur.left = removeHelper(cur.left, key);
        } else if (key.compareTo(cur.key) > 0) {
            cur.right = removeHelper(cur.right, key);
        } else { // Node found
            if (cur.left == null) {
                return cur.right;
            }
            if (cur.right == null) {
                return cur.left;
            }
            BSTNode temp = cur;
            cur = getRightMostNode(cur.right);
            cur.left = temp.left; // left subtree unchanged, take over
            cur.right = removeHelper(temp.right, cur.key); // right subtree changed, adjust the right subtree as remove the take-place Node.
        }
        return cur;
    }

    private BSTNode getRightMostNode(BSTNode cur) {
        if (cur.left == null) {
            return cur;
        }
        return getRightMostNode(cur.left);
    }

    @Override
    public V remove(K key, V value) {
        if (!containsKey(key) || !get(key).equals(value)) {
            return null;
        }
        V re = get(key);
        root = removeHelper(root, key);
        --size;
        return re;
    }

    @Override
    public Iterator<K> iterator() {
        return keySet().iterator();
    }
}
