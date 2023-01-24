import java.net.InetAddress;
import java.net.UnknownHostException;

public class Neighbor {
    private int node_name;
    private double weight;
    private int sent_port;
    private int get_port;
    private String ipAddress;

    public Neighbor(int node_name, double weight, int sent_port, int get_port) {
        this.node_name = node_name;
        this.weight = weight;
        this.sent_port = sent_port;
        this.get_port = get_port;
        try {
            InetAddress addr = InetAddress.getByName(String.valueOf(node_name));
            this.ipAddress = addr.getHostAddress();
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    public String getIpAddress() {
        return ipAddress;
    }

    public int getGet_port() {
        return get_port;
    }

    public int getSent_port() {
        return sent_port;
    }

    public int getNode_name() {
        return node_name;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(Double newWeight){
        this.weight = newWeight;
    }
}
