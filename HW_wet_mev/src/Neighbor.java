public class Neighbor {
    private int node_name;
    private double weight;
    private int sent_port;
    private int get_port;

    public Neighbor(int node_name, double weight, int sent_port, int get_port) {
        this.node_name = node_name;
        this.weight = weight;
        this.sent_port = sent_port;
        this.get_port = get_port;
    }

    public double getWeight() {
        return weight;
    }
    public void setWeight(Double newWeight){
        this.weight = newWeight;
    }
}
