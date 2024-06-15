package grupo3.utils;

public class IntPair {
    private int appearances;
    private int inversions;

    // Constructor initializes both integers to 0
    public IntPair() {
        this.appearances = 0;
        this.inversions = 0;
    }

    // Method to increment appearances by 1
    public void incrementAppearances() {
        this.appearances++;
    }

    // Method to increment inversions by 1
    public void incrementInversions() {
        this.inversions++;
    }

    // Method to retrieve the current value of appearances
    public int getAppearances() {
        return this.appearances;
    }

    // Method to retrieve the current value of inversions
    public int getInversions() {
        return this.inversions;
    }
}