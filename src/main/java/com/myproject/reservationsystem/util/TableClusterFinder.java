package com.myproject.reservationsystem.util;

import com.myproject.reservationsystem.entity.RestaurantTable;

import java.util.*;

public class TableClusterFinder {

    public static List<List<RestaurantTable>> findAllTableClusters(List<RestaurantTable> allTables) {
        List<List<RestaurantTable>> clusters = new ArrayList<>();
        Set<Integer> visited = new HashSet<>();

        for (RestaurantTable table : allTables) {
            if (!visited.contains(table.getId())) {
                List<RestaurantTable> cluster = new ArrayList<>();
                findClusterDFS(table, visited, cluster);
                clusters.add(cluster);
            }
        }
        return clusters;
    }

    private static void findClusterDFS(RestaurantTable table, Set<Integer> visited, List<RestaurantTable> cluster) {
        Stack<RestaurantTable> stack = new Stack<>();
        stack.push(table);

        while (!stack.isEmpty()) {
            RestaurantTable current = stack.pop();
            if (!visited.contains(current.getId())) {
                visited.add(current.getId());
                cluster.add(current);
                for (RestaurantTable adj : current.getAdjacentTables()) {
                    if (!visited.contains(adj.getId())) {
                        stack.push(adj);
                    }
                }
            }
        }
    }

    public static List<RestaurantTable> findValidCluster(List<List<RestaurantTable>> clusters, int numOfPeople) {
        for (List<RestaurantTable> cluster : clusters) {
            int totalCapacity = cluster.stream().mapToInt(RestaurantTable::getCapacity).sum();
            if (totalCapacity >= numOfPeople) {
                return cluster;
            }
        }
        return null;
    }
}
