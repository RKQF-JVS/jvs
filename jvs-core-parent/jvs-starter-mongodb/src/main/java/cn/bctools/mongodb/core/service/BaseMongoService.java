package cn.bctools.mongodb.core.service;

import cn.bctools.mongodb.core.Page;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.util.List;

/**
 * mongodb的基础service接口
 * T 对应集合实体类
 * 根据ObjectParam构成条件的参数参考下面的注解以及标注
 *
 * @see cn.bctools.mongodb.core.annotation.ConditionsAnnotation
 * @see cn.bctools.mongodb.core.annotation.OperatorAnnotation
 */
public interface BaseMongoService<T> {
    /**
     * 根据id获取数据
     * @param id id
     * @return
     */
    T getById(String id);


    /**
     * 检查id是否存在,如果不存在那么就会抛出异常，
     *
     * @param id      id
     * @param message 提示信息
     * @return
     */
    T checkByIdAngGet(String id, String message);

    /**
     * 插入实体类到db
     *
     * @param t
     * @return
     */
    boolean insert(T t);


    /**
     * 往对应的集合中批量插入数据，注意批量的数据中不要包含重复的id
     *
     * @param list
     * @return
     */
    boolean insertBatch(List<T> list);


    /**
     * 根据id删除数据
     *
     * @param id
     * @return
     */

    boolean deleteById(String id);

    /**
     * 根据object构造条件删除数据
     *
     * @param objectParam
     * @param
     * @return
     */
    boolean delete(Object objectParam);

    /**
     * 根据原始Query删除
     *
     * @param query
     * @return
     */
    boolean delete(Query query);

    /**
     * 根据 object修改
     *
     * @param id
     * @param objectUpdate
     * @param
     * @return
     */
    boolean updateById(String id, Object objectUpdate);

    /**
     * 根据update修改
     *
     * @param id
     * @param update
     * @return
     */
    boolean updateById(String id, Update update);


    /**
     * @param objectParam  构成query的object
     * @param objectUpdate 构成update的object
     * @return
     */

    boolean update(Object objectParam, Object objectUpdate);

    /**
     * 根据原始Query和Update进行修改
     *
     * @param query
     * @param update
     * @return
     */

    boolean update(Query query, Update update);

    /**
     * 获取所有数据
     *
     * @return
     */
    List<T> select();

    /**
     * 根据对象生成条件查找符合条件的实体数据
     *
     * @param objectParam
     * @return
     */
    List<T> select(Object objectParam);


    /**
     * 根据对象来生产条件查找返回R类型数据
     *
     * @param objectParam
     * @param returnClass
     * @param <R>         接收的返回类型
     * @return
     */
    <R> List<R> select(Object objectParam, Class<R> returnClass);

    /**
     * 根据Query查找符合条件的数据
     *
     * @param query
     * @return
     */
    List<T> select(Query query);

    /**
     * 根据原生query来进行查找
     *
     * @param query
     * @param returnClass
     * @param <R>
     * @return
     */
    <R> List<R> select(Query query, Class<R> returnClass);


    T selectOne(Object objectParam);

    /**
     * 根据对象来生产条件查找返回R类型数据,获取第一个符合的
     *
     * @param objectParam
     * @param returnClass
     * @param <R>         接收的返回类型
     * @return
     */
    <R> R selectOne(Object objectParam, Class<R> returnClass);


    T selectOne(Query query);


    /**
     * 根据原生Query查找第一个
     *
     * @param query
     * @param returnClass
     * @param <R>
     * @return
     */
    <R> R selectOne(Query query, Class<R> returnClass);


    Page<T> page(Object objectParam, Page<T> page);


    /**
     * 分页方法,会将data填充到传入的page里面去
     *
     * @param objectParam 根据object构造条件
     * @param page        不能为空，调用前请填充好current和size
     * @param returnClass 返回的类型
     * @param <R>
     * @return
     * @see Page
     * @see Page#setCurrent(int)  当前页
     * @see Page#setSize(int)  条数
     */
    <R> Page<R> page(Object objectParam, Page<R> page, Class<R> returnClass);


    Page<T> page(Query query, Page<T> page);

    /**
     * 分页方法,会将data填充到传入的page里面去
     *
     * @param query       原生query构造，但是不要加skip和limit,该方法会自动判断
     * @param page        不能为空，调用前请填充好current和size
     * @param returnClass 返回的类型
     * @param <R>
     * @return
     * @see Page
     * @see Page#setCurrent(int)  当前页
     * @see Page#setSize(int)  条数
     */
    <R> Page<R> page(Query query, Page<R> page, Class<R> returnClass);


    long count();

    /**
     * 根据object生成条件查找count
     *
     * @param objectParam 条件
     * @return
     */
    long count(Object objectParam);

    /**
     * 根据Query生产count
     *
     * @param query
     * @return
     */
    long count(Query query);

    /**
     * 默认为T类型
     *
     * @param conditions
     * @param page
     * @return
     * @see #aggregatePage(List, Page, Class)
     */
    Page<T> aggregatePage(List<AggregationOperation> conditions, Page<T> page);

    /**
     * 不进行排序
     *
     * @param conditions
     * @param page
     * @param clazz
     * @param <R>
     * @return
     * @see #aggregatePage(List, Sort, Page, Class)
     */
    <R> Page<R> aggregatePage(List<AggregationOperation> conditions, Page<R> page, Class<R> clazz);

    /**
     * 对该集合的aggregate的聚合函数分页,conditions不要添加skip和limit以及sort,该方法会自动添加 page请先填充好current和size
     * 拿到的数据会填充到入参参数page的data中
     *
     * @param conditions
     * @param sort       排序 如果为null将不会添加
     * @param page
     * @param clazz      接受数据的类型
     * @return
     * @see Page
     * @see Page#setCurrent(int)  当前页
     * @see Page#setSize(int)  条数
     */
    <R> Page<R> aggregatePage(List<AggregationOperation> conditions, Sort sort, Page<R> page, Class<R> clazz);

    /**
     * 接受类型为T类型
     *
     * @param aggregation
     * @return
     * @see #aggregateData(Aggregation, Class)
     */
    List<T> aggregateData(Aggregation aggregation);

    /**
     * 对聚合函数返回的接受进行接受
     *
     * @param aggregation
     * @param outputType
     * @param <R>
     * @return
     * @see #aggregate(Aggregation, Class)
     */
    <R> List<R> aggregateData(Aggregation aggregation, Class<R> outputType);

    /**
     * 对BaseMongoServiceImpl的入参table进行聚合函数查找,返回AggregationResults
     *
     * @param aggregation
     * @param outputType  接受结果的类型
     * @param <R>
     * @return
     */
    <R> AggregationResults<R> aggregate(Aggregation aggregation, Class<R> outputType);


}
