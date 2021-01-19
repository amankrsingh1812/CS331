package matrixmultiplication;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Random;
import java.util.concurrent.TimeUnit;

class rowComputeTask extends Thread{
	private int row;

	public rowComputeTask(int row)
	{
		this.row=row;
	}

	public void run()
	{
		for(int column=0;column<MatrixMultiplication.N;column++)
		{
			for(int j=0;j<MatrixMultiplication.N;j++)
				MatrixMultiplication.C[row][column]+=MatrixMultiplication.A[row][j]*MatrixMultiplication.B[j][column];
		}
	}
}

class initialiseTask extends Thread{
	private int startRow;
	private int endRow;

	public initialiseTask(int startRow,int endRow)
	{
		this.startRow = startRow;
		this.endRow = endRow;
	}

	public void run()
	{	
		Random rand = new Random();
		
		for(int row = startRow;row < endRow;row++)
			for(int column = 0;column < MatrixMultiplication.N;column++)
			{
				MatrixMultiplication.C[row][column]=0;
				MatrixMultiplication.A[row][column]=rand.nextInt(11);
				MatrixMultiplication.B[row][column]=rand.nextInt(11);
			}
	}
}

class task1 extends Thread{
	private int row;
	private int column;

	task1(int row,int column)
	{
		this.row=row;
		this.column=column;
	}

	public void run()
	{
		for(int j=0;j<MatrixMultiplication.N;j++)
		{
			MatrixMultiplication.C[row][column]+=MatrixMultiplication.A[row][j]*MatrixMultiplication.B[j][column];
		}
	}
}

class MatrixMultiplication{
	public static final int N=1000;
	public static int A[][];
	public static int B[][];
	public static int C[][];
	private static ExecutorService matrixMultiplicationExecutor;
	private static ExecutorService matrixInitialiserExecutor;

	private static int stringToInt(String s)
	{
		if(s.length() > 2)
			return -1;
		int ans=0;
		for(char c : s.toCharArray())
		{
			if(c-'0'<0||c-'0'>9)
				return -1;
			ans=ans*10;
			ans=ans+(c-'0');
		}
		return ans;
	}

	private static void intialise(int Nthreads)
	{
		A = new int[N][N];
		B = new int[N][N];
		C = new int[N][N];

		int perThreadRows = MatrixMultiplication.N/Nthreads;
		for(int i=0;i<Nthreads;i++)
			matrixInitialiserExecutor.submit(new initialiseTask(i*perThreadRows,Math.min((i+1)*perThreadRows,MatrixMultiplication.N)));
		//Random rand = new Random();

		//for(int i=0;i<N;i++)
			//for(int j=0;j<N;j++)
			//{
				////matrixMultiplicationExecutor.submit(new initialiseTask(i,j));
				//C[i][j]=0;
				//A[i][j]=rand.nextInt(11);
				//B[i][j]=rand.nextInt(11);
			//}
	}

	public static void main(String args[])
	{
		if(args.length != 1)
		{
			System.out.println("Please Enter Valid Number of arguments!!");
			System.exit(1);
		}
		int Nthreads = stringToInt(args[0]);

		if(Nthreads<4||Nthreads>16)
		{
			System.out.println("Please Enter Valid Number of Threads!!");
			System.exit(1);
		}

		matrixInitialiserExecutor = Executors.newFixedThreadPool(Nthreads);
		intialise(Nthreads);
		matrixInitialiserExecutor.shutdown();
		try {
			matrixInitialiserExecutor.awaitTermination(90, TimeUnit.SECONDS);
		}
		catch (InterruptedException e){
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}


		matrixMultiplicationExecutor = Executors.newFixedThreadPool(Nthreads);

		for(int i=0;i<N;i++)
			//for(int j=0;j<N;j++)
			matrixMultiplicationExecutor.submit(new rowComputeTask(i));

		matrixMultiplicationExecutor.shutdown();
		try {
			matrixMultiplicationExecutor.awaitTermination(90, TimeUnit.SECONDS);
		}
		catch (InterruptedException e){
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		//for(int i=0;i<N;i++)
		//for(int j=0;j<N;j++)
		//{
		//for(int k=0;k<N;k++)
		//C[i][j]+=A[i][k]*B[k][j];

		////if(C[i][j]!=0)
		////System.out.println("Error at "+i+" "+j);
		//}

	}
}
