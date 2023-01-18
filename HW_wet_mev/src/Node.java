import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    public int id;
    public int num_of_nodes;
    private HashMap<Integer, Neighbor> neighbors;
    private Vector<Pair<Integer, Double>> vectorOfEdgesAndWeights;
    private List<Integer> ports;


    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbors = new HashMap<>();
        vectorOfEdgesAndWeights = new Vector<Pair<Integer, Double>>();
        ports = new ArrayList<Integer>();
    }

    public void addPair(Integer key, Double value) {
        Pair<Integer, Double> pair = new Pair<Integer, Double>(key, value);
        vectorOfEdgesAndWeights.add(pair);
    }
    public void updateEdge(int nodeID, Double weight){
        // update the edge in vectorOfEdgesAndWeights
        neighbors.get(nodeID).setWeight(weight);
        sendMessage(nodeID, weight.toString());
    }

    public void processMessage(String message) {
        System.out.println("Node " + this.id + " received message: " + message);
        // other processing logic goes here
    }

    // Sends a message to a specified neighbor (identified by receiverID)
    public void sendMessage(int receiverID, String message) {
        // Retrieve the neighbor object from the list of neighbors
        Neighbor receiver = this.neighbors.get(receiverID);
        try {
            // Create a socket connection to the neighbor's IP address and port
            Socket socket = new Socket(receiver.getIpAddress(), receiver.getSent_port());
            // Get the output stream from the socket to write the message
            OutputStream out = socket.getOutputStream();
            // Write the message to the output stream
            out.write(message.getBytes());
            // Flush the output stream to ensure the message is sent
            out.flush();
            // Close the socket connection
            socket.close();
        } catch (IOException e) {
            // Print the stack trace if an exception occurs
            e.printStackTrace();
        }
    }


    public void receiveMessage() {

        for (Integer port : ports) { // Iterating through all ports in the ports list
            Thread thread = new Thread(() -> { // Creating a new thread for each port
                try {
                    ServerSocket serverSocket = new ServerSocket(port); // Create a server socket on the port
                    while (true) {
                        Socket socket = serverSocket.accept(); // Wait for incoming connection
                        InputStream in = socket.getInputStream();
                        byte[] messageBytes = new byte[1024];
                        in.read(messageBytes); // Read the message from the input stream
                        String message = new String(messageBytes); // Convert the message to string
                        processMessage(message); // Process the message
                        socket.close(); // Close the socket
                    }
                } catch (IOException e) {
                    e.printStackTrace(); // Printing the error if any
                }
            });
            thread.start(); // Starting the thread
        }
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
        ports.add(get_port);

    }

    public HashMap<Integer, Neighbor> getNeighbors() {
        return this.neighbors;
    }
}