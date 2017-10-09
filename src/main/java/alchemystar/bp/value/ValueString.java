package alchemystar.bp.value;

/**
 * ValueString
 *
 * @Author lizhuyang
 */
public class ValueString extends Value {

    private String s;

    public ValueString() {
    }

    public ValueString(String s) {
        this.s = s;
    }

    // [type][length][data]
    @Override
    public int getLength() {
        return 1 + 4 + s.length();
    }

    @Override
    public byte getType() {
        return STRING;
    }

    public void read(byte[] bytes) {
        s = new String(bytes);
    }

    @Override
    public String toString() {
        return s;
    }

    public String getString() {
        return s;
    }

    public ValueString setString(String s) {
        this.s = s;
        return this;
    }

    @Override
    public int compare(Value value) {
        return 0;
    }
}
