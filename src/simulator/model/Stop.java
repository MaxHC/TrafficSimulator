package simulator.model;

public abstract class Stop {
    public int[] coordinates;
    public Stop(int x, int y) {
        this.coordinates = new int[2];
        this.coordinates[0] = x;
        this.coordinates[1] = y;
    }

    public int[] getCoordinates(){
        return this.coordinates;
    }
}
