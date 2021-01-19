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
	public void run()
	{
		Random rand = new Random();
		double x = rand.nextDouble();
		double y = rand.nextDouble();
		//System.out.println(x+" "+y);

		double d=x*x+y*y;
		if(d<=1)
			MonteCarloSimulation.circlePointsCount.incrementAndGet();

		MonteCarloSimulation.squarePointsCount.incrementAndGet();
	}
}

class MonteCarloSimulation{
	public static AtomicInteger circlePointsCount;
	public static AtomicInteger squarePointsCount;
	private static final int Npoints = 1000000;

	private static void setFileOutput()
	{
		PrintStream o;
		try {
			o = new PrintStream(new File("Logs.txt"));
			System.setOut(o);
			System.setErr(o);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
	}

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
		//setFileOutput();
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

		for(int i=1;i<=Npoints;i++)
		{
			taskExecutorService.submit(new workerThreadTask());
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

		System.out.println("The Estimated Value of PI is:"+PI);
	}
}

