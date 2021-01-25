package PartC;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;

// Thread task to compute a paticular row of result matrix
class rowComputeTask extends Thread {
	private int row;

	public rowComputeTask(int row) {
		this.row = row;
	}

	public void run() {

		// Runs in O(N*N)
		for (int column = 0; column < MatrixMultiplication.N; column++) {
			for (int j = 0; j < MatrixMultiplication.N; j++)
				MatrixMultiplication.C[row][column] += MatrixMultiplication.A[row][j]
						* MatrixMultiplication.B[j][column];
		}
	}
}

// Thread task to initialise matrix
// Initialises rows in [startRow,endRow-1]
class initialiseTask extends Thread {
	private int startRow;
	private int endRow;

	public initialiseTask(int startRow, int endRow) {
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public void run() {
		Random rand = new Random();

		for (int row = startRow; row < endRow; row++)
			for (int column = 0; column < MatrixMultiplication.N; column++) {
				MatrixMultiplication.C[row][column] = 0;
				// Generate a Random Intergers between 0 to 10 (inclusive) for matrix A and B
				MatrixMultiplication.A[row][column] = rand.nextInt(11);
				MatrixMultiplication.B[row][column] = rand.nextInt(11);
			}
	}
}

class MatrixMultiplication {
	public static final int N = 1000;
	public static int A[][];
	public static int B[][];
	public static int C[][]; // Result Matrix
	private static ExecutorService matrixMultiplicationExecutor;

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

	private static void intialise(int Nthreads) {
		A = new int[N][N];
		B = new int[N][N];
		C = new int[N][N];

		int perThreadRows = (MatrixMultiplication.N + Nthreads - 1) / Nthreads;
		for (int i = 0; i < Nthreads; i++)
			matrixMultiplicationExecutor.submit(
					new initialiseTask(i * perThreadRows, Math.min((i + 1) * perThreadRows, MatrixMultiplication.N)));
	}

	// Function to write the contents of Matrix C in a File
	public static void outputFile() {
		PrintStream o;
		try {
			o = new PrintStream(new File("MultiplicationOutput.txt"));
			System.setOut(o);
			System.setErr(o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}

		for (int i = 0; i < N; i++) {
			for (int j = 0; j < N; j++) {
				System.out.print(C[i][j] + " ");
			}
			System.out.println("");
		}
	}

	public static void main(String args[]) {
		boolean fileOP = false;
		if (args.length != 1 && args.length != 2) {
			System.out.println("Please Enter Valid Number of arguments!!");
			System.exit(1);
		}
		if (args.length == 2) {
			int tval = stringToInt(args[1]);
			if (tval < 0 || tval > 1) {
				System.out.println("Please Enter Valid Flags!!");
				System.exit(1);
			}
			if (tval == 1)
				fileOP = true;
		}
		int Nthreads = stringToInt(args[0]);

		if (Nthreads < 4 || Nthreads > 16) {
			System.out.println("Please Enter Valid Number of Threads!!");
			System.exit(1);
		}

		// Create fixed size thread pool for initialisation
		matrixMultiplicationExecutor = Executors.newFixedThreadPool(Nthreads);
		// Initialise Matrices
		intialise(Nthreads);

		// Wait untill all entries in matrices are initialised
		matrixMultiplicationExecutor.shutdown();
		try {
			matrixMultiplicationExecutor.awaitTermination(90, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		// Create fixed size thread pool for multiplication
		matrixMultiplicationExecutor = Executors.newFixedThreadPool(Nthreads);

		// 
		for (int i = 0; i < N; i++)
			matrixMultiplicationExecutor.submit(new rowComputeTask(i));

		matrixMultiplicationExecutor.shutdown();
		try {
			matrixMultiplicationExecutor.awaitTermination(90, TimeUnit.SECONDS);
		} catch (InterruptedException e) {
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		if (fileOP)
			outputFile();

	}
}
