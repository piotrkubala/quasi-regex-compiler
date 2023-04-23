package pl.edu.agh.kis.generator;

public class Counter extends Number {
    private int value;

    public Counter(int value) {
        this.value = value;
    }

    public void inc(int value) {
        this.value += value;
    }

    public void inc() {
        inc(1);
    }

    @Override
    public int intValue() {
        return value;
    }

    @Override
    public long longValue() {
        return value;
    }

    @Override
    public float floatValue() {
        return value;
    }

    @Override
    public double doubleValue() {
        return value;
    }
}
