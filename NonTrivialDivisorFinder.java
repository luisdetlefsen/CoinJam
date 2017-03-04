package CodeJam.Y2016.Qualification.JamCoin;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

public class NonTrivialDivisorFinder {

    Logger logger = Logger.getAnonymousLogger();
    private static volatile boolean divisorFound = false;

    private static BigInteger divisor;

    /**
     * This method is called by a thread when it has completed its computation
     *
     * @param div
     */
    synchronized public static void report(BigInteger div) {
        if (div.compareTo(BigInteger.ZERO) == 0) {
            return;
        }
        divisor = div;
        divisorFound = true;
    }

    private BigInteger sqrt(BigInteger x) {
        BigInteger div = BigInteger.ZERO.setBit(x.bitLength() / 2);
        BigInteger div2 = div;
        // Loop until we hit the same value twice in a row, or wind
        // up alternating.
        for (;;) {
            BigInteger y = div.add(x.divide(div)).shiftRight(1);
            if (y.equals(div) || y.equals(div2)) {
                return y;
            }
            div2 = div;
            div = y;
        }
    }

    private BigInteger findDivisorWithThreads(int numberOfThreads, BigInteger N) {

        logger.setLevel(Level.INFO);
        long startTime = System.currentTimeMillis();
        NonTrivialDivisorFinderThread[] worker = new NonTrivialDivisorFinderThread[numberOfThreads];

        BigInteger limit = sqrt(N).add(BigInteger.ONE);

        BigInteger groupSize = limit.divide(new BigInteger(String.valueOf(numberOfThreads)));
        for (int i = 0; i < numberOfThreads; i++) {
            BigInteger lowerBound;
            if (i == 0) {
                lowerBound = BigInteger.ONE;
            } else {
                lowerBound = groupSize.multiply(new BigInteger(String.valueOf(i)));
            }

            BigInteger upperBound = lowerBound.add(groupSize);
            worker[i] = new NonTrivialDivisorFinderThread(N, lowerBound, upperBound);
            worker[i].setName("NonTrivialDivisorFinder" + i);
        }

        for (int i = 0; i < numberOfThreads; i++) {
            worker[i].start();
        }

        boolean interruptThreads = divisorFound;
        while (!interruptThreads) {
            interruptThreads = divisorFound;
            if (interruptThreads) {
                logger.info("Interrupting threads.");
                //System.out.println("Interrupting threads");
                for (int i = 0; i < numberOfThreads; i++) {
                    worker[i].interrupt();
                }
                logger.info("Threads interrupted.");
                break;
            }
            try {
                Thread.sleep(100);
            } catch (InterruptedException ex) {
                Logger.getLogger(NonTrivialDivisorFinder.class.getName()).log(Level.SEVERE, null, ex);
            }
        }

        long elapsedTime = System.currentTimeMillis() - startTime;

        System.out.println("Total elapsed time to find divisor " + divisor + " with threads:  "
                + (elapsedTime / 1000.0) + " seconds.\n");

        return divisor;
    }

    public BigInteger findNonTrivialDivisor(BigInteger N, int numThreads) {
        divisorFound = false;
        divisor = null;
        return findDivisorWithThreads(numThreads, N);
    }

//    public static void main(String[] args) {
//        int numberOfThreads = 4;
//        NonTrivialDivisorFinder d = new NonTrivialDivisorFinder();
//        BigInteger t = d.findDivisorWithThreads(numberOfThreads, new BigInteger("10000000000000000000000010100001"));
//        System.out.println("Done: " + t);
//    }
}
