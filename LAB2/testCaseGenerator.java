package testCaseGenerator;

import java.util.Random;
import java.io.*;
import java.util.*; 
import java.lang.*;

//Each Tansaction is represented using single string with 
//1st character as transaction type Id, 
//2nd to 11th character as userId 
//and rest characters represent remaining arguments depending on transaction type


class generateTestCase{

	private static String generateNewUserId(int cnt,int branchId)
	{
		StringBuilder newUserId = new StringBuilder();
		newUserId.append(String.valueOf(cnt));
		while(newUserId.length()<9)
			newUserId.insert(0,'0');
		newUserId.insert(0,branchId);
		return newUserId.toString();
	}

	private static int stringToInt(String s)
	{
		int ans=0;
		for(char ch : s.toCharArray())
		{
			ans=ans*10;
			ans+=ch-'0';
		}
		return ans;
	}


	public static void main(String args[]) throws IOException  
	{
		// Valid Input argument checks
		if (args.length != 1) {
			System.out.println("Please Enter Valid Number of arguments!!");
			System.exit(1);
		}

		int N=stringToInt(args[0]);

		System.out.println(N);
 		FileWriter fw=new FileWriter(new File("testCase.txt")); 
		ArrayList <Integer> transactionType = new ArrayList<Integer>();
		ArrayList <String> userId = new ArrayList<String>();
		ArrayList<ArrayList <String>> branchWiseUserId = new ArrayList<ArrayList<String>> ();

		for(int i=0;i<10;i++)
			branchWiseUserId.add(new ArrayList<String>());
		for(int i=1;i<=N;i++)
		{
			if(i<=33*(N/100))
				transactionType.add(0);
			else if(i<=66*(N/100))
				transactionType.add(1);
			else if(i<=99*(N/100))
				transactionType.add(2);
			else if(i<=993*(N/1000))
				transactionType.add(3);
			else if(i<=996*(N/1000))
				transactionType.add(4);
			else
				transactionType.add(5);
		}
		for(int branchId=0;branchId<10;branchId++)
			for(int i=1;i<=10000;i++)
			{
				String curUserId = generateNewUserId(i,branchId);
				userId.add(curUserId);
				branchWiseUserId.get(branchId).add(curUserId);
			}

		int Namount=10001;
		for(int branchId=0;branchId<10;branchId++)
		{
			int cnt = 10000;
			
			for(int updaterId=0;updaterId<10;updaterId++)
			{
			
				Random rand = new Random();
				Collections.shuffle(transactionType);
				for(int i=0;i<N;i++)
				{
					int curTransactionType = transactionType.get(i);
					String curTransaction = "";
					curTransaction += (curTransactionType);
					String curUserId = branchWiseUserId.get(branchId).get(rand.nextInt(branchWiseUserId.get(branchId).size()));
					switch (curTransactionType){
						case 0:
							curTransaction += curUserId;
							curTransaction += String.valueOf(rand.nextInt(Namount));	
							break;
						case 1:
							curTransaction += curUserId;
							curTransaction += String.valueOf(rand.nextInt(Namount));
							break;
						case 2:
							curTransaction += curUserId;
							curTransaction += userId.get(rand.nextInt(userId.size()));
							curTransaction += String.valueOf(rand.nextInt(Namount));
							break;
						case 3:
							cnt++;
							String newUserId = generateNewUserId(cnt,branchId);
							userId.add(newUserId);
							branchWiseUserId.get(branchId).add(newUserId);
							curTransaction += newUserId;
							curTransaction += String.valueOf(rand.nextInt(Namount));
							break;
						case 4:
							curTransaction +=  userId.get(rand.nextInt(userId.size()));
							break;
						case 5:
							curTransaction += curUserId;
							curTransaction += String.valueOf(rand.nextInt(10));
							break;
					}
					fw.write(curTransaction+"\n");
				}
			}
		}

		fw.close(); 

	}
}
