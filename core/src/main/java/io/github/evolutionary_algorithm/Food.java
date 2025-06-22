package io.github.evolutionary_algorithm;

public class Food {
    private int nutrition;
    private int code;
    private int i;
    private int j;
    public Food(int i,int j, int nutrition, int code){
        this.i = i;
        this.j = j;
        this.nutrition = nutrition;
        this.code = code;
    }
    public int getNutrition() {
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
