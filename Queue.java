package TrabalhoAeroporto;

public class Queue implements Comparable<Queue> {
    Integer total_distance;
    String name;
    Queue(String name, Integer dist) {
        this.name = name;
        this.total_distance = dist;
    }
    @Override
    public int compareTo(Queue outraConta) {
        if (this.total_distance < outraConta.total_distance) {
            return -1;
        }
        if (this.total_distance > outraConta.total_distance) {
            return 1;
        }
        return 0;
    }
}
