import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.io.*;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicReference;

public class Node extends Thread {
    public int id;
    public int num_of_nodes;
    private int nodeCounter;
    private HashSet<Integer> receiveMessageIds;
    private String IP;
    private HashMap<Integer, Neighbor> neighbors;
    private Vector<Pair<Integer, Double>> vectorOfEdgesAndWeights;
    private List<Integer> getPorts;
    private HashMap<Integer, ServerSocket> ServersSocket;
    private List<Integer> sendPorts;
    private HashMap<Integer, Socket> sendSockets;
    private List<Pair<Integer, Double>> edeges;
    Matrix matrix;




    public Node(int id, int num_of_nodes) throws IOException {
        this.id = id;
        this.num_of_nodes = num_of_nodes;
        this.neighbors = new HashMap<>();
        this.edeges = new ArrayList<>();
        this.nodeCounter = 0;
        this.receiveMessageIds = new HashSet<>();
        vectorOfEdgesAndWeights = new Vector<Pair<Integer, Double>>();
        getPorts = new ArrayList<Integer>();
        this.IP = "localhost";
        this.sendPorts = new ArrayList<>();
        this.sendSockets = new HashMap<>();
        this.ServersSocket = new HashMap<>();
        matrix = new Matrix(num_of_nodes);



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

    public synchronized void uniqueMessageCounter(Integer id){
        if (!this.receiveMessageIds.contains(id)){

            this.nodeCounter ++;

            this.receiveMessageIds.add(id);

            //System.out.println("node " + this.id + " received message from node " + id + " and got from:" + this.receiveMessageIds.size() + " nodes");


        }
    }

    public void processMessage(int nodeId, String message) {
        matrix.updateMat(nodeId, message);
        //System.out.println(matrix.toString());
        //System.out.println("node: " + this.id + " process");

        // other processing logic goes here
    }



    @Override
    public void run(){
        AtomicReference<AtomicBoolean> stop = new AtomicReference<>(new AtomicBoolean(false));



                    for (ServerSocket serverSocket : ServersSocket.values()) {
                        new Thread(() -> {
                            while (receiveMessageIds.size() < num_of_nodes) {

                            try {

                                Socket socket = serverSocket.accept();// start listen, if we pass this code line its mean that we got a message

                                new Thread(() -> {
                                    try {

                                        DataInputStream in = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                                        int id = in.readInt();
                                        int len = in.readInt();



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

                                        //System.out.println(this.id + " got message Org from: " + id);

                                        //adding the ID that i got the message from to the set in a sync func
                                        uniqueMessageCounter(id);


                                        processMessage(id, dataString.toString());

                                        sendMessage(dataString.toString(), id);

                                        //if (!receiveMessageIds.contains(id)){}
                                        TimeUnit.SECONDS.sleep(2);
                                        socket.close();



                                    }
                                    catch (IOException e) {
                                        e.printStackTrace();
                                    } catch (InterruptedException e) {
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
                }).start();


            }


        }



    // Sends a message to a specified neighbor (identified by receiverID)
    public void sendMessage(String message, int id) {
        for (Neighbor neighbor : neighbors.values()) {

            // Retrieve the neighbor object from the list of neighbors


            try {
                Socket socket = new Socket(this.IP,neighbor.getSent_port());
                int port = neighbor.getSent_port();
                //Socket socket = this.sendSockets.get(port);
                DataOutputStream out = new DataOutputStream(socket.getOutputStream());
                byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                out.writeInt(id);
                out.writeInt(dataInBytes.length);
                out.write(dataInBytes);
                out.flush();
                //System.out.println("node: " + this.id + " from: " + id + " send to " + neighbor.getNode_name() + " message " + message);
                //socket.getOutputStream().write(message.getBytes());
            } catch (IOException e) {
                // Print the stack trace if an exception occurs
                e.printStackTrace();
            }
        }
    }






    public void createVector(){


        Double notIn = Double.valueOf(-1);
        for (int i = 1; i < num_of_nodes + 1; i++){
            if (neighbors.containsKey(i)){
                addPair(i, neighbors.get(i).getWeight());
            }
            else {
                addPair(i, notIn);
            }
        }
     }

    public void print_graph() {
        try{
            TimeUnit.SECONDS.sleep(2);
        }
        catch (InterruptedException e){
            e.printStackTrace();
        }
        System.out.println(matrix.toString());
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


    }

    public String getVectorAsString(){
        StringBuilder vector = new StringBuilder();
        String s = null;
        for (Pair<Integer, Double> pair : edeges){
            vector.append(pair.toString());
            vector.append(",");
            vector.append(" ");
            s += pair.toString();
        }

        return "[" + vector.toString() + "]";
    }

    public HashMap<Integer, Neighbor> getNeighbors() {
        return this.neighbors;
    }
}