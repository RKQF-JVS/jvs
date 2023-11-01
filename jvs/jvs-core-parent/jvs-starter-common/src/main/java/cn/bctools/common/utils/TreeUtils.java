package cn.bctools.common.utils;

import cn.bctools.common.entity.po.TreePo;
import cn.bctools.common.utils.function.Get;
import cn.hutool.core.lang.tree.Tree;
import cn.hutool.core.lang.tree.TreeNodeConfig;
import cn.hutool.core.lang.tree.TreeUtil;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ObjectUtils;

import javax.validation.constraints.NotNull;
import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * <树形工具>
 *
 * @author auto
 **/
public class TreeUtils {

    /**
     * 将对象数据转树操作
     *
     * @param list   树节点集合
     * @param rootId 根节点id
     * @return 树形结构集合
     */
    public static List<Tree<Object>> tree(List<TreePo> list, String rootId) {
        String name = Get.name(TreePo::getSort);
        return TreeUtil.build(list, rootId, new TreeNodeConfig().setWeightKey(name), (treeNode, tree) -> {
            tree.setId(treeNode.getId());
            tree.setParentId(treeNode.getParentId());
            tree.setName(treeNode.getName());
            tree.setWeight(treeNode.getSort());
            tree.putExtra("extend", treeNode.getExtend());
            tree.putExtra("isNew", treeNode.getIsNew());
        });
    }

    /**
     * 单列集合转树形结构(单个根节点)
     *
     * @param nodeList     节点集合(需要有父级节点的标识)
     * @param rootKey      根节点标识
     * @param getKey       获取当前节点标识
     * @param getParentKey 获取父级节点标识
     * @param setChildList 记录子节点
     * @param <T>          节点类型
     * @param <K>          key类型
     * @return 所有子级的key集合
     */
    public static <T, K> T list2Tree(Collection<T> nodeList,
                                     @NotNull K rootKey,
                                     @NotNull Function<T, K> getKey,
                                     @NotNull Function<T, K> getParentKey,
                                     @NotNull BiConsumer<T, List<T>> setChildList) {
        List<T> rootList = list2Tree(nodeList, Collections.singletonList(rootKey), getKey, getParentKey, setChildList);
        if (ObjectUtils.isEmpty(rootList)) {
            return null;
        }
        return rootList.get(0);
    }

    /**
     * 单列集合转树形结构(多个根节点)
     *
     * @param nodeList     节点集合(需要有父级节点的标识)
     * @param rootKeyList  根节点标识集合
     * @param getKey       获取当前节点标识
     * @param getParentKey 获取父级节点标识
     * @param setChildList 记录子节点
     * @param <T>          节点类型
     * @param <K>          key类型
     * @return 所有子级的key集合
     */
    public static <T, K> List<T> list2Tree(Collection<T> nodeList,
                                           @NotNull Collection<K> rootKeyList,
                                           @NotNull Function<T, K> getKey,
                                           @NotNull Function<T, K> getParentKey,
                                           @NotNull BiConsumer<T, List<T>> setChildList) {
        if (ObjectUtils.isEmpty(nodeList)) {
            // 节点为空
            return Collections.emptyList();
        }
        T next = nodeList.iterator().next();
        return list2Tree(nodeList, rootKeyList, getKey, getParentKey, setChildList, next instanceof Comparable);
    }

    /**
     * 单列集合转树形结构(多个根节点)
     *
     * @param nodeList     节点集合(需要有父级节点的标识)
     * @param rootKeyList  根节点标识集合
     * @param getKey       获取当前节点标识
     * @param getParentKey 获取父级节点标识
     * @param setChildList 记录子节点
     * @param sorted       是否需要排序
     * @param <T>          节点类型
     * @param <K>          key类型
     * @return 所有子级的key集合
     */
    private static <T, K> List<T> list2Tree(Collection<T> nodeList,
                                            @NotNull Collection<K> rootKeyList,
                                            @NotNull Function<T, K> getKey,
                                            @NotNull Function<T, K> getParentKey,
                                            @NotNull BiConsumer<T, List<T>> setChildList,
                                            boolean sorted) {
        if (ObjectUtils.isEmpty(nodeList)) {
            // 节点为空
            return Collections.emptyList();
        }
        Stream<T> rootStream = nodeList.stream().filter(node -> rootKeyList.contains(getKey.apply(node)));
        if (sorted) {
            rootStream = rootStream.sorted();
        }
        List<T> rootList = rootStream.collect(Collectors.toList());
        if (ObjectUtils.isEmpty(rootList)) {
            // 找不到根节点
            return Collections.emptyList();
        }
        Stream<T> stream = nodeList.stream().filter(node -> Objects.nonNull(getParentKey.apply(node)));
        if (sorted) {
            stream = stream.sorted();
        }
        Map<K, List<T>> map = stream.collect(Collectors.groupingBy(getParentKey));
        for (T node : nodeList) {
            K key = getKey.apply(node);
            List<T> child = map.get(key);
            if (Objects.isNull(child)) {
                child = Collections.emptyList();
            }
            setChildList.accept(node, child);
        }
        return rootList;
    }

    /**
     * 树形结构转单列集合(会进行去重)
     *
     * @param root         根节点
     * @param getChildList 获取子节点集合
     * @param <T>          节点类型
     * @return 所有节点的单列集合
     */
    public static <T> List<T> tree2List(T root, @NotNull Function<T, List<T>> getChildList) {
        if (Objects.isNull(root)) {
            // 节点为空
            return Collections.emptyList();
        }
        Set<T> set = new LinkedHashSet<>(2 << 5);
        set.add(root);
        Deque<T> queue = new ArrayDeque<>(2 << 5);
        queue.add(root);
        while (!queue.isEmpty()) {
            T node = queue.poll();
            List<T> childList = getChildList.apply(node);
            if (ObjectUtils.isEmpty(childList)) {
                continue;
            }
            for (T childNode : childList) {
                boolean notExist = set.add(childNode);
                if (notExist) {
                    queue.add(childNode);
                }
            }
        }
        return new ArrayList<>(set);
    }

    /**
     * 根据上下级关系遍历沿途的节点(包含root节点)
     *
     * @param nodeList     节点集合(需要有父级节点的标识)
     * @param rootKey      根节点数据
     * @param getKey       获取当前节点标识
     * @param getParentKey 获取父级节点标识
     * @param <T>          节点类型
     * @param <K>          key类型
     * @return 所有子级的key集合
     */
    public static <T, K> List<T> getListPassingBy(Collection<T> nodeList,
                                                  @NotNull K rootKey,
                                                  @NotNull Function<T, K> getKey,
                                                  @NotNull Function<T, K> getParentKey) {
        if (ObjectUtils.isEmpty(nodeList)) {
            return Collections.emptyList();
        }
        Map<K, List<T>> keyMap = nodeList.stream()
                .filter(k -> ObjectNull.isNotNull(getKey.apply(k)))
                .collect(Collectors.groupingBy(getKey));
        // 防止重复遍历key
        Set<K> passByKey = new HashSet<>(nodeList.size());
        List<T> passByNode = new ArrayList<>(nodeList.size());
        Deque<K> keyQueue = new ArrayDeque<>(nodeList.size());
        keyQueue.add(rootKey);
        passByKey.add(rootKey);
        List<T> nodes = keyMap.get(rootKey);
        if (ObjectUtils.isNotEmpty(nodes)) {
            passByNode.addAll(nodes);
        }
        Map<K, List<T>> parentKeyMap = nodeList.stream()
                .filter(k -> ObjectNull.isNotNull(getParentKey.apply(k)))
                .collect(Collectors.groupingBy(getParentKey));
        while (!keyQueue.isEmpty()) {
            K key = keyQueue.pop();
            List<T> childList = parentKeyMap.get(key);
            if (ObjectUtils.isEmpty(childList)) {
                continue;
            }
            passByNode.addAll(childList);
            for (T t : childList) {
                K childKey = getKey.apply(t);
                boolean notExist = passByKey.add(childKey);
                if (notExist) {
                    keyQueue.add(childKey);
                }
            }
        }
        return passByNode;
    }

    /**
     * 从根节点开始, 寻找目标节点
     *
     * @param nodeList     节点集合(需要有父级节点的标识)
     * @param targetKey    目标节点数据
     * @param getKey       获取当前节点标识
     * @param getParentKey 获取父级节点标识
     * @param <T>          节点类型
     * @param <K>          key类型
     * @return 沿经的所有节点(包含根节点与目标节点)
     */
    public static <T, K> List<T> getSearchPath(Collection<T> nodeList,
                                               @NotNull K targetKey,
                                               @NotNull Function<T, K> getKey,
                                               @NotNull Function<T, K> getParentKey) {
        if (ObjectUtils.isEmpty(nodeList)) {
            return Collections.emptyList();
        }
        Map<K, List<T>> keyMap = nodeList.stream()
                .filter(k -> ObjectNull.isNotNull(getKey.apply(k)))
                .collect(Collectors.groupingBy(getKey));
        List<T> endNodeList = keyMap.get(targetKey);
        if (ObjectUtils.isEmpty(targetKey) || CollectionUtils.isEmpty(endNodeList) || endNodeList.size() != 1) {
            return Collections.emptyList();
        }
        Set<K> keySet = new HashSet<>();
        List<T> path = new ArrayList<>();
        T currentNode = endNodeList.get(0);
        path.add(currentNode);
        keySet.add(targetKey);
        while (true) {
            K parentKey = getParentKey.apply(currentNode);
            boolean notExist = keySet.add(parentKey);
            if (!notExist) {
                // 进入死循环, 直接返回空集合
                return Collections.emptyList();
            }
            List<T> parents = keyMap.get(parentKey);
            if (ObjectUtils.isEmpty(parents) || parents.size() != 1) {
                break;
            }
            currentNode = parents.get(0);
            path.add(currentNode);
        }
        // 调换顺序, 从起始节点开始
        Collections.reverse(path);
        return path;
    }

}

