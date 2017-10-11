package alchemystar.bp;

import java.util.ArrayList;
import java.util.List;

/**
 * BPNode
 * todo 节点内部查询用二分法
 *
 * @Author lizhuyang
 */
public class BPNode {
    /**
     * 是否为叶子节点
     */
    protected boolean isLeaf;

    /**
     * 是否为根节点
     */
    protected boolean isRoot;

    /**
     * 父节点
     */
    protected BPNode parent;

    /**
     * 叶节点的前节点
     */
    protected BPNode previous;

    /**
     * 叶节点的后节点
     */
    protected BPNode next;

    /**
     * 节点的关键字
     */
    protected List<Tuple> entries;

    /**
     * 子节点
     */
    protected List<BPNode> children;
    // M必须>2
    protected int maxLength = 16;

    public BPNode(boolean isLeaf) {
        this.isLeaf = isLeaf;
        entries = new ArrayList<Tuple>();
        if (!isLeaf) {
            children = new ArrayList<BPNode>();
        }
    }

    public BPNode(boolean isLeaf, boolean isRoot) {
        this(isLeaf);
        this.isRoot = isRoot;
    }

    public Tuple get(Tuple key) {
        if (isLeaf) {
            for (Tuple tuple : entries) {
                if (tuple.compare(key) == 0) {
                    return tuple;
                }
            }
            return null;
        } else {
            // 非叶子节点
            // 如果key<最左边的key,沿第一个子节点继续搜索
            if (key.compare(entries.get(0)) < 0) {
                return children.get(0).get(key);
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                // 如果key >  最右边的key,则按照最后一个子节点搜索
                return children.get(children.size() - 1).get(key);
            } else {
                for (int i = 0; i < entries.size(); i++) {
                    // 比key大的前一个子节点继续搜索
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        return children.get(i + 1).get(key);
                    }
                }
            }
        }
        return null;
    }

    public void insert(Tuple key, BPTree tree) {
        if (isLeaf) {
            // 无需分裂
            if (!isLeafSplit()) {
                innerInsert(key);
                //                if (parent != null) {
                //                    parent.updateInsert(tree);
                //                }
            } else {
                // 需要分裂
                // 则分裂成左右两个节点
                BPNode left = new BPNode(true);
                BPNode right = new BPNode(true);
                if (previous != null) {
                    previous.setNext(left);
                    left.setPrevious(previous);
                }
                if (next != null) {
                    // 相当于在list中插入了一个数据
                    next.setPrevious(right);
                    right.setNext(next);
                }
                if (previous == null) {
                    tree.setHead(left);
                }
                left.setNext(right);
                right.setPrevious(left);
                previous = null;
                next = null;
                // 插入后再分裂
                innerInsert(key);
                int leftSize = this.entries.size() / 2;
                int rightSize = this.entries.size() - leftSize;

                // 左右节点,分别赋值
                for (int i = 0; i < leftSize; i++) {
                    left.getEntries().add(entries.get(i));
                }
                // 叶子节点需要全拷贝
                for (int i = 0; i < rightSize; i++) {
                    right.getEntries().add(entries.get(leftSize + i));
                }

                // 表明当前节点不是根节点
                if (parent != null) {
                    // 调整父子节点的关系
                    // 寻找到当前节点对应的index
                    int index = parent.getChildren().indexOf(this);
                    // 删掉当前节点
                    parent.getChildren().remove(this);
                    left.setParent(parent);
                    right.setParent(parent);
                    // 将节点增加到parent上面
                    parent.getChildren().add(index, left);
                    parent.getChildren().add(index + 1, right);
                    // for gc
                    setEntries(null);
                    setChildren(null);

                    // 插入关键字
                    parent.innerInsert(right.getEntries().get(0));
                    // 更新
                    parent.updateInsert(tree);
                    setParent(null);
                } else {
                    // 如果是根节点
                    isRoot = false;
                    // 根节点的分裂
                    BPNode parent = new BPNode(false, true);
                    tree.setRoot(parent);
                    left.setParent(parent);
                    right.setParent(parent);
                    parent.getChildren().add(left);
                    parent.getChildren().add(right);
                    // for gc
                    setEntries(null);
                    setChildren(null);
                    // 插入关键字
                    parent.innerInsert(right.getEntries().get(0));
                    // 更新根节点
                    parent.updateInsert(tree);
                }
            }
        } else {
            // 如果不是叶子节点,沿着第一个子节点继续搜索
            if (key.compare(entries.get(0)) < 0) {
                children.get(0).insert(key, tree);
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                // 沿最后一个子节点继续搜索
                children.get(children.size() - 1).insert(key, tree);
            } else {
                //否则沿比key大的前一个子节点继续搜索
                for (int i = 0; i < entries.size(); i++) {
                    // 比key大的前一个子节点继续搜索
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        children.get(i + 1).insert(key, tree);
                        break;
                    }
                }
            }
        }
    }

    protected boolean remove(Tuple key, BPTree tree) {
        boolean found = false;
        // 如果是叶子节点
        if (isLeaf) {
            // 如果不包含此key,则直接返回
            if (!contains(key)) {
                return false;
            }
            // 叶子节点 && 根节点,表明只有此一个节点,直接删除
            if (isRoot) {
                if (remove(key)) {
                    found = true;
                }
            } else {
                if (canRemoveDirect()) {
                    int index = getRemoveKeyIndex(key);
                    if (remove(key)) {
                        found = true;
                    }
                    //  if (index == 0) {
                    //      adjustParentEntries();
                    //  }
                } else {
                    // 如果自身关键字数小于M/2,并且前节点关键字数大于M/2,则从其处借补
                    if (canBorrow(previous)) {
                        if (remove(key)) {
                            borrowLeafPrevious();
                            found = true;
                        }

                    } else if (canBorrow(next)) {
                        if (remove(key)) {
                            borrowLeafNext();
                            found = true;
                        }
                    } else {
                        // 现在需要合并叶子节点
                        if (canMerge(previous)) {
                            // 如果没有这个节点
                            if (getRemoveKeyIndex(key) == -1) {
                                return false;
                            }
                            // 与前节点合并
                            addPreNode(previous);
                            if (remove(key)) {
                                found = true;
                            }
                            previous.setParent(null);
                            previous.setEntries(null);
                            // 删掉当前节点的entry
                            int currEntryIndex = getParentEntry(this);
                            if (parent == null || parent.getEntries() == null || currEntryIndex < 0) {
                                currEntryIndex = getParentEntry(this);
                            }
                            parent.getEntries().remove(currEntryIndex);
                            // 然后删掉前驱
                            parent.getChildren().remove(previous);
                            previous.setParent(null);
                            // 更新链表
                            if (previous.getPrevious() != null) {
                                BPNode temp = previous;
                                temp.getPrevious().setNext(this);
                                // 更新前驱
                                previous = temp.getPrevious();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            } else {
                                tree.setHead(this);
                                previous.setNext(null);
                                previous = null;
                            }
                        } else if (canMerge(next)) {
                            if (getRemoveKeyIndex(key) == -1) {
                                return false;
                            }
                            // 与后节点合并
                            addNextNode(next);
                            if (remove(key)) {
                                found = true;
                            }
                            next.setParent(null);
                            next.setEntries(null);
                            // 同时删掉后继节点的entry
                            int currEntryIndex = getParentEntry(this.next);
                            parent.getEntries().remove(currEntryIndex);
                            parent.getChildren().remove(next);
                            // 更新链表
                            if (next.getNext() != null) {
                                BPNode temp = next;
                                temp.getNext().setPrevious(this);
                                next = temp.getNext();
                                temp.setPrevious(null);
                                temp.setNext(null);
                            } else {
                                next.setPrevious(null);
                                next = null;
                            }
                        }
                    }

                }
                parent.updateRemove(tree);
            }
        } else {
            // 如果不是叶子节点,沿着第一个子节点继续搜索
            if (key.compare(entries.get(0)) < 0) {
                if (children.get(0).remove(key, tree)) {
                    found = true;
                }
            } else if (key.compare(entries.get(entries.size() - 1)) >= 0) {
                // 沿最后一个子节点继续搜索
                if (children.get(children.size() - 1).remove(key, tree)) {
                    found = true;
                }
            } else {
                //否则沿比key大的前一个子节点继续搜索
                for (int i = 0; i < entries.size(); i++) {
                    if (key.compare(entries.get(i)) >= 0 && key.compare(entries.get(i + 1)) < 0) {
                        if (children.get(i + 1).remove(key, tree)) {
                            found = true;
                        }
                        break;
                    }
                }
            }
        }
        return found;
    }

    // 删除节点后的中间节点更新
    protected void updateRemove(BPTree tree) {
        if (children.size() < maxLength / 2 || children.size() < 2) {
            if (isRoot) {
                // 根节点并且子节点树>=2 , 直接return
                if (children.size() >= 2) {
                    return;
                } else {
                    // 如果 < 2,则需要和子节点合并
                    // 直接将子节点做为根节点
                    BPNode root = children.get(0);
                    tree.setRoot(root);
                    root.setParent(null);
                    root.setRoot(true);
                    setEntries(null);
                    setChildren(null);
                }
            } else {
                // 计算前后节点
                int currIdx = parent.getChildren().indexOf(this);
                int prevIdx = currIdx - 1;
                int nextIdx = currIdx + 1;
                BPNode previous = null, next = null;
                if (prevIdx >= 0) {
                    previous = parent.getChildren().get(prevIdx);
                }
                if (nextIdx < parent.getChildren().size()) {
                    next = parent.getChildren().get(nextIdx);
                }
                if (canBorrow(previous)) {
                    // 从前驱处借
                    // 从前叶子节点末尾节点添加到首位
                    borrowNodePrevious(previous);
                } else if (canBorrow(next)) {
                    // 从后继中借
                    borrowNodeNext(next);
                } else {
                    // 现在需要合并子节点
                    if (canMerge(previous)) {
                        // 与前节点合并
                        addPreNode(previous);
                        previous.setParent(null);
                        previous.setEntries(null);
                        // 删掉当前节点的entry
                        int currEntryIndex = getParentEntry(this);
                        parent.getEntries().remove(currEntryIndex);
                        // 删掉前驱
                        parent.getChildren().remove(previous);
                    } else if (canMerge(next)) {
                        // 与后节点合并
                        addNextNode(next);
                        next.setParent(null);
                        next.setEntries(null);
                        // 同时删掉后继节点的entry
                        int currEntryIndex = getParentEntry(next);
                        parent.getEntries().remove(currEntryIndex);
                        // 删掉后继
                        parent.getChildren().remove(next);
                    }
                }
                parent.updateRemove(tree);
            }
        }
    }

    // 这边其实没有必要调整,但调整了也不影响B+树的性质
    private void adjustParentEntries() {
        // 调整对应的关键字
        int currEntryIndex = getParentEntry(this);
        // 如果 < 0,表明当节点是parent下的第一个节点,不调整其父的父关键字
        if (currEntryIndex < 0) {
            return;
        }
        parent.getEntries().remove(currEntryIndex);
        parent.getEntries().add(currEntryIndex, getEntries().get(0));
    }

    private int getParentEntry(BPNode BPNode) {
        int index = parent.getChildren().indexOf(BPNode);
        return index - 1;
    }

    public boolean isNodeSplit() {
        // 因为已经在下面+1,所以不需要=号
        if ((entries.size()) > maxLength) {
            return true;
        }
        return false;
    }

    public boolean canRemoveDirect() {
        if (entries.size() > maxLength / 2 && entries.size() > 2) {
            return true;
        }
        return false;
    }

    public boolean canBorrow(BPNode BPNode) {
        if (BPNode != null
                && BPNode.getEntries().size() > maxLength / 2
                && BPNode.getEntries().size() > 2
                && BPNode.getParent() == parent) {
            return true;
        }
        return false;
    }

    public boolean canMerge(BPNode BPNode) {
        if (BPNode != null
                && (BPNode.getEntries().size() <= maxLength / 2 || BPNode.getEntries().size() <= 2)
                && BPNode.getParent() == parent) {
            return true;
        }
        return false;
    }

    public void addPreNode(BPNode BPNode) {
        // 叶子节点无需下移
        if (!BPNode.isLeaf()) {
            int parentIdx = this.getParentEntry(this);
            // 事实上是父parent的entry下移
            entries.add(0, this.getParent().getEntries().get(parentIdx));
        }
        for (int i = BPNode.getEntries().size() - 1; i >= 0; i--) {
            entries.add(0, BPNode.getEntries().get(i));
        }
        if (!BPNode.isLeaf()) {
            for (int i = BPNode.getChildren().size() - 1; i >= 0; i--) {
                BPNode.getChildren().get(i).setParent(this);
                children.add(0, BPNode.getChildren().get(i));
            }
        }
    }

    public void addNextNode(BPNode BPNode) {
        // 后驱节点的entry下移
        // 叶子节点无需下移
        if (!BPNode.isLeaf()) {
            int parentIdx = this.getParentEntry(BPNode);
            entries.add(BPNode.getParent().getEntries().get(parentIdx));
        }
        for (int i = 0; i < BPNode.getEntries().size(); i++) {
            entries.add(BPNode.getEntries().get(i));
        }
        if (BPNode.getChildren() != null) {
            for (int i = 0; i < BPNode.getChildren().size(); i++) {
                BPNode.getChildren().get(i).setParent(this);
                children.add(BPNode.getChildren().get(i));
            }
        }
    }

    public boolean isLeafSplit() {
        // todo
        // caculate the size and define is need to split
        // 这边用<号,因为entries要+1
        if (entries.size() >= maxLength) {
            return true;
        }
        return false;
    }

    /**
     * 插入到当前节点的关键字中
     * 有序
     */
    protected void innerInsert(Tuple tuple) {
        //如果关键字列表长度为0，则直接插入
        if (entries.size() == 0) {
            entries.add(tuple);
            return;
        }
        //否则遍历列表
        for (int i = 0; i < entries.size(); i++) {
            //如果该关键字键值已存在，则更新
            // todo duplicated key error
            if (entries.get(i).compare(tuple) == 0) {
                return;
                //否则插入
            } else if (entries.get(i).compare(tuple) > 0) {
                //插入到链首
                if (i == 0) {
                    entries.add(0, tuple);
                    return;
                    //插入到中间
                } else {
                    entries.add(i, tuple);
                    return;
                }
            }
        }
        //插入到末尾
        entries.add(entries.size(), tuple);
    }

    private void borrowNodePrevious(BPNode previous) {
        int size = previous.getEntries().size();
        int childSize = previous.getChildren().size();
        // 将previous的最后一个entry到parent对应index的下一个指向,再将父节点对应的entry下放到当前节点
        //      10
        // 3 9       11
        // 变换为
        //      9
        // 3          10
        int parentIdx = getParentEntry(previous) + 1;
        // 先下放
        Tuple downerKey = parent.getEntries().get(parentIdx);
        // 由于是向previous借,肯定是0
        entries.add(0, downerKey);
        // previous的上提
        parent.getEntries().remove(parentIdx);
        parent.getEntries().add(parentIdx, previous.getEntries().get(size - 1));
        previous.getEntries().remove(size - 1);
        // 将child也借过来
        BPNode borrowChild = previous.getChildren().get(childSize - 1);
        children.add(0, borrowChild);
        borrowChild.setParent(this);
        previous.getChildren().remove(borrowChild);
    }

    private void borrowNodeNext(BPNode next) {
        // 将next的第一个entry上提,再将父节点对应的entry下放到当前节点
        //      10
        // 3       11  12
        // 变换为
        //      11
        // 10         12
        // 将child也借过来
        int parentIdx = getParentEntry(next);
        // 先下放
        Tuple downerKey = parent.getEntries().get(parentIdx);
        // 由于是向next借,所以肯定是最后
        entries.add(downerKey);
        // next的上提
        parent.getEntries().remove(parentIdx);
        parent.getEntries().add(parentIdx, next.getEntries().get(0));
        next.getEntries().remove(0);

        // 将child也借过来
        BPNode borrowChild = next.getChildren().get(0);
        children.add(borrowChild);
        borrowChild.setParent(this);
        next.getChildren().remove(borrowChild);
    }

    private void borrowLeafPrevious() {
        int size = previous.getEntries().size();
        // 从previous借最后一个过来,加到当前entry最前面
        Tuple borrowKey = previous.getEntries().get(size - 1);
        previous.getEntries().remove(borrowKey);
        entries.add(0, borrowKey);
        // 找到当前节点在父节点中的entries
        int currEntryIdx = getParentEntry(this);
        parent.getEntries().remove(currEntryIdx);
        parent.getEntries().add(currEntryIdx, borrowKey);
    }

    private void borrowLeafNext() {
        // 从next借第一个过来,加到当前entry最后面
        Tuple borrowKey = next.getEntries().get(0);
        next.getEntries().remove(borrowKey);
        entries.add(borrowKey);
        // 找到当前节点的后继节点在父节点中的parent
        int currEntryIdx = getParentEntry(this.next);
        parent.getEntries().remove(currEntryIdx);
        // 是将next的第一个上提,而不是borrowKey
        // 由于之前remove过了,所以index还是0
        parent.getEntries().add(currEntryIdx, next.getEntries().get(0));
    }

    protected void updateInsert(BPTree tree) {
        // 当前页面存不下,需要分裂
        if (isNodeSplit()) {
            BPNode left = new BPNode(false);
            BPNode right = new BPNode(false);
            int leftSize = this.entries.size() / 2;
            int rightSize = this.entries.size() - leftSize;
            // 左边复制entry
            for (int i = 0; i < leftSize; i++) {
                left.getEntries().add(entries.get(i));
            }
            // 左边复制child
            for (int i = 0; i <= leftSize; i++) {
                left.getChildren().add(children.get(i));
                children.get(i).setParent(left);
            }
            // 右边的第一个关键字上提到父节点,当前entries不保存
            // 右边复制entries
            for (int i = 1; i < rightSize; i++) {
                right.getEntries().add(entries.get(leftSize + i));
            }
            // 右边复制child
            for (int i = 1; i < rightSize + 1; i++) {
                right.getChildren().add(children.get(leftSize + i));
                children.get(leftSize + i).setParent(right);
            }
            Tuple keyToUpdateParent = entries.get(leftSize);
            if (parent != null) {
                int index = parent.getChildren().indexOf(this);
                parent.getChildren().remove(this);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(index, left);
                parent.getChildren().add(index + 1, right);
                // 插入关键字
                parent.innerInsert(keyToUpdateParent);
                // 父节点更新关键字
                parent.updateInsert(tree);
                setEntries(null);
                setChildren(null);
                setParent(null);
            } else {
                // 如果是根节点
                isRoot = false;
                BPNode parent = new BPNode(false, true);
                tree.setRoot(parent);
                left.setParent(parent);
                right.setParent(parent);
                parent.getChildren().add(left);
                parent.getChildren().add(right);
                setEntries(null);
                setChildren(null);
                // 插入关键字
                parent.innerInsert(keyToUpdateParent);
                // 更新根节点
                parent.updateInsert(tree);
            }
        }

    }

    public boolean isLeaf() {
        return isLeaf;
    }

    public BPNode setLeaf(boolean leaf) {
        isLeaf = leaf;
        return this;
    }

    public boolean isRoot() {
        return isRoot;
    }

    public BPNode setRoot(boolean root) {
        isRoot = root;
        return this;
    }

    public BPNode getParent() {
        return parent;
    }

    public BPNode setParent(BPNode parent) {
        this.parent = parent;
        return this;
    }

    public BPNode getPrevious() {
        return previous;
    }

    public BPNode setPrevious(BPNode previous) {
        this.previous = previous;
        return this;
    }

    public BPNode getNext() {
        return next;
    }

    public BPNode setNext(BPNode next) {
        this.next = next;
        return this;
    }

    public List<Tuple> getEntries() {
        return entries;
    }

    public BPNode setEntries(List<Tuple> entries) {
        this.entries = entries;
        return this;
    }

    public List<BPNode> getChildren() {
        return children;
    }

    public BPNode setChildren(List<BPNode> children) {
        this.children = children;
        return this;
    }

    // 判断当前节点是否包含此tuple
    protected boolean contains(Tuple tuple) {
        for (Tuple item : entries) {
            if (item.compare(tuple) == 0) {
                return true;
            }
        }
        return false;
    }

    protected int getRemoveKeyIndex(Tuple key) {
        int index = 0;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).compare(key) == 0) {
                index = i;
                return index;
            }
        }
        return -1;
    }

    // 删除节点
    protected boolean remove(Tuple key) {
        int index = -1;
        boolean foud = false;
        for (int i = 0; i < entries.size(); i++) {
            if (entries.get(i).compare(key) == 0) {
                index = i;
                foud = true;
                break;
            }
        }
        if (index != -1) {
            entries.remove(index);
        }
        if (foud) {
            return true;
        } else {
            return false;
        }
    }
}


