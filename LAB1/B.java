package PartB;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

//
class ThreadTask extends Thread {
	public int st, end;

	public ThreadTask(int st, int end) {
		this.st = st;
		this.end = end;
	}

	// Each thread evalaute the function at the given points and stores the sum in local varible.
	// Finally it updates the global sum variable upon completion of all iterations
	public void run() {
		double localSum = 0; // stores sum locally in a threads context

		// Iterate on given sets of points
		for (int i = st; i < end; i++) {
			double fac = 2;
			if (i % 2 == 1)
				fac += 2;
			localSum += fac * SimpsonRule.function(-1.00 + Double.valueOf(i) * SimpsonRule.delta);
		}

		// Update global sum Variable
		synchronized (SimpsonRule.lock) {
			SimpsonRule.sum += localSum;
		}
	}
}

class SimpsonRule {
	public static int N = 1000000 + 1; // Variable to store number of points
	public static double delta = 0; // Delta value as defined in simpson rule
	public static double sum = 0; // Stores the sum of function values
	public static Object lock; // Lock for synchronisation
	public static final double rootTwoPi = Math.sqrt(2.0000 * Math.PI); // Constant value = squareRoot(2*PI)

	// The function for which integral is to evaluated
	public static double function(double x) {
		double argument = x * x / 2.00;
		double num = Math.exp(-argument);
		return num / rootTwoPi;
	}

	// Helper function to convert String to Int. Return -1 in case of wrong input
	private static int stringToInt(String s) {
		if (s.length() > 2)
			return -1;
		int ans = 0;
		for (char c : s.toCharArray()) {
			if (c - '0' < 0 || c - '0' > 9)
				return -1;
			ans = ans * 10;
			ans = ans + (c - '0');
		}
		return ans;
	}

	public static void main(String args[]) {
		// Valid Input argument checks
		if (args.length != 1) {
			System.out.println("Please Enter Valid Number of arguments!!");
			System.exit(1);
		}

		int Nthreads = stringToInt(args[0]); // Stores Number of Threads from Input

		// Number of Threads Check
		if (Nthreads < 4 || Nthreads > 16) {
			System.out.println("Please Enter Valid Number of Threads!!");
			System.exit(1);
		}

		// Edge Cases
		sum = function(-1.00) + function(1.00);

		N = N + N % Nthreads; // Make Number of points divisible by Nthreads
		delta = (2 / Double.valueOf(N));
		int perThreaditerations = N / Nthreads; // Stores Number of iterations to be carried out by each thread

		// Thread Executor Service of a fixed size Thread pool
		ExecutorService threadExecutorService = Executors.newFixedThreadPool(Nthreads);
		lock = new Object();

		// Give each thread Proportionate work by distributing equall number of points to each thread
		for (int i = 0; i < Nthreads; i++) {
			threadExecutorService
					.submit(new ThreadTask(Math.max(1, i * perThreaditerations), (i + 1) * perThreaditerations));
		}

		// Wait for task of all threads to complete
		threadExecutorService.shutdown();
		try {
			threadExecutorService.awaitTermination(90, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		// Calculate final answer
		double answer = sum * delta / 3.000;

		System.out.println("The value of Integral is " + answer);
	}
}
