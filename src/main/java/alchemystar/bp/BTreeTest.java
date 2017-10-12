package alchemystar.bp;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.junit.Test;

import alchemystar.bp.value.Value;
import alchemystar.bp.value.ValueInt;
import alchemystar.bp.value.ValueString;

/**
 * @Author lizhuyang
 */
public class BTreeTest {

    @Test
    public void test() {
        BPTree bpTree = new BPTree();
        int insertSize = 100000;
        for (int i = 1; i <= insertSize; i++) {
            Value[] values = new Value[2];
            // Random random = new Random();
            // int toInsert = random.nextInt(insertSize);
            values[0] = new ValueInt(i);
            values[1] = new ValueString("alchemystar");
            Tuple tuple = new Tuple(values);
            if (i == 9) {
                bpTree.insert(tuple);
            } else {
                bpTree.insert(tuple);
            }
        }
        BPNode node = bpTree.getHead();
        List<Tuple> list = new ArrayList<Tuple>();
        while (node != null) {
            for (int i = 0; i < node.getEntries().size(); i++) {
                list.add(node.getEntries().get(i));
            }
            node = node.next;
        }

        for (Tuple item : list) {
            bpTree.remove(item);
        }
        printBtree(bpTree.getRoot());
    }

    public static void printBtree(BPNode BPNode) {
        if (BPNode == null) {
            return;
        }

        if ((!BPNode.isLeaf()) && ((BPNode.getEntries().size() + 1) != BPNode.getChildren().size())) {
            System.out.println("B+Tree Error");
        } else {
            //  System.out.println("it's okay");
        }
        /*   if (BPNode.isRoot()) {
            System.out.print("root:");
        }*/
        /*if (BPNode.isLeaf()) {
            System.out.print("leaf:");
            for (int i = 0; i < BPNode.getEntries().size(); i++) {
                System.out.print(BPNode.getEntries().get(i) + ",");
            }
            return;
        }
        for (int i = 0; i < BPNode.getEntries().size(); i++) {
            System.out.print(BPNode.getEntries().get(i) + ",");
        }
        System.out.println();*/
        if (!BPNode.isLeaf()) {
            for (int i = 0; i < BPNode.getChildren().size(); i++) {
                if (BPNode.getChildren().get(i).getParent() != BPNode) {
                    System.out.println("parent BPNode error");
                    throw new RuntimeException("error");
                }
                if (BPNode.getEntries().size() + 1 != BPNode.getChildren().size()) {
                    throw new RuntimeException("cacaca error");
                }
                if (i < BPNode.getEntries().size()) {
                    if (BPNode.getEntries().get(i)
                            .compare(BPNode.getChildren().get(i).getEntries().get(BPNode.getChildren
                                    ().get(i).getEntries().size() - 1)) < 0) {
                        throw new RuntimeException("hahaha error");
                    }
                }
                if (i == BPNode.getEntries().size()) {
                    if (BPNode.getEntries().get(i - 1)
                            .compare(BPNode.getChildren().get(i).getEntries().get(BPNode.getChildren
                                    ().get(i).getEntries().size() - 1)) > 0) {
                        throw new RuntimeException("hahaha error");
                    }
                }
                printBtree(BPNode.getChildren().get(i));
            }
        }

    }

    @Test
    public void test2() {
        BPTree bpTree = new BPTree();
        bpTree.insert(genTuple(7699));
        bpTree.insert(genTuple(3825));
        bpTree.insert(genTuple(9358));
        bpTree.insert(genTuple(4519));
        bpTree.insert(genTuple(1362));
        bpTree.insert(genTuple(2288));
        bpTree.insert(genTuple(5599));
        bpTree.insert(genTuple(1562));
        bpTree.insert(genTuple(898));
        bpTree.insert(genTuple(9786));
        bpTree.insert(genTuple(9691));
        bpTree.insert(genTuple(4139));
        bpTree.insert(genTuple(9674));
        bpTree.insert(genTuple(3620));
        bpTree.insert(genTuple(5514));
        bpTree.insert(genTuple(6645));
        bpTree.insert(genTuple(6949));
        bpTree.insert(genTuple(8651));
        bpTree.insert(genTuple(9645));
        bpTree.insert(genTuple(5175));
        bpTree.insert(genTuple(6162));
        bpTree.insert(genTuple(6521));
        bpTree.insert(genTuple(3214));
        bpTree.insert(genTuple(7351));
        bpTree.insert(genTuple(7095));
        bpTree.insert(genTuple(3719));
        bpTree.insert(genTuple(1883));
        bpTree.insert(genTuple(1494));
        bpTree.insert(genTuple(9660));
        bpTree.insert(genTuple(1438));
        bpTree.insert(genTuple(6874));
        bpTree.insert(genTuple(2854));
        bpTree.insert(genTuple(5718));
        System.out.println("hahaha");
        BPNode BPNode = bpTree.getHead();

        while (BPNode.getNext() != null) {
            BPNode = BPNode.getNext();
        }
        while (BPNode != null) {
            for (int i = BPNode.getEntries().size() - 1; i >= 0; i--) {
                // System.out.println(BPNode.getEntries().get(i));
                Tuple tuple = bpTree.get(BPNode.getEntries().get(i));
                bpTree.remove(tuple);
                if (tuple == null) {
                    System.out.println("it is null");
                } else {
                    System.out.println(tuple.getValues()[0]);
                }
            }
            BPNode = BPNode.getPrevious();
        }
        printBtree(bpTree.getRoot());
    }

    @Test
    public void test3() {
        BPTree bpTree = new BPTree();
        int insertSize = 1000;
        for (int i = 1; i <= insertSize; i++) {
            Random random = new Random();
            int toInsert = random.nextInt(insertSize);
            Tuple tuple = genTuple(toInsert);
            // System.out.println("index=" + i);
            if (toInsert % 2 == 0) {
                bpTree.insert(tuple);
            } else {
                bpTree.remove(tuple);
            }
        }
        printBtree(bpTree.getRoot());
        for (int i = 1; i <= insertSize * 5; i++) {
            Random random = new Random();
            int toInsert = random.nextInt(insertSize * 10);
            Tuple tuple = genTuple(toInsert);
            //if (bpTree.get(tuple) == null) {
            System.out.println("indexuuu=" + i);

            if (insertSize % 2 == 0) {
                bpTree.remove(tuple);
            } else {
                bpTree.insert(tuple);
            }

            printBtree(bpTree.getRoot());
        }

        BPNode node = bpTree.getHead();
        List<Tuple> list = new ArrayList<Tuple>();
        while (node != null) {
            for (int i = 0; i < node.getEntries().size(); i++) {
                list.add(node.getEntries().get(i));
            }
            node = node.next;
        }

        for (Tuple item : list) {
            bpTree.remove(item);
        }
        printBtree(bpTree.getRoot());

    }

    public static Tuple genTuple(int i) {
        Value[] values = new Value[2];
        values[0] = new ValueInt(i);
        values[1] = new ValueString("alchemystar");
        return new Tuple(values);
    }
}
