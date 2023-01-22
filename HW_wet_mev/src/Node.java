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
                                        System.out.println("node: " + this.id + " try");
                                        DataInputStream input = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
                                        System.out.println("node: " + this.id + " create input");
                                        // using TLV protocol of messages [1 byte character indicate the type| 4 byte Integer indicate the length | N byte of message]

                                        char dataType = 's';
                                        System.out.println("node: " + this.id + "this is the data type: " + dataType);
                                        int length = input.readInt();
                                        System.out.println("node: " + this.id + "this is the data length: " + length);
                                        if (dataType == 's'){

                                            byte[] messageByte = new byte[length];
                                            boolean end = false;
                                            StringBuilder dataString = new StringBuilder(length);
                                            int totalBytesRead = 0;
                                            // Read function might not be able to read all data in one call. so, we need to cal the read() in while loop:
                                            System.out.println("node: " + this.id + "before while");
                                            while (!end){
                                                int currentBytesRead = input.read(messageByte);
                                                totalBytesRead = currentBytesRead + totalBytesRead;
                                                if (totalBytesRead <= length){
                                                    dataString.append(new String(messageByte, 0, currentBytesRead, StandardCharsets.UTF_8));
                                                }else {
                                                    dataString.append(new String(messageByte, 0, length - totalBytesRead + currentBytesRead, StandardCharsets.UTF_8));
                                                }
                                                if (dataString.length() >= length){
                                                    end = true;
                                                }
                                            }
                                            System.out.println("node: " + this.id + "before process");
                                            processMessage(dataString.toString());
                                        }
//                                        InputStream in = socket.getInputStream();
//                                          System.out.println(this.id +" - get here1" + " ");
//                                        byte[] massageBytes = new byte[1024];
//                                        System.out.println("get here2");

//                                        in.read(massageBytes);
//                                        System.out.println("get here3");
//                                        String massage = new String(massageBytes);
//                                        processMessage(massage);
                                        socket.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }).start();
                            } catch (IOException e) {
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
            ;
            try {

                // Create a socket connection to the neighbor's IP address and port
                Socket socket = new Socket(this.IP, neighbor.getSent_port());
                // Get the output stream from the socket to write the message

                //OutputStream out = socket.getOutputStream();
                // Write the message to the output stream
                DataOutputStream output = new DataOutputStream(socket.getOutputStream());

                char type = 's';
                byte[] dataInBytes = message.getBytes(StandardCharsets.UTF_8);
                output.writeChar(type);
                output.writeInt(dataInBytes.length);
                output.write(dataInBytes);
                // Flush the output stream to ensure the message is sent
                output.flush();
                // Close the socket connection
                socket.close();
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