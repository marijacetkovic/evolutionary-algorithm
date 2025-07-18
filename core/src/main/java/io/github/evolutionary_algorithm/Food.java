package io.github.evolutionary_algorithm;

public class Food {
    private double nutrition;
    private int code;
    private int i;
    private int j;
    public Food(int i,int j, double nutrition, int code){
        this.i = i;
        this.j = j;
        this.nutrition = nutrition;
        this.code = code;
    }
    public double getNutrition() {
        return nutrition;
    }

    public int getCode() {
        return code;
    }

    public int getI() {
        return i;
    }

    public int getJ() {
        return j;
    }
}
