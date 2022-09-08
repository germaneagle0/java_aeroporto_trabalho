/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
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

