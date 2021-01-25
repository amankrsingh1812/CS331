package PartA;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

class workerThreadTask extends Thread {
	private int Niterations; // Number of iterations required to be performed

	public workerThreadTask(int Niterations) {
		this.Niterations = Niterations;
	}

	// Each thread stores the count of points(both Inside circle and total points)
	// in a local varible
	// Finally it updates the global count of points
	public void run() {
		Random rand = new Random();
		int localCirclePointsCount = 0; // stores count of Points inside circle locally in the threads context
		int localSquarePointCount = 0; // stores total count of Points locally in the threads context
		while (Niterations > 0) {
			double x = rand.nextDouble(); // Generate a random double value between 0 and 1 (inclusive)
			double y = rand.nextDouble();

			double d = x * x + y * y; // Calcutate distance of points from origin

			// Check whether generated points lie inside circle
			if (d <= 1)
				localCirclePointsCount++;
			localSquarePointCount++;
			Niterations--;
		}

		System.out.println(localCirclePointsCount);
		// Update global counters
		MonteCarloSimulation.circlePointsCount.getAndAdd(localCirclePointsCount);
		MonteCarloSimulation.squarePointsCount.getAndAdd(localSquarePointCount);
	}
}

class MonteCarloSimulation {
	public static AtomicInteger circlePointsCount; // Global counter of Points inside circle
	public static AtomicInteger squarePointsCount; // Global counter of total number of Points generated
	private static final int Npoints = 100000000; // Total Number of Points to be tested

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

		// Initailise both global counter with 0
		circlePointsCount = new AtomicInteger(0);
		squarePointsCount = new AtomicInteger(0);

		// Thread Executor Service of a fixed size Thread pool
		ExecutorService taskExecutorService = Executors.newFixedThreadPool(Nthreads);

		// Give each thread Proportionate work by distributing almost equall number of iterations to each thread
		int perThreaditerations = (Npoints + Nthreads - 1) / Nthreads;
		for (int i = 0; i < Nthreads; i++) {
			int numIterations = Math.min((i + 1) * perThreaditerations, Npoints) - i * perThreaditerations;
			taskExecutorService.submit(new workerThreadTask(numIterations));
		}

		// Wait for task of all threads to complete
		taskExecutorService.shutdown();
		try {
			taskExecutorService.awaitTermination(90, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		// Calcutae PI as Ratio of 4*NumberOfPointsInsideCircle / TotalNumberOfPoints
		double PI = (4 * circlePointsCount.doubleValue()) / squarePointsCount.doubleValue();

		System.out.println("The Estimated Value of PI is: " + PI);
	}
}
