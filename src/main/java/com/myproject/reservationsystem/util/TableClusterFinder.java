package com.myproject.reservationsystem.util;

import com.myproject.reservationsystem.entity.RestaurantTable;

import java.util.*;

public class TableClusterFinder {

    public static List<List<RestaurantTable>> findAllTableClusters(List<RestaurantTable> allTables, int numOfCustomer) {
        Set<Set<Integer>> uniqueClusterIds = new HashSet<>(); // Track unique clusters using table IDs
        List<List<RestaurantTable>> clusters = new ArrayList<>();

        for (RestaurantTable table : allTables) {
            Set<Integer> clusterIds = new TreeSet<>();
            List<RestaurantTable> cluster = new ArrayList<>();
            findClusterDFS(table, cluster, clusterIds, numOfCustomer);

            // Discard duplicate clusters
            if (!uniqueClusterIds.contains(clusterIds) && !cluster.isEmpty()) {
                uniqueClusterIds.add(clusterIds); // Store cluster as a unique set of IDs
                clusters.add(cluster);
            }
        }
        return clusters;
    }

    private static void findClusterDFS(RestaurantTable table, List<RestaurantTable> cluster, Set<Integer> clusterIds, int numOfCustomer) {
        Stack<RestaurantTable> stack = new Stack<>();
        stack.push(table);
        Set<Integer> visited = new HashSet<>();
        int totalCapacity = 0;

        while (!stack.isEmpty() && totalCapacity < numOfCustomer) {
            RestaurantTable current = stack.pop();
            if (!visited.contains(current.getId())) {
                visited.add(current.getId());
                clusterIds.add(current.getId()); // Store table ID in the cluster set
                cluster.add(current);
                totalCapacity += current.getCapacity();

                for (RestaurantTable adj : current.getAdjacentTables()) {
                    if (!visited.contains(adj.getId())) {
                        stack.push(adj);
                    }
                }
            }
        }

        if (totalCapacity < numOfCustomer) {
            cluster.clear();
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
