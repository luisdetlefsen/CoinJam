package CodeJam.Y2016.Qualification.JamCoin;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.math.BigInteger;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author 30102913
 */
public class JamCoinGenerator {

    private final int MIN_BASE = 2;
    private final int MAX_BASE = 10;

    private PrintWriter writer;

    String lastJamCoinUsed = "";

    private List<BigInteger> getNonTrivialDivisors(String x) {
        System.out.println("Searching nonTrivialDivisors for " + x);
        BigInteger limit = new BigInteger(x);

        List<BigInteger> divisors = new ArrayList<>();
        int z = 0;
        for (BigInteger i = BigInteger.ONE; i.compareTo(limit) < 0; i = i.add(BigInteger.ONE)) {
            z++;
            if (z >= 1000) {
                // System.out.println("Iteration: " + i);
                z = 0;
            }
            if (limit.mod(i) == BigInteger.ZERO) {
                if (i.compareTo(BigInteger.ONE) != 0 && i.compareTo(limit) != 0) {
                    divisors.add(i);
                    return divisors;
                }
            }
        }
//        
//        for (Long i = 1l; i < limit; ++i) {
//            if (x % i == 0) {
//                if (i != 1 && i != x) {
//                    divisors.add(i);
//                   // if(divisors.size()>=5)
//                        return divisors;
//                }
//            }
//        }

        return divisors;
    }

    private String convertBase(String n, int fromBase, int toBase) {
        return new BigInteger(n, fromBase).toString(toBase);

        // return Long.parseLong(Long.toString(Long.parseLong(n, fromBase), toBase));
    }

    private String generateRandomJamCoin(int length) {
        String jamCoin = "";
        char[] chars = {'0', '1'};
        for (int i = 0; i < length; i++) {
            jamCoin += chars[ThreadLocalRandom.current().nextInt(0, 1 + 1)];
        }
        System.out.println("Random poossible JamCoin :" + jamCoin);
        return jamCoin;
    }

    private List<BigInteger> findNonTrivialDivisorP(String jamCoin, BigInteger p) {
        BigInteger divisor;
        List<BigInteger> divisors = new ArrayList<>();
        for (int k = MIN_BASE; k <= MAX_BASE; k++) {
            BigInteger n = new BigInteger(convertBase(jamCoin, k, 10));

            divisor = n.gcd(p);

            if (divisor.compareTo(BigInteger.ONE) == 0 || divisor.compareTo(n) == 0) {
                return null;
            }
            System.out.println("Nontrivial divisor: " + divisor);
            divisors.add(divisor);
        }
        return divisors;
    }

    private void solveCase(final int N, final int J, final int caseNumber) {
        List<String> jamCoinsGenerated = new ArrayList<>();

        BigInteger magicNumber = generateMagicNumber();
        writer.println("Case #" + caseNumber + ":");
        System.out.println("Case #" + caseNumber + ":");
        for (int i = 0; i < J; i++) {
            String jamCoin = "";
            do {
                jamCoin = generateValidJamCoin(N);
            } while (jamCoinsGenerated.contains(jamCoin) | findNonTrivialDivisorP(jamCoin, magicNumber)== null );

            jamCoinsGenerated.add(jamCoin);

//            List<BigInteger> divisorsUsed = new ArrayList<>();
//            boolean divisorAdded = false;
            //      System.out.print(jamCoin + " ");
            writer.print(jamCoin + " ");
            BigInteger divisor = null;
            for (int k = MIN_BASE; k <= MAX_BASE; k++) {
                NonTrivialDivisorFinder ntdf = new NonTrivialDivisorFinder();
                //System.out.println("Searching divisor for " + jamCoin + " in base " + k);
                //divisor = ntdf.findNonTrivialDivisor(new BigInteger(convertBase(jamCoin, k, 10)), 4);

                divisor = new BigInteger(convertBase(jamCoin, k, 10)).gcd(magicNumber);

//                System.out.println("Magic number: " + divisor);

                //   List<BigInteger> nonTrivialDivisors = getNonTrivialDivisors(convertBase(jamCoin, k, 10));
//                for (int m = 0; m < nonTrivialDivisors.size(); m++) {
//
//                    BigInteger nonTrivialDivisor = nonTrivialDivisors.get(nonTrivialDivisors.size() - 1 - m);
//                    if (!divisorsUsed.contains(nonTrivialDivisor)) {
//                        divisorsUsed.add(nonTrivialDivisor);
//                        divisorAdded = true;
//                        divisor = nonTrivialDivisor;
//                        break;
//                    }
//                }
                //  if (!divisorAdded) {
                // divisor = nonTrivialDivisors.get(0);
                // }
                //.out.print(divisor + " ");
                writer.print(divisor.toString() + " ");
                //  nonTrivialDivisors.forEach(x -> System.out.print(x + " "));

                writer.flush();
            }
            //     System.out.println("");
            writer.println("");

        }
    }

    /**
     * An incremental JamCoin is preferred in order to avoid really large
     * numbers.
     *
     * @param length
     * @return
     */
    private String generateIncrementalJamCoin(int length) {
        String jamCoin = "";
        if (lastJamCoinUsed.isEmpty()) {
            for (int i = 1; i < length; i++) {
                jamCoin += "0";
            }
            jamCoin = "1" + jamCoin;
            lastJamCoinUsed = jamCoin;
        } else {
            jamCoin = lastJamCoinUsed;
        }
        BigInteger tmp = new BigInteger(jamCoin, 2);
        tmp = tmp.add(BigInteger.ONE);

        lastJamCoinUsed = tmp.toString(2);
        return lastJamCoinUsed;
    }

    private String generateValidJamCoin(int length) {
        String jamCoin = "";
        do {
            jamCoin = generateIncrementalJamCoin(length);
        } while (!isJamCoin(jamCoin));
        //System.out.println("Valid JamCoin: " + jamCoin);
        return jamCoin;
    }

    /**
     * The number provided must not be a prime number in any base from 2 to 10.
     *
     * @param s
     * @return
     */
    private boolean validPrimeBase(String s) {
        for (int j = MIN_BASE; j <= MAX_BASE; j++) {

            if (isPrime(new BigInteger(s, j))) {
                return false;
            }
        }
        return true;
    }

    private boolean validLeadTrail(String s) {
        return s.charAt(0) == '1' && s.charAt(s.length() - 1) == '1';
    }

    private boolean validDigits(String s) {
        if (s.length() < 2) {
            return false;
        }

        for (int i = 1; i < s.length() - 1; i++) {
            if (Character.compare(s.charAt(i), '0') != 0
                    && Character.compare(s.charAt(i), '1') != 0) {
                return false;
            }
        }
        return true;
    }

    private boolean isJamCoin(String s) {
        return validDigits(s) && validLeadTrail(s) && validPrimeBase(s);
    }

    private boolean isPrime(BigInteger n) {

        return n.isProbablePrime(1);
        /*
         if (n.mod(BigInteger.valueOf(2l)) == BigInteger.ZERO) {
         return false;
         }
         n.
         for (int i = 3; i * i <= n; i += 2) {
         if (n % i == 0) {
         return false;
         }
         }
         return true;
         */
    }

    public void chunk(BigInteger lowerBound, BigInteger upperBound, BigInteger N) {

        Utility.sqrt(upperBound);

        if (lowerBound.compareTo(BigInteger.ONE) == 0) {
            lowerBound = lowerBound.add(BigInteger.ONE);
        }
        ArrayList<Integer> primeTable = new ArrayList<>();

        //classic sieve from 1 to sqrt(n)
        BitSet primeBitSet = new BitSet();
        //upperBound.compareTo(new BigInteger(2*2));
        BigInteger comparator = new BigInteger("4");

        for (int j = 2; comparator.multiply(comparator).compareTo(upperBound) < 0; j++) {
            if (!primeBitSet.get(j - 1)) {
                primeTable.add(j);
                if (N.mod(new BigInteger(String.valueOf(j))) == BigInteger.ZERO) {
                    System.out.println("Mod found: " + j);
                    return;
                }

                BigInteger comparator2 = new BigInteger("2");
                comparator2 = comparator2.multiply(new BigInteger(String.valueOf(j)));
                for (int k = 2 * j; comparator2.compareTo(upperBound) < 0 /*k <= n*/; k += j) {
                    primeBitSet.set(k - 1);
                    comparator2 = comparator2.multiply(new BigInteger(String.valueOf(j)));
                }
            }
            comparator = new BigInteger(String.valueOf(j));
        }

        //sieve in the expected range using primes generated by the classic sieve
//        BitSet primesInRangeBitSet = new BitSet(n - l + 1);
//        for (int i = 0; i < primeTable.size(); ++i) {
//            int pomoc = (l / ((int) primeTable.get(i))) * ((int) primeTable.get(i));
//            for (int j = pomoc; j <= n; j = j + (int) primeTable.get(i)) {
//                if (j >= l && !primesInRangeBitSet.get(j - l)) {
//                    primesInRangeBitSet.set(j - l);
//                }
//            }
//        }
        System.out.println("primetable");
        //outputting the primes
        for (int k = 0; k < primeTable.size(); k++) {
            BigInteger comparator3 = new BigInteger(String.valueOf((int) primeTable.get(k)));
            BigInteger comparator4 = new BigInteger(String.valueOf((int) primeTable.get(k)));
            if (comparator3.compareTo(lowerBound) >= 0 && comparator4.compareTo(upperBound) <= 0) {
                System.out.println(primeTable.get(k));
            }
        }

//        System.out.println("rangebitset");
//        for (int k = 0; k < primesInRangeBitSet.size(); k++) {
//            
//            if (primesInRangeBitSet.get(k) == false && k <= n - l) {
//                System.out.println((k + l));
//            }
//        }
//        System.out.println("");
    }

    public void chunk(int lowerBound, int upperBound) {

        int l = lowerBound;
        int n = upperBound;
        if (l == 1) {
            l++;
        }
        ArrayList<Integer> primeTable = new ArrayList<>((int) Math.sqrt(n));
        if (l < 1 || n < 1 || l > n || n > 1000000000) {
            //   System.exit(0);
        }

        //classic sieve from 1 to sqrt(n)
        BitSet primeBitSet = new BitSet((int) (Math.sqrt(n) + 1));
        for (int j = 2; j * j <= n; j++) {
            if (!primeBitSet.get(j - 1)) {
                primeTable.add(j);
                for (int k = 2 * j; k <= n; k += j) {
                    primeBitSet.set(k - 1);
                }
            }
        }

        //sieve in the expected range using primes generated by the classic sieve
//        BitSet primesInRangeBitSet = new BitSet(n - l + 1);
//        for (int i = 0; i < primeTable.size(); ++i) {
//            int pomoc = (l / ((int) primeTable.get(i))) * ((int) primeTable.get(i));
//            for (int j = pomoc; j <= n; j = j + (int) primeTable.get(i)) {
//                if (j >= l && !primesInRangeBitSet.get(j - l)) {
//                    primesInRangeBitSet.set(j - l);
//                }
//            }
//        }
        System.out.println("primetable");
        //outputting the primes
        for (int k = 0; k < primeTable.size(); k++) {

            if ((int) primeTable.get(k) >= l && (int) primeTable.get(k) <= n) {
                System.out.println(primeTable.get(k));
            }
        }

//        System.out.println("rangebitset");
//        for (int k = 0; k < primesInRangeBitSet.size(); k++) {
//            
//            if (primesInRangeBitSet.get(k) == false && k <= n - l) {
//                System.out.println((k + l));
//            }
//        }
//        System.out.println("");
    }

    public int[] chunk(int N) {

        // initially assume all integers are prime
        boolean[] isPrime = new boolean[N + 1];
        for (int i = 2; i <= N; i++) {
            isPrime[i] = true;
        }

        // mark non-primes <= N using Sieve of Eratosthenes
        for (int i = 2; i * i <= N; i++) {

            // if i is prime, then mark multiples of i as nonprime
            // suffices to consider mutiples i, i+1, ..., N/i
            if (isPrime[i]) {
                for (int j = i; i * j <= N; j++) {
                    isPrime[i * j] = false;
                }
            }
        }

        // count primes
        int primesCount = 0;
        for (int i = 2; i <= N; i++) {
            if (isPrime[i]) {
                primesCount++;
            }
        }

        int[] primes = new int[primesCount];
        for (int i = 2, j = 0; i <= N; i++) {
            if (isPrime[i]) {
                primes[j] = i;
                j++;
            }
        }
        return primes;
    }

    private BigInteger generateMagicNumber() {
        List<BigInteger> primes = new ArrayList<>();
        BigInteger i = BigInteger.ONE;
        while (true) {
            if (i.isProbablePrime(1)) {
                primes.add(i);
                //   System.out.println(i.toString());
            }
            i = i.add(new BigInteger("1"));

            if (primes.size() >= 100) {
                break;
            }
        }

        //multiply all primes
        BigInteger r = BigInteger.ONE;

        for (BigInteger prime : primes) {
            r = r.multiply(prime);
        }
        //  System.out.println(r.toString());
        return r;
    }

    public static void main(String[] args) {
        try {

            JamCoinGenerator c = new JamCoinGenerator();
            c.writer = new PrintWriter("C:\\CodeJam\\C\\JamCoin_OutputSmall.txt", "UTF-8");
            long startTime = System.currentTimeMillis();
           
           // c.solveCase(16, 50, 1);  //small case
           // c.solveCase(20, 1, 1);
            c.solveCase(32, 500, 1);  //large case
            //c.chunk(0, 50);
            //   c.generateMagicNumber();
            //    c.chunk(new BigInteger("0"), new BigInteger("10000000000000000000000010100001"), new BigInteger("10000000000000000000000010100001"));
            // List<BigInteger> t = c.getNonTrivialDivisors("10000000000000000000000010100001");
            //System.out.println(t.get(0));
            long elapsedTime = System.currentTimeMillis() - startTime;

            System.out.println("End Application. Total elapsed time:  " + (elapsedTime / 1000.0) + " seconds.\n");
            //c.solveCase(4, 1, 1);
            c.writer.close();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(JamCoinGenerator.class.getName()).log(Level.SEVERE, null, ex);
        } catch (UnsupportedEncodingException ex) {
            Logger.getLogger(JamCoinGenerator.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
