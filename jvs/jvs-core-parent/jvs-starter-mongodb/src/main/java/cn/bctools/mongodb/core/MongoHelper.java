package cn.bctools.mongodb.core;

import cn.bctools.mongodb.core.annotation.ConditionsAnnotation;
import cn.bctools.mongodb.core.annotation.Eq;
import cn.bctools.mongodb.core.annotation.No;
import cn.bctools.mongodb.core.annotation.OperatorAnnotation;
import cn.bctools.mongodb.core.service.impl.BaseMongoServiceImpl;
import cn.bctools.mongodb.core.utils.ClassUtils;
import com.mongodb.client.ListIndexesIterable;
import com.mongodb.client.model.IndexOptions;
import com.mongodb.client.model.Indexes;
import org.bson.Document;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.*;


/**
 * MongoDB操作辅助类
 *
 * @see cn.bctools.mongodb.core.service.BaseMongoService
 * @see BaseMongoServiceImpl
 */

public class MongoHelper {

    @Autowired
    public MongoTemplate mongoTemplate;

    /**
     * 缓存Class相关filed
     */
    final Map<Class, Field[]> classFieldCacheMap = new HashMap<>();
    /**
     * 缓存Field相关注解信息
     */
    final Map<Field, FieldAnnotationCache> fieldAnnotationCacheMap = new HashMap<>();
    @Autowired
    ConditionsAnnotationHandlerRegister conditionsAnnotationHandlerRegister;
    @Autowired
    OperatorAnnotationHandlerRegister operatorAnnotationHandlerRegister;


    /**
     * 功能描述: 创建一个集合
     * 同一个集合中可以存入多个不同类型的对象，我们为了方便维护和提升性能，
     * 后续将限制一个集合中存入的对象类型，即一个集合只能存放一个类型的数据
     *
     * @param name 集合名称，相当于传统数据库的表名
     * @return:void
     */
    public void createCollection(String name) {
        mongoTemplate.createCollection(name);
    }

    /**
     * 功能描述: 创建索引
     * 索引是顺序排列，且唯一的索引
     *
     * @param collectionName 集合名称，相当于关系型数据库中的表名
     * @param filedName      对象中的某个属性名
     * @return:java.lang.String
     */
    public String createIndex(String collectionName, String filedName) {
        //配置索引选项
        IndexOptions options = new IndexOptions();
        // 设置为唯一
        options.unique(true);
        //创建按filedName升序排的索引
        return mongoTemplate.getCollection(collectionName).createIndex(Indexes.ascending(filedName), options);
    }


    /**
     * 功能描述: 获取当前集合对应的所有索引的名称
     *
     * @param collectionName
     * @return:java.util.List<java.lang.String>
     */
    public List<String> getAllIndexes(String collectionName) {
        ListIndexesIterable<Document> list = mongoTemplate.getCollection(collectionName).listIndexes();
        //上面的list不能直接获取size，因此初始化arrayList就不设置初始化大小了
        List<String> indexes = new ArrayList<>();
        for (Document document : list) {
            document.entrySet().forEach((key) -> {
                //提取出索引的名称
                if (key.getKey().equals("name")) {
                    indexes.add(key.getValue().toString());
                }
            });
        }
        return indexes;
    }

    /**
     * 功能描述: 往对应的集合中插入一条数据
     *
     * @param info           存储对象
     * @param collectionName 集合名称
     * @return:void
     */
    public <T> boolean insert(T info, String collectionName) {
        return mongoTemplate.insert(info, collectionName) != null;
    }

    /**
     * 功能描述: 往对应的集合中批量插入数据，注意批量的数据中不要包含重复的id
     *
     * @param infos 对象列表
     * @return:void
     */

    public <T> boolean insertMulti(List<T> infos, String collectionName) {
        Collection<T> insert = mongoTemplate.insert(infos, collectionName);
        return insert != null && insert.size() == infos.size();
    }

    /**
     * 功能描述: 使用索引信息精确更改某条数据
     *
     * @param id             唯一键
     * @param collectionName 集合名称
     * @param updateObject   待更新的内容
     * @return:void
     */
    public <O> boolean updateById(String id, String collectionName, O updateObject) {
        return updateById(id, collectionName, updateObject, updateObject.getClass());
    }

    public <O, T> boolean updateById(String id, String collectionName, O updateObject, Class<T> entityClass) {
        return update(new Query(Criteria.where("id").is(id)), collectionName, updateObject, entityClass);
    }


    public <O> boolean update(Object objectParam, String collectionName, O updateObject) {
        return update(objectParam, collectionName, updateObject, updateObject.getClass());
    }

    public <O, T> boolean update(Object objectParam, String collectionName, O updateObject, Class<T> entityClass) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return update(query, collectionName, updateObject, entityClass);
    }

    public <O> boolean update(Query query, String collectionName, O updateObject) {
        return update(query, collectionName, updateObject, updateObject.getClass());
    }

    public <O, T> boolean update(Query query, String collectionName, O updateObject, Class<T> entityClass) {
        return update(query, createUpdate(updateObject), collectionName, entityClass);
    }

    public <T> boolean update(Query query, Update update, String collectionName, Class<T> entityClass) {
        return mongoTemplate.updateMulti(query, update, entityClass, collectionName).wasAcknowledged();
    }


    public <T> Update createUpdate(T updateObject) {
        Update update = new Update();
        ClassUtils.consumerNotNullField(updateObject, (field -> {
            try {
                String name = field.getName();
                if (!("id".equals(name) || "_id".equals(name))) update.set(name, field.get(updateObject));
            } catch (IllegalAccessException e) {
                throw new RuntimeException("服务出错");
            }
        }));
        return update;
    }


    /**
     * 功能描述: 根据id删除集合中的内容
     *
     * @param id             序列id
     * @param collectionName 集合名称
     * @param clazz          集合中对象的类型
     * @return:void
     */

    public <T> boolean deleteById(String id, Class<T> clazz, String collectionName) {
        // 设置查询条件，当id=#{id}
        Query query = new Query(Criteria.where("id").is(id));
        return deleteByQuery(collectionName, query, clazz);
    }

    /**
     * 功能描述: 根据id查询信息
     *
     * @param id             注解
     * @param clazz          类型
     * @param collectionName 集合名称
     * @return:java.util.List<T>
     */

    public <T> T selectById(String id, Class<T> clazz, String collectionName) {
        // 查询对象的时候，不仅需要传入id这个唯一键，还需要传入对象的类型，以及集合的名称
        return mongoTemplate.findById(id, clazz, collectionName);
    }


    public boolean deleteByObjectParam(String collectName, Object objectParam, Class<?> clazz) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return deleteByQuery(collectName, query, clazz);
    }


    public boolean deleteByQuery(String collectName, Query query, Class<?> clazz) {
        return mongoTemplate.remove(query, clazz, collectName).wasAcknowledged();
    }


    /**
     * 功能描述: 查询列表信息
     * 将集合中符合对象类型的数据全部查询出来
     *
     * @param collectName 集合名称
     * @param clazz       类型
     * @return:java.util.List<T>
     */
    public <T> List<T> selectList(String collectName, Class<T> clazz) {
        return selectByObjecctParam(collectName, null, clazz);
    }


    /**
     * 功能描述: 根据条件查询集合
     *
     * @param collectName 集合名称
     * @param objectParam 查询条件
     * @param clazz       对象类型
     * @return:java.util.List<T>
     */
    public <T> List<T> selectByObjecctParam(String collectName, Object objectParam, Class<T> clazz) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return selectByQuery(collectName, query, clazz);

    }


    /**
     * 功能描述: 根据条件查询集合
     *
     * @param collectName 集合名称
     * @param query       查询条件
     * @param clazz       对象类型
     * @return
     */
    public <T> List<T> selectByQuery(String collectName, Query query, Class<T> clazz) {
        return mongoTemplate.find(query, clazz, collectName);
    }


    public <T> T selectOneByObjectParam(String collectName, Object objectParam, Class<T> clazz) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return selectOneByQuery(collectName, query, clazz);
    }


    public <T> T selectOneByQuery(String collectName, Query query, Class<T> clazz) {
        return mongoTemplate.findOne(query, clazz, collectName);
    }

    public long countByObjectParam(String collectName, Object objectParam) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return countByQuery(collectName, query);
    }


    public long countByQuery(String collectName, Query query) {
        return mongoTemplate.count(query, collectName);
    }

    public <T> Page<T> pageByObjectParam(String collectName, Page<T> page, Object objectParam, Class<T> clazz) {
        Query query = new Query();
        quqeyAddCriteria(query, objectParam);
        return this.pageByQuery(collectName, page, query, clazz);
    }

    /**
     * @param collectName 集合名字
     * @param page        page 里面包含分页的相关信息，数据查询完成后，会赋值到page的data属性中
     * @param query       条件  里面不要包含分页条件
     * @param clazz       接受查询数据的类型class
     * @param <T>
     * @return
     */
    public <T> Page<T> pageByQuery(String collectName, Page<T> page, Query query, Class<T> clazz) {
        long count = this.countByQuery(collectName, query);
        page.setTotal(count);
        if (page.currentIsHaveData()) {
            query.skip(page.getSkip()).limit(page.getSize());
            List<T> data = this.selectByQuery(collectName, query, clazz);
            page.setData(data);
        }
        return page;
    }

    /**
     * aggregate分页
     *
     * @param conditions 已经写好的条件 不要添加count和skip limit
     * @param page       分页结果,请事先填充正确的MongoDBPage#current,MongoDBPage#size
     * @param clazz      接受结果的类型
     * @param <T>
     * @return
     */
    public <T> Page<T> aggregatePage(List<AggregationOperation> conditions, String collectionName, Page<T> page, Class<T> clazz) {
        return aggregatePage(conditions, null, collectionName, page, clazz);
    }

    public <T> Page<T> aggregatePage(List<AggregationOperation> conditions, Sort sort, String collectionName, Page<T> page, Class<T> clazz) {
        conditions.add(Aggregation.count().as("count"));
        List<HashMap> hashMaps = aggregateData(Aggregation.newAggregation(conditions), collectionName, HashMap.class);
        Long total = hashMaps.isEmpty() ? 0L : Long.valueOf(String.valueOf(hashMaps.get(0).get("count")));
        page.setTotal(total);
        if (page.currentIsHaveData()) {
            conditions.remove(conditions.size() - 1);
            if (sort != null) conditions.add(Aggregation.sort(sort));
            conditions.add(Aggregation.skip(page.getSkip()));
            conditions.add(Aggregation.limit(page.getSize()));
            Aggregation aggregation = Aggregation.newAggregation(conditions);
            List<T> ts = aggregateData(aggregation, collectionName, clazz);
            page.setData(ts);
        }
        return page;
    }


    public <T> AggregationResults<T> aggregate(Aggregation aggregation, String collectionName, Class<T> outputType) {
        return mongoTemplate.aggregate(aggregation, collectionName, outputType);
    }


    public <T> List<T> aggregateData(Aggregation aggregation, String collectionName, Class<T> outputType) {
        return aggregate(aggregation, collectionName, outputType).getMappedResults();
    }


    public void quqeyAddCriteria(Query query, Object objectParam) {
        query.addCriteria(createCriteria(objectParam));
    }

    /**
     * 根据object和它的field上面的注解构造成Criteria
     *
     * @param objectParam
     * @return
     */
    public Criteria createCriteria(Object objectParam) {
        Criteria criteria = new Criteria();
        if (objectParam == null)
            return criteria;
        Field[] fields = classFieldCacheMap.computeIfAbsent(objectParam.getClass(), clazz -> {
            Field[] declaredFields = clazz.getDeclaredFields();
            for (Field field : declaredFields) {
                field.setAccessible(true);
            }
            return declaredFields;
        });

        for (Field field : fields) {
            FieldAnnotationCache fieldAnnotationCache = fieldAnnotationCacheMap.computeIfAbsent(field, f -> createFieldAnnotationCache(f));
            OperatorAnnotationHandler operatorAnnotationHandler = operatorAnnotationHandlerRegister.getHandler(fieldAnnotationCache.getOperatorAnnotation());
            ConditionsAnnotationHandler conditionsAnnotationHandler = conditionsAnnotationHandlerRegister.getHandler(fieldAnnotationCache.getConditionsAnnotation());
            String filedName = fieldAnnotationCache.getFiledName();
            if (!fieldAnnotationCache.isNo() && conditionsAnnotationHandler == null) {
                conditionsAnnotationHandler = conditionsAnnotationHandlerRegister.getHandler(Eq.class);
            }
            if (conditionsAnnotationHandler != null) {
                try {
                    Object value = field.get(objectParam);
                    criteria = conditionsAnnotationHandler.handler(criteria.and(filedName), value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
            if (operatorAnnotationHandler != null) {
                criteria = operatorAnnotationHandler.handler(criteria, new Criteria());
            }
        }

        return criteria;
    }

    protected FieldAnnotationCache createFieldAnnotationCache(Field field) {
        Annotation[] annotations = field.getDeclaredAnnotations();
        Annotation operatorAnnotation = null;
        Annotation conditionsAnnotation = null;
        for (Annotation annotation : annotations) {
            if (operatorAnnotation != null && conditionsAnnotation != null)
                continue;
            if (operatorAnnotation == null && annotation.annotationType().isAnnotationPresent(OperatorAnnotation.class))
                operatorAnnotation = annotation;
            if (conditionsAnnotation == null && annotation.annotationType().isAnnotationPresent(ConditionsAnnotation.class))
                conditionsAnnotation = annotation;
        }
        FieldAnnotationCache fieldAnnotationCache = new FieldAnnotationCache();
        fieldAnnotationCache.setOperatorAnnotation(operatorAnnotation != null ? operatorAnnotation.annotationType() : null);
        String filedName = field.getName();
        if (conditionsAnnotation != null) {
            fieldAnnotationCache.setConditionsAnnotation(conditionsAnnotation.annotationType());
            try {
                String v = (String) conditionsAnnotation.annotationType().getMethod("value").invoke(conditionsAnnotation);
                if (!Objects.equals(v, "")) {
                    filedName = v;
                }
            } catch (Exception e) {

            }
        }
        fieldAnnotationCache.setFiledName(filedName);
        fieldAnnotationCache.setNo(field.isAnnotationPresent(No.class));
        return fieldAnnotationCache;

    }


    public MongoTemplate getMongoTemplate() {
        return mongoTemplate;
    }
}