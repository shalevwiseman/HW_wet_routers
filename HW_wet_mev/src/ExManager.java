import java.io.*;
import java.util.HashMap;
import java.util.Objects;

public class ExManager {
    private String path;
    private int num_of_nodes;
    public HashMap<Integer, Node> nodes;
    private String IpAdrress;
    // your code here

    public ExManager(String path){
        this.path = path;
        this.nodes = new HashMap<>();
        // your code here
    }

    public Node get_node(int id){
        return nodes.get(id);
    }

    public int getNum_of_nodes() {
        return this.num_of_nodes;
    }

    public void update_edge(int id1, int id2, double weight){
        nodes.get(id1).updateEdge(id2, weight);
        nodes.get(id2).updateEdge(id1, weight);
    }

    public void read_txt(){
        try {
            BufferedReader reader = new BufferedReader(new FileReader(this.path));
            String line;
            while ((line = reader.readLine()) != null) {
                String[] words = line.split("\\s+");
                if (words.length == 1 && !Objects.equals(words[0], "stop")){
                    this.num_of_nodes = Integer.parseInt(words[0]);
                    continue;
                }
                if (words[0].equals("stop")) {
                    break;
                }
                int node_name = Integer.parseInt(words[0]);
                Node node = new Node(node_name, this.num_of_nodes);
                nodes.put(node_name, node);
                if (words.length > 1) {
                    for (int i = 1; i < words.length; i += 4) {
                        int neighbor_name = Integer.parseInt(words[i]);
                        double weight = Double.parseDouble(words[i + 1]);
                        int sent_port = Integer.parseInt(words[i + 2]);
                        int get_port = Integer.parseInt(words[i + 3]);
                        node.addNeighbor(neighbor_name, weight, sent_port, get_port);
                    }
                    node.serversUp();
                }
            }
            for (Node node : nodes.values()){
                node.clientsUp();
            }
            reader.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void start(){
        for(Node node: nodes.values()){

            node.run();
        }

        nodes.get(3).createVector();
        String message = nodes.get(3).getVectorAsString();
        nodes.get(3).sendMessage(message);
    }

    public void terminate(){
        // your code here
    }
}
