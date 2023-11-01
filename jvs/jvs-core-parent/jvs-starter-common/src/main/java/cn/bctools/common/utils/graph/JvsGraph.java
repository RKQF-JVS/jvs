package cn.bctools.common.utils.graph;

import java.util.*;

/**
 * 有向图
 *
 * @Author: GuoZi
 */
public class JvsGraph<T> {

    private final Set<T> nodes = new HashSet<>();
    private final Map<T, Set<T>> preEdges = new HashMap<>();
    private final Map<T, Set<T>> nextEdges = new HashMap<>();

    /**
     * 获取图中所有节点
     *
     * @return 节点集合
     */
    public Set<T> getNodes() {
        return this.nodes;
    }

    /**
     * 添加节点
     *
     * @param node 节点
     * @return 节点是否存在
     */
    public boolean addNode(T node) {
        return nodes.add(node);
    }

    /**
     * 添加边
     *
     * @param from 起点节点
     * @param to   目标节点
     * @return 边是否存在
     */
    public boolean addEdge(T from, T to) {
        addNode(from);
        addNode(to);
        Set<T> preNodes = preEdges.computeIfAbsent(to, v -> new HashSet<>());
        preNodes.add(from);
        Set<T> nextNodes = nextEdges.computeIfAbsent(from, v -> new HashSet<>());
        return nextNodes.add(to);
    }

    /**
     * 删除节点
     *
     * @param node 节点
     * @return 删除的节点是否存在
     */
    public boolean removeNode(T node) {
        boolean exist = nodes.remove(node);
        if (!exist) {
            return false;
        }
        Set<T> preNodes = preEdges.remove(node);
        Set<T> nextNodes = nextEdges.remove(node);
        if (preNodes != null) {
            for (T preNode : preNodes) {
                nextEdges.get(preNode).remove(node);
            }
        }
        if (nextNodes != null) {
            for (T nextNode : nextNodes) {
                preEdges.get(nextNode).remove(node);
            }
        }
        return true;
    }

    /**
     * 删除边
     *
     * @param from 起点节点
     * @param to   目标节点
     * @return 删除的边是否存在
     */
    public boolean removeEdge(T from, T to) {
        if (!nodes.contains(from) || !nodes.contains(to)) {
            return false;
        }
        preEdges.computeIfAbsent(to, v -> new HashSet<>()).remove(from);
        return nextEdges.computeIfAbsent(from, v -> new HashSet<>()).remove(to);
    }

    /**
     * 获取后继节点集合
     *
     * @param node 当前节点
     * @return 节点集合
     */
    public Set<T> getNextNodes(T node) {
        Set<T> postNodes = nextEdges.get(node);
        if (postNodes == null) {
            postNodes = Collections.emptySet();
        }
        return postNodes;
    }

    /**
     * 获取前序节点集合
     *
     * @param node 当前节点
     * @return 节点集合
     */
    public Set<T> getPreNodes(T node) {
        Set<T> preNodes = preEdges.get(node);
        if (preNodes == null) {
            preNodes = Collections.emptySet();
        }
        return preNodes;
    }

}
