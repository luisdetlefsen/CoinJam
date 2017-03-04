package CodeJam.Y2016.Qualification.JamCoin;

import java.math.BigInteger;
import java.util.logging.Level;
import java.util.logging.Logger;

class NonTrivialDivisorFinderThread extends Thread {

    Logger logger = Logger.getAnonymousLogger();
    BigInteger lowerBound;
    BigInteger upperBound;
    BigInteger N;

    public NonTrivialDivisorFinderThread(BigInteger N, BigInteger lowerBound, BigInteger upperBound) {
        logger.setLevel(Level.INFO);
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
        this.N = N;
    }

    private BigInteger getNonTrivialDivisor(BigInteger N, BigInteger lowerBound, BigInteger upperBound) {

        logger.info(Thread.currentThread().getName() + " searching nonTrivialDivisor for " + N.toString() + " from " + lowerBound + " to " + upperBound);
//        System.out.println(Thread.currentThread().getName() + " searching nonTrivialDivisor for " + N.toString() + " from " + lowerBound + " to " + upperBound);
        for (BigInteger i = lowerBound; i.compareTo(upperBound) < 0; i = i.add(BigInteger.ONE)) {
            if (this.isInterrupted()) {
                logger.info(Thread.currentThread().getName() + " aborting search. Divisor found by another thread.");
                return BigInteger.ZERO;
            }
//            if (NonTrivialDivisorFinder.divisorFound) {

//                   return BigInteger.ZERO;
//                }
            if (N.mod(i) == BigInteger.ZERO) {
                if (i.compareTo(BigInteger.ONE) != 0 && i.compareTo(N) != 0) {
                    logger.info(Thread.currentThread().getName() + " found divisor: " + i);
                    //System.out.println(Thread.currentThread().getName() + " found divisor: " + i);
                    return i;
                }
            }
        }
        logger.info(Thread.currentThread().getName() + " divisor not found.");
        //System.out.println(Thread.currentThread().getName() + " divisor not found.");
        return BigInteger.ZERO;
    }

    @Override
    public void run() {
        //System.out.println(Thread.currentThread().getName() + " Thread started.");
        logger.info(Thread.currentThread().getName() + " Thread started.");
        BigInteger divisor = BigInteger.ZERO;

        divisor = getNonTrivialDivisor(N, lowerBound, upperBound);

        NonTrivialDivisorFinder.report(divisor);
        logger.info(Thread.currentThread().getName() + " Thread ended normally.");
        //System.out.println(Thread.currentThread().getName() + " Thread ended normally.");
    }

    @Override
    public void interrupt() {
        super.interrupt(); //To change body of generated methods, choose Tools | Templates.
        //System.out.println("Thread was interrupted. Not polite.");
    }

}
