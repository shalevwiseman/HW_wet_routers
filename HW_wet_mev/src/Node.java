import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;

public class Node extends Thread {
    public int id;
    public int num_of_nodes;
    private String IP;
    private HashMap<Integer, Neighbor> neighbors;
    private Vector<Pair<Integer, Double>> vectorOfEdgesAndWeights;
    private List<Integer> getPorts;
    private HashMap<Integer, ServerSocket> ServersSocket;
    private List<Integer> sendPorts;
    private HashMap<Integer, Socket> sendSockets;
    private List<Pair<Integer, Double>> edeges;




    public Node(int id, int num_of_nodes) throws IOException {
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbors = new HashMap<>();
        this.edeges = new ArrayList<>();
        vectorOfEdgesAndWeights = new Vector<Pair<Integer, Double>>();
        getPorts = new ArrayList<Integer>();
        this.IP = "localhost";
        this.sendPorts = new ArrayList<>();
        this.sendSockets = new HashMap<>();
        this.ServersSocket = new HashMap<>();


    }

    public void addPair(Integer key, Double value) {
        Pair<Integer, Double> pair = new Pair<Integer, Double>(key, value);
        vectorOfEdgesAndWeights.add(pair);
        edeges.add(pair);
    }
    public void updateEdge(int nodeID, Double weight){
        // update the edge in vectorOfEdgesAndWeights
        neighbors.get(nodeID).setWeight(weight);
        //sendMessage(weight.toString());

        for (Pair<Integer,Double> pair : edeges){
            if (nodeID == pair.getKey()){
                pair.setValue(weight);
            }
        }
    }

    public void processMessage(String message) {
        System.out.println("Node " + this.id + " received message: " + message);
        // other processing logic goes here
    }



    @Override
    public void run(){
        boolean stop = false;

            new Thread(() -> {
                while (!stop) {
                    for (ServerSocket serverSocket : ServersSocket.values()) {
                        //system.out.println("Node" + this.id + "-" + serverSocket + "again2");

                            try {

                                Socket socket = serverSocket.accept();// start listen

                                new Thread(() -> {
                                    try {

                                        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                                        int id = in.readInt();
                                        int len = in.readInt();
                                        System.out.println("id: " + id + " len: "+ len);


                                        byte[] buffer = new byte[len];

                                        boolean end = false;
                                        StringBuilder dataString = new StringBuilder(len);
                                        int totalBytesRead = 0;
                                        // Read function might not be able to read all data in one call. so, we need to cal the read() in while loop:

                                        while (!end){
                                            int currentBytesRead = in.read(buffer);
                                            totalBytesRead = currentBytesRead + totalBytesRead;
                                            if (totalBytesRead <= len){
                                                dataString.append(new String(buffer, 0, currentBytesRead, StandardCharsets.UTF_8));
                                            }else {
                                                dataString.append(new String(buffer, 0, len - totalBytesRead + currentBytesRead, StandardCharsets.UTF_8));
                                            }
                                            if (dataString.length() >= len){
                                                end = true;
                                            }
                                        }

                                        processMessage(dataString.toString());
                                        socket.close();
                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                    //int dataFromNeighbor = socket.getInputStream().read(buffer);

                                        //String message = new String(buffer, 0, dataFromNeighbor, "UTF-8");
                                        //System.out.println(this.id + "after data " + message);


                                }).start();
                            }catch (IOException e) {
                                e.printStackTrace();
                            }



                    }
                }
            }).start();


        }



    // Sends a message to a specified neighbor (identified by receiverID)
    public void sendMessage(String message) {
        for (Neighbor neighbor : neighbors.values()) {

            // Retrieve the neighbor object from the list of neighbors

            try {
                int port = neighbor.getSent_port();
                Socket socket = this.sendSockets.get(port);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                out.writeInt(this.id);
                out.writeInt(dataInBytes.length);
                out.write(dataInBytes);
                out.flush();
                //socket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                // Print the stack trace if an exception occurs
                e.printStackTrace();
            }
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



    public void clientsUp() throws IOException {
        for (Integer port : sendPorts){
            Socket socket = new Socket(this.IP,port);//create a socket to this neighbor
            this.sendSockets.put(port, socket);
        }
    }
    public void serversUp() throws IOException {
        for (Integer port : getPorts){
            ServerSocket serverSocket = new ServerSocket(port);//create a Server socket to this neighbor
            ServersSocket.put(port, serverSocket);
        }
    }

    public void addNeighbor(int node_name, double weight, int sent_port, int get_port) throws IOException {
        this.neighbors.put(node_name, new Neighbor(node_name, weight, sent_port, get_port));

        getPorts.add(get_port);//add the get port to the list
        this.sendPorts.add(sent_port);//add the 'send port' to the list
        addPair(node_name,weight);

    }

    public String getVectorAsString(){
        String s = null;
        for (Pair<Integer, Double> pair : edeges){
            s += pair.toString();
        }

        return s;
    }

    public HashMap<Integer, Neighbor> getNeighbors() {
        return this.neighbors;
    }
}