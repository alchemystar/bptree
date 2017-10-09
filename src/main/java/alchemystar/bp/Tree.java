package alchemystar.bp;

/**
 * Tree
 *
 * @Author lizhuyang
 */
public interface Tree {

    Tuple get(Tuple key);   //查询

    boolean remove(Tuple key);    //移除

    void insert(Tuple key); //插入
}
