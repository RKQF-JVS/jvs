package cn.bctools.mongodb.core.service.impl;


import cn.bctools.mongodb.core.MongoHelper;
import cn.bctools.mongodb.core.Page;
import cn.bctools.mongodb.core.service.BaseMongoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.util.Assert;

import java.util.List;

public abstract class BaseMongoServiceImpl<T> implements BaseMongoService<T> {
    @Autowired
    protected MongoHelper mongoHelper;

    protected final String table;

    protected final Class<T> tableClass;

    /**
     * @param table      集合的名字
     * @param tableClass 集合对应的实体类的class
     */
    public BaseMongoServiceImpl(String table, Class<T> tableClass) {
        Assert.notNull(table, "集合名不能为空");
        Assert.notNull(table, "集合类型不能为空");
        this.table = table;
        this.tableClass = tableClass;
    }


    @Override
    public T getById(String id) {
        return mongoHelper.selectById(id, tableClass, table);
    }

    @Override
    public T checkByIdAngGet(String id, String message) {
        T t = getById(id);
        Assert.notNull(t, message);
        return t;
    }

    @Override
    public boolean insert(T t) {
        return mongoHelper.insert(t, table);
    }

    @Override
    public boolean insertBatch(List<T> list) {
        return mongoHelper.insertMulti(list, table);
    }

    @Override
    public boolean deleteById(String id) {
        return mongoHelper.deleteById(id, tableClass, table);
    }

    @Override
    public boolean delete(Object objectParam) {
        return mongoHelper.deleteByObjectParam(table, objectParam, tableClass);
    }


    @Override
    public boolean delete(Query query) {
        return mongoHelper.deleteByQuery(table, query, tableClass);
    }


    @Override
    public boolean updateById(String id, Object objectUpdate) {
        return mongoHelper.updateById(id, table, objectUpdate, tableClass);
    }


    @Override
    public boolean updateById(String id, Update update) {
        return update(new Query(Criteria.where("id").is("id")), update);
    }


    @Override
    public boolean update(Object objectParam, Object objectUpdate) {
        return mongoHelper.update(objectParam, table, objectUpdate, tableClass);
    }

    @Override
    public boolean update(Query query, Update update) {
        return mongoHelper.update(query, update, table, tableClass);
    }

    @Override
    public List<T> select() {
        return mongoHelper.selectList(table, tableClass);
    }

    @Override
    public List<T> select(Object objectParam) {
        return select(objectParam, tableClass);
    }

    @Override
    public <R> List<R> select(Object objectParam, Class<R> returnClass) {
        return mongoHelper.selectByObjecctParam(table, objectParam, returnClass);
    }


    @Override
    public List<T> select(Query query) {
        return select(query, tableClass);
    }

    @Override
    public <R> List<R> select(Query query, Class<R> returnClass) {
        return mongoHelper.selectByQuery(table, query, returnClass);
    }

    @Override
    public T selectOne(Object objectParam) {
        return selectOne(objectParam, tableClass);
    }

    @Override
    public T selectOne(Query query) {
        return selectOne(query, tableClass);
    }

    @Override
    public <R> R selectOne(Object objectParam, Class<R> returnClass) {
        return mongoHelper.selectOneByObjectParam(table, objectParam, returnClass);
    }

    @Override
    public <R> R selectOne(Query query, Class<R> returnClass) {
        return mongoHelper.selectOneByQuery(table, query, returnClass);
    }

    @Override
    public <R> Page<R> page(Object objectParam, Page<R> page, Class<R> returnClass) {
        return mongoHelper.pageByObjectParam(table, page, objectParam, returnClass);
    }

    @Override
    public <R> Page<R> page(Query query, Page<R> page, Class<R> returnClass) {
        return mongoHelper.pageByQuery(table, page, query, returnClass);
    }

    @Override
    public Page<T> page(Object objectParam, Page<T> page) {
        return page(objectParam, page, tableClass);
    }

    @Override
    public Page<T> page(Query query, Page<T> page) {
        return page(query, page, tableClass);
    }

    @Override
    public long count() {
        return count(new Query());
    }

    @Override
    public long count(Object objectParam) {
        return mongoHelper.countByObjectParam(table, objectParam);
    }

    @Override
    public long count(Query query) {
        return mongoHelper.countByQuery(table, query);
    }

    @Override
    public Page<T> aggregatePage(List<AggregationOperation> conditions, Page<T> page) {
        return aggregatePage(conditions, page, tableClass);
    }

    @Override
    public <R> Page<R> aggregatePage(List<AggregationOperation> conditions, Page<R> page, Class<R> clazz) {
        return aggregatePage(conditions, null, page, clazz);
    }

    @Override
    public <R> Page<R> aggregatePage(List<AggregationOperation> conditions, Sort sort, Page<R> page, Class<R> clazz) {
        return mongoHelper.aggregatePage(conditions, sort, table, page, clazz);
    }

    @Override
    public List<T> aggregateData(Aggregation aggregation) {
        return aggregateData(aggregation, tableClass);
    }

    @Override
    public <R> List<R> aggregateData(Aggregation aggregation, Class<R> outputType) {
        return this.aggregate(aggregation, outputType).getMappedResults();
    }

    @Override
    public <R> AggregationResults<R> aggregate(Aggregation aggregation, Class<R> outputType) {
        return mongoHelper.aggregate(aggregation, table, outputType);
    }

    protected Criteria createCriteria(Object objectParam) {
        return mongoHelper.createCriteria(objectParam);
    }
}
