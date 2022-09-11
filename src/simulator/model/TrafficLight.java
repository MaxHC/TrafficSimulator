package simulator.model;

import java.util.Scanner;

public class TrafficLight {

    private int light; //0: red, 1: green, 2: orange
    private int count;
    public int[] time = {7, 6, 1};
    private boolean countable;

    public TrafficLight(boolean green) {
        this.light = (green) ? 1 : 0;
        this.count = 0;
        this.countable = true;
    }

    public int getLight() {
        return this.light;
    }
    public void change() {
        this.count = 0;
        this.light = (this.light + 1) % 3;
    }

    public void setTime(int[] time) {
        this.time = time;
    }

    public void setLight(int light) {
        this.light = light;
    }

    public void setCount(int count) {
        this.count = count;
    }

    public void setCountable(boolean countable) {
        this.countable = countable;
    }

    public void counting() {
        if (this.countable) {
            this.count++;
            if (this.count >= time[this.light] * 300) {
                this.change();
            }
        }
    }

}
