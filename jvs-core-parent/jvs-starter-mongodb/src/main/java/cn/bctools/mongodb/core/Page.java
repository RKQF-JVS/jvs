package cn.bctools.mongodb.core;


import java.util.Collections;
import java.util.List;

public class Page<T> {
    // 当前的页数
    private int current = 1;
    // 一页的数据
    private int size = 10;
    // 符合条件的数据
    private long total;
    // 分页的数据
    private List<T> data;

    public Page(int current, int size) {
        setCurrent(current);
        setSize(size);
    }

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        this.current = current < 1 ? 1 : current;
    }

    public int getSize() {
        return size;
    }

    public void setSize(int size) {
        this.size = size < 10 ? 10 : size;
    }

    public long getTotal() {
        return total;
    }

    public void setTotal(long total) {
        this.total = total;
    }

    public List<T> getData() {
        return data != null ? data : Collections.emptyList();
    }

    public void setData(List<T> data) {
        this.data = data;
    }


    /**
     * 当前要查找的页是否还有数据 需要提前先填写total
     *
     * @return
     */
    public boolean currentIsHaveData() {
        return total > getSkip();
    }


    public long getSkip() {
        return (current - 1) * size;
    }

}
