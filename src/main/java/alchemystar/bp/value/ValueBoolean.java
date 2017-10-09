package alchemystar.bp.value;

/**
 * ValueBoolean
 *
 * @Author lizhuyang
 */
public class ValueBoolean extends Value {

    private boolean b;

    public ValueBoolean() {
    }

    public ValueBoolean(boolean b) {
        this.b = b;
    }

    @Override
    public int getLength() {
        // 1 for type
        return 1 + 1;
    }

    @Override
    public byte getType() {
        return BOOLEAN;
    }

    @Override
    public String toString() {
        if (b) {
            return "true";
        } else {
            return "false";
        }
    }

    public boolean getBoolean() {
        return b;
    }

    public ValueBoolean setBoolean(boolean b) {
        this.b = b;
        return this;
    }

    @Override
    public int compare(Value value) {
        // todo
        return 0;
    }
}
