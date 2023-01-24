import java.util.*;

public class RoutingTable
{
    int numRouters;
    ArrayList<Integer> distance;
    ArrayList<Integer> next;

    public RoutingTable(int selfName, int numRouters, int firstNeighbor, int diamBound) {
        this.numRouters = numRouters;
        this.distance = new ArrayList<>(numRouters + 1);
        this.next = new ArrayList<>(numRouters + 1);
        distance.add(0, -1);
        next.add(0, -1);
        for (int i = 1; i <= numRouters; i++) {
            if (i == selfName) {
                distance.add(i, 0);
                next.add(i, null);
            } else {
                distance.add(i, diamBound);
                next.add(i, firstNeighbor);
            }
        }
    }

    public ArrayList<Integer> getNext() {
        return next;
    }

    public ArrayList<Integer> getDistance() {
        return distance;
    }

    public void updateDistance(Integer newWeight, Integer oldWeight, int index) {
        distance.set(index, distance.get(index) - oldWeight + newWeight);
    }

}