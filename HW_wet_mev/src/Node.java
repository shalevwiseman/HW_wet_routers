import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    public int id;
    public int num_of_nodes;
    private HashMap<Integer, Neighbor> neighbors;
    private Vector<Pair<Integer, Double>> vectorOfEdgesAndWeights;


    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbors = new HashMap<>();
        vectorOfEdgesAndWeights = new Vector<Pair<Integer, Double>>();
    }

    public void addPair(Integer key, Double value) {
        Pair<Integer, Double> pair = new Pair<Integer, Double>(key, value);
        vectorOfEdgesAndWeights.add(pair);
    }
    public void updateEdge(int nodeID, Double weight){
        // update the edge in vectorOfEdgesAndWeights
        neighbors.get(nodeID).setWeight(weight);
    }

     public void createVector(){
         Iterator<Integer> iterator = neighbors.keySet().iterator();
         while (iterator.hasNext()){
             Integer neighborName = iterator.next();
             Neighbor neighbor = neighbors.get(neighborName);
             Double weight = neighbor.getWeight();
             this.addPair(neighborName, weight);
         }
     }

    public void print_graph(){
        return;
    }


    public void addNeighbor(int node_name, double weight, int sent_port, int get_port) {
        this.neighbors.put(node_name, new Neighbor(node_name, weight, sent_port, get_port));
    }

    public HashMap<Integer, Neighbor> getNeighbors() {
        return this.neighbors;
    }
}