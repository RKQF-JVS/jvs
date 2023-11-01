package cn.bctools.common.utils.graph;

import cn.bctools.common.exception.BusinessException;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.ObjectUtils;

import java.util.*;

/**
 * 图论工具类
 *
 * @Author: GuoZi
 */
@Slf4j
public class GraphUtils {

    private GraphUtils() {
    }

    /**
     * 判断有向图是否存在循环
     *
     * @param nodeList 节点关系
     * @param <T>      节点数据类型
     * @return 判断结果
     */
    public static <T> boolean hasCycle(Map<T, List<T>> nodeList) {
        if (ObjectUtils.isEmpty(nodeList)) {
            return false;
        }
        JvsGraph<T> graph = new JvsGraph<>();
        for (Map.Entry<T, List<T>> entry : nodeList.entrySet()) {
            T id = entry.getKey();
            List<T> nextIds = entry.getValue();
            graph.addNode(id);
            if (ObjectUtils.isEmpty(nextIds)) {
                continue;
            }
            for (T nextId : nextIds) {
                graph.addEdge(id, nextId);
            }
        }
        try {
            GraphUtils.getGraphRootList(graph);
            return false;
        } catch (Exception e) {
            log.info(e.getMessage());
            return true;
        }
    }

    /**
     * 获取图中的所有连通分支的根节点
     * <p>
     * 顺便校验是否存在循环
     *
     * @param graph 关系图
     * @param <T>   节点数据类型
     * @return 根节点集合
     */
    private static <T> Set<T> getGraphRootList(JvsGraph<T> graph) {
        Set<T> nodes = graph.getNodes();
        if (ObjectUtils.isEmpty(nodes)) {
            return Collections.emptySet();
        }
        // 获取图中各个连通分支的根节点
        Set<T> roots = new HashSet<>();
        Set<T> handledNodes = new HashSet<>();
        Set<T> path = new HashSet<>();
        Set<T> branchPath = new HashSet<>();
        for (T node : nodes) {
            if (handledNodes.contains(node)) {
                continue;
            }
            GraphUtils.searchRoot(graph, node, roots, path, branchPath);
            handledNodes.addAll(path);
            path.clear();
            branchPath.clear();
        }
        return roots;
    }

    /**
     * 遍历图, 寻找根节点
     *
     * @param graph       关系图
     * @param currentNode 当前节点
     * @param roots       根节点集合
     * @param path        处理过的节点
     * @param <T>         节点数据类型
     */
    private static <T> void searchRoot(JvsGraph<T> graph, T currentNode, Set<T> roots, Set<T> path, Set<T> branchPath) {
        path.add(currentNode);
        // 校验参数循环依赖
        boolean exist = !branchPath.add(currentNode);
        if (exist) {
            throw new BusinessException("关系图存在循环");
        }
        Set<T> preNodes = graph.getPreNodes(currentNode);
        if (ObjectUtils.isEmpty(preNodes)) {
            roots.add(currentNode);
            branchPath.remove(currentNode);
            return;
        }
        for (T preNode : preNodes) {
            GraphUtils.searchRoot(graph, preNode, roots, path, branchPath);
        }
        branchPath.remove(currentNode);
    }

}
