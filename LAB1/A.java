package MonteCarloSimulationFile;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Random;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.concurrent.TimeUnit;

class workerThreadTask extends Thread{
	private int Niterations;

	public workerThreadTask(int Niterations)
	{
		this.Niterations=Niterations;
	}

	public void run()
	{
		Random rand = new Random();
		int localCirclePointsCount = 0;
		int localSquarePointCount =0;
		while(Niterations>0)
		{
			double x = rand.nextDouble();
			double y = rand.nextDouble();
			//System.out.println(x+" "+y);

			double d=x*x+y*y;
			if(d<=1)
				localCirclePointsCount++;
			localSquarePointCount++;
			Niterations--;
		}

		MonteCarloSimulation.circlePointsCount.getAndAdd(localCirclePointsCount);
		MonteCarloSimulation.squarePointsCount.getAndAdd(localSquarePointCount);
	}
}

class MonteCarloSimulation{
	public static AtomicInteger circlePointsCount;
	public static AtomicInteger squarePointsCount;
	private static final int Npoints = 1000000;

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

		circlePointsCount=new AtomicInteger(0);
		squarePointsCount=new AtomicInteger(0);

		ExecutorService taskExecutorService = Executors.newFixedThreadPool(Nthreads);

		int perThreaditerations = (Npoints+Nthreads-1)/Nthreads;

		for(int i=0;i<Nthreads;i++)
		{
			int numIterations = Math.min((i+1)*perThreaditerations,Npoints) - i*perThreaditerations;
			taskExecutorService.submit(new workerThreadTask(numIterations));
		}

		taskExecutorService.shutdown();
		try {
			taskExecutorService.awaitTermination(90, TimeUnit.SECONDS);
		}
		catch (InterruptedException e){
			System.out.println("Fatal Error, Please run again!!");
			System.exit(1);
		}

		double PI = (4*circlePointsCount.doubleValue())/squarePointsCount.doubleValue();

		System.out.println("The Estimated Value of PI is: "+PI);
	}
}

