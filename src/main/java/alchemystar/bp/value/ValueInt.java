package alchemystar.bp.value;

/**
 * @Author lizhuyang
 */
public class ValueInt extends Value {

    private int i;

    public ValueInt() {
    }

    public ValueInt(int i) {
        this.i = i;
    }

    @Override
    public int getLength() {
        // 1 for tupe
        return 1 + 4;
    }

    @Override
    public byte getType() {
        return INT;
    }

    @Override
    public String toString() {
        return String.valueOf(i);
    }

    public int getInt() {
        return i;
    }

    public ValueInt setInt(int i) {
        this.i = i;
        return this;
    }

    @Override
    public int compare(Value value) {
        int toCompare = (((ValueInt) value).getInt());
        if (i > toCompare) {
            return 1;
        }
        if (i == toCompare) {
            return 0;
        }
        return -1;
    }
}
