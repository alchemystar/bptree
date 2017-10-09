package alchemystar.bp;

import alchemystar.bp.value.Value;

/**
 * 最基本的元组概念
 *
 * @Author lizhuyang
 */
public class Tuple {
    // 元组中的值
    protected Value[] values;

    public Tuple() {
    }

    public Tuple(Value[] values) {
        this.values = values;
    }

    // 和另一个tuple的比较
    // 注意,另一个tuple的可能是个索引,所以两者column的length可能不等
    // 这边仅仅按长度最小计算,是为了索引比较
    // todo 物理元组计算需不同
    public int compare(Tuple tuple) {
        int min = values.length < tuple.getValues().length ? values.length : tuple.getValues().length;
        for (int i = 0; i < min; i++) {
            int comp = values[i].compare(tuple.getValues()[i]);
            if (comp == 0) {
                continue;
            }
            return comp;
        }
        return 0;
    }

    public int getLength() {
        int sum = 0;
        for (Value item : values) {
            sum += item.getLength();
        }
        return sum;
    }

    public Value[] getValues() {
        return values;
    }

    public Tuple setValues(Value[] values) {
        this.values = values;
        return this;
    }
}
