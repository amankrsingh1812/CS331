package assignment2;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.Random;
import java.util.Hashtable;
import java.io.*; 
import java.util.ArrayList;

//Class of Nodes of LinkedList
//ReentrantLock is used for fine-grain locking for updation of value of a node
class node{
	public String userId;
	public int amount;
	public ReentrantLock lock;
	public node next;
	public node(String userId, int amount)
	{
		this.userId = userId;
		this.amount = amount;
		this.lock = new ReentrantLock();
		this.next = null;
	}

}

//Class of LinkedList
//Updation is done using fine-grain locking
//Deletion and insertion is done use coarse-grain locking
class linkedList{

	public node head;
	public node end;
	public ReentrantReadWriteLock lock;
	public linkedList()
	{
		head = new node("0000000000",-1);
		end = head;
		lock = new ReentrantReadWriteLock();
	}

	//First finds the required node in the list
	//Then accquire lock of the node and adds updateAmount to its value
	public boolean updateNode(String userId,int updateAmount)
	{
		lock.readLock().lock();
		node cur = head.next;
		while(cur!=null)
		{
			if(cur.userId.equals(userId))
			break;
			cur=cur.next;
		}
		lock.readLock().unlock();
		if(cur!=null)
		{
			try{
				cur.lock.lock();
				if(cur.amount+updateAmount<0)
				return false;
				cur.amount += updateAmount;
				return true;
			}
			finally{
				cur.lock.unlock();
			}
		}
		return false;
	}
	
	//The end variable always points to the end of linked list 
	//New node is added to the next of end and end is change to end.next
	public boolean insertNode(String userId, int amount)
	{
		if(end!=null)
		{
			try{
				lock.writeLock().lock();
				end.next=new node(userId,amount);
				end=end.next;
				return true;
			}
			finally{
				lock.writeLock().unlock();
			}
		}
		
		return false;
	}
	
	//First finds the required node in the list
	//Then accquire lock of the list and deletes the node
	public int deleteNode(String userId)
	{
		node prevNode = head;
		node curNode = head.next;
		while(curNode != null &&!curNode.userId.equals(userId))
		{
			prevNode = curNode;
			curNode = curNode.next;
		}
		if(curNode!=null)
		{
			try{
				lock.writeLock().lock();
				int amount = curNode.amount;
				prevNode.next = curNode.next;
				return amount;
			}
			finally{
				lock.writeLock().unlock();
			}
		}

		return -1;
	}

	//Used by main thread to add initial users
	public void addNode(String userId,int amount)
	{
		end.next = new node(userId,amount);
		end = end.next;
	}
}

// Thread task to execute transactions per updater
class updaterTask extends Thread{
	private int branchId;
	private int inpId;
	public updaterTask(int branchId,int updaterId)
	{
		this.branchId = branchId;
		inpId = branchId*10 + updaterId;
	}

	//Helper Function to convert string to integer
	private int stringToInt(String s)
	{
		int ans=0;
		for(char ch : s.toCharArray())
		{
			ans=ans*10;
			ans+=ch-'0';
		}
		return ans;
	}

	//TransactionCodes 0-Deposit, 1-Withdraw, 2-Transfer, 3-addCustomer, 4-deleteCustomer, 5-transferCustomer
	public void run()
	{
		for(int i=0;i<exectuteTransactions.transactionsPerThread;i++)
		{
			String curUserId, fromUserId, toUserId, newUserId;
			int amount;
			String curTransaction = exectuteTransactions.inp[i+exectuteTransactions.transactionsPerThread*inpId];
			switch(curTransaction.charAt(0)) {
				//Deposit: adds to the node containig current customer details the value amount
				case '0':
					curUserId = curTransaction.substring(1,11);
					amount = stringToInt(curTransaction.substring(11));
					exectuteTransactions.branchWiseListMap.get((curUserId.charAt(0)-'0')).updateNode(curUserId,amount);
					break;
				
				//Withdraw: adds to the node containig current customer details the value -amount
				case '1':
					curUserId = curTransaction.substring(1,11);
					amount = stringToInt(curTransaction.substring(11));	
					exectuteTransactions.branchWiseListMap.get((curUserId.charAt(0)-'0')).updateNode(curUserId,-amount);
					break;

				//Transfer: deposits money in the toUserId acount and withdraws money from the fromUserId acount
				case '2':
					fromUserId = curTransaction.substring(1,11);
					toUserId = curTransaction.substring(11,21);
					amount = stringToInt(curTransaction.substring(21));
					if(exectuteTransactions.branchWiseListMap.get((toUserId.charAt(0)-'0')).updateNode(toUserId,amount))
					{
						if(!exectuteTransactions.branchWiseListMap.get((fromUserId.charAt(0)-'0')).updateNode(fromUserId,-amount))
							exectuteTransactions.branchWiseListMap.get((toUserId.charAt(0)-'0')).updateNode(toUserId,-amount);
					}
					break;

				//addCustomer: Adds new node to the linkedList corresponding to the new customer
				case '3':
					newUserId = curTransaction.substring(1);
					amount = stringToInt(curTransaction.substring(11));	
					exectuteTransactions.branchWiseListMap.get(branchId).insertNode(newUserId,amount);
					break;

				//deleteCustomer: Deletes node from the linkedList containing the customer details	
				case '4':
					curUserId = curTransaction.substring(1,11);
					exectuteTransactions.branchWiseListMap.get((curUserId.charAt(0)-'0')).deleteNode(curUserId);
					break;

				//transferCustomer: Deletes the node of customer from the curent branch list and adds a new node for customer in the new branch list
				case '5':
					curUserId = curTransaction.substring(1,11);
					int newBranchId = stringToInt(curTransaction.substring(11));
					amount = exectuteTransactions.branchWiseListMap.get((curUserId.charAt(0)-'0')).deleteNode(curUserId);
					if(amount >= 0)
						exectuteTransactions.branchWiseListMap.get(newBranchId).insertNode(curUserId,amount);
					break;
			}
		}
	}
}

class exectuteTransactions {

	public static String inp[];
	public static int transactionsPerThread;
	public static Hashtable <Integer,linkedList> branchWiseListMap;		//Thread Safe HashTable to store linkedList branchWise

	//Helper Function to generate user Id for a new user in a branch
	//cnt is the total number of users in the branch before adding new user 
	private static String generateNewUserId(int cnt,int branchId)
	{
		StringBuilder newUserId = new StringBuilder();
		newUserId.append(String.valueOf(cnt));
		while(newUserId.length()<9)
			newUserId.insert(0,'0');
		newUserId.insert(0,branchId);
		return newUserId.toString();
	}

	//Helper function to convert String to Integer
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


	public static void main(String args[]) throws Exception 
	{
		// Valid Input argument checks
		if (args.length != 1) {
			System.out.println("Please Enter Valid Number of arguments!!");
			System.exit(1);
		}

		//Initialisations

		transactionsPerThread=stringToInt(args[0]);

		branchWiseListMap = new Hashtable<>();
		Random rand = new Random();
		for(int branchId=0;branchId<10;branchId++)
		{
			linkedList list = new linkedList();
			for(int i=1;i<=10000;i++)
			{
				list.addNode(generateNewUserId(i,branchId),rand.nextInt(100000));
			}
			branchWiseListMap.put(branchId,list);
		}
		inp = new String [transactionsPerThread*100];
		int i=0;
		File file = new File("testCase.txt");
		BufferedReader br = new BufferedReader(new FileReader(file));

		String st;
		while ((st = br.readLine()) != null)
		{
			inp[i++]=st;
		}

		//Execution begins
		System.out.println("Starting Execution");
		System.out.println("Please Wait!! This may take some time");
		long executionTime = System.currentTimeMillis(); //Used to track time of execution

		ArrayList <updaterTask> threadlist = new ArrayList<>();
		for(int branchId=0;branchId<10;branchId++)
			for(int updaterId=0;updaterId<10;updaterId++)
			{
				updaterTask curThread = new updaterTask(branchId,updaterId);
				curThread.start();
				threadlist.add(curThread);
			}
		for(int branchId=0;branchId<10;branchId++)
			for(int updaterId=0;updaterId<10;updaterId++)
			{
				updaterTask curThread = threadlist.get(branchId*10+updaterId);
				curThread.join();
			}

		executionTime = System.currentTimeMillis() - executionTime;
		System.out.println("Program executed Successfully in "+executionTime+" ms");

		br.close();

	}

}
