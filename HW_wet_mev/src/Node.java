import java.net.*;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    public int id;
    public int num_of_nodes;
    private String IP;
    private HashMap<Integer, Neighbor> neighbors;
    private Vector<Pair<Integer, Double>> vectorOfEdgesAndWeights;
    private List<Integer> ports;




    public Node(int id, int num_of_nodes) {
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbors = new HashMap<>();
        vectorOfEdgesAndWeights = new Vector<Pair<Integer, Double>>();
        ports = new ArrayList<Integer>();
        this.IP = "127.0.0.1";


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
    @Override
    public void run(){
        boolean stop = false;



        for (Integer port : this.ports){
            // Creates a process for each port to listen in parallel
            new Thread(() -> {
                try {
                    System.out.println("Node" + this.id + "-" + port);
                    ServerSocket serverSocket = new ServerSocket(port);
                    System.out.println("Node" + this.id + "-" + port + "again1");
                    while (!stop){
                        System.out.println("Node" + this.id + "-" + port + "again2");
                        Socket socket = serverSocket.accept();// start listen
                        System.out.println("Node" + this.id + "-" + port + "again3");
                        new Thread(() ->{
                            try {
                                InputStream in = socket.getInputStream();
                                byte[] massageBytes = new byte[1024];
                                in.read(massageBytes);
                                String massage = new String(massageBytes);
                                processMessage(massage);
                                socket.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }).run();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }).run();


        }

    }

    // Sends a message to a specified neighbor (identified by receiverID)
    public void sendMessage(int receiverID, String message) {
        // Retrieve the neighbor object from the list of neighbors
        Neighbor receiver = this.neighbors.get(receiverID);
        try {
            // Create a socket connection to the neighbor's IP address and port
            Socket socket = new Socket(this.IP, receiver.getSent_port());
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