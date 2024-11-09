package io.github.evolutionary_algorithm;

import java.util.Random;

public class Gene {
    private Random r;
    int value;
    public Gene(){
        r = new Random();
        this.value = r.nextInt(100);
    }

    public int getValue() {
        return value;
    }

    public void setGene(int gene) {
        this.value = gene;
    }
}
