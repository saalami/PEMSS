package ca.ucalgary.sim;

public class Evacuee {
    private static int counter;
    private int id;

    public Evacuee() {
        id = counter++;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return "Ev" + id;
    }
}
