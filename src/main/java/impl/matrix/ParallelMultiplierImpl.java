package impl.matrix;

import api.matrix.ParallelMultiplier;

public class ParallelMultiplierImpl implements ParallelMultiplier {
    int maxThreadsCount;

    ParallelMultiplierImpl(int maxThreadsCount) {
        this.maxThreadsCount = maxThreadsCount;
    }

    @Override
    public double[][] mul(double[][] a, double[][] b) {
        Thread[] t = new Thread[maxThreadsCount];
        int aRowsCount = a.length;
        int bColumnsCount = b[0].length;
        double[][] c = new double[aRowsCount][bColumnsCount];

        if (a[0].length != b.length) {
            return c;
        }

        int cElementsCount = aRowsCount * bColumnsCount;

        long step = (int) Math.ceil((double) cElementsCount / maxThreadsCount);
        long startI = 0;
        long finishI = startI + step;
        for (int i = 0; i < maxThreadsCount; i++) {
            t[i] = new Thread(new Multiplier(a, b, c, startI, finishI));
            t[i].start();

            if (finishI == cElementsCount) {
                maxThreadsCount = i + 1;
                break;
            }
            startI += step;
            finishI = Math.min(finishI + startI, cElementsCount);
        }

        try {
            for (int i = 0; i < maxThreadsCount; i++) {
                t[i].join();
            }
        } catch (InterruptedException e) {
            System.out.println("Error!");
        }

        return c;
    }
}