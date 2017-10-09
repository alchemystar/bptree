package alchemystar.bp;

/**
 * BPTree
 *
 * @Author lizhuyang
 */
public class BPTree implements Tree {

    /**
     * 根节点
     */
    protected BPNode root;

    /**
     * 叶子节点的链表头
     */
    protected BPNode head;

    public BPTree() {
        root = new BPNode(true, true);
        head = root;
    }

    public Tuple get(Tuple key) {
        return root.get(key);
    }

    public boolean remove(Tuple key) {
        return root.remove(key, this);
    }

    public void insert(Tuple key) {
        root.insert(key, this);
    }

    public BPNode getRoot() {
        return root;
    }

    public BPTree setRoot(BPNode root) {
        this.root = root;
        return this;
    }

    public BPNode getHead() {
        return head;
    }

    public BPTree setHead(BPNode head) {
        this.head = head;
        return this;
    }

}
