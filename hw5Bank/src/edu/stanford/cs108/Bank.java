package edu.stanford.cs108;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.CountDownLatch;

public class Bank {
	// define null trans
	private final static Transaction nullTransaction = new Transaction(-1, 0, 0);
	
	private static ArrayBlockingQueue<Transaction> blockingQueue;
	private static int queueSize = 20;
	
	// setup accounts
	private static Account[] accounts;
	private static int accountsCount = 20;
	private final static int initBalance = 1000;
	private final static int initTransCount = 0;
	
	// setup workers 
	private static int workersCount;
	private static Worker[] workers;
	
	private static CountDownLatch latch;
	
	public static void main(String[] args) {
		// args[0] is the file name
		// args[1] is the worker numbers
		// create blockingQueue
		blockingQueue = new ArrayBlockingQueue<Transaction>(queueSize);

		// create accounts 
		// id from 0-20
		accounts = new Account[accountsCount];
		for(int i=0;i<accountsCount; i++) {
			accounts[i] = new Account(i, initBalance, initTransCount);
		}
		
		// get workers count
		workersCount = Integer.parseInt(args[1]);
		
		// set countDownLatch
		latch = new CountDownLatch(workersCount);
		
		// create worker (threads)
		workers = new Worker[workersCount];
		for(int i = 0; i < workersCount; i++) {
			workers[i] = new Worker();
		}
		
		// start worker threads
		for(int i=0; i < workersCount; i++) {
			workers[i].start();
		}
		
		// read file
		readFile(args[0]);
				
	    // await for latch
		try {
			latch.await();
			
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
		
		// print
		printAccounts();
		return;
		 
	}
	
	
	/* print the final status of all accounts after transactions */
	 
	private static void printAccounts() {
		for(int i = 0; i < accountsCount; i++) {
			System.out.println(accounts[i]);
		}
	}
	
	/* implement the worker class 
	 * override the run method
	 * */
	public static class Worker extends Thread {
		// get from and to of each transaction
		// modify the from and to account
		@Override
		public void run() {
//			Thread running = Thread.currentThread();
//			System.out.println(running.getName() );
			while (true) {
				try {
					Transaction tran = blockingQueue.take();
					if (tran.realTransaction()) {
						// Account from = accounts[tran.getFrom()];
						// Account to = accounts[tran.getTo()];
						// int amount = tran.getAmount();
						// from.transfer(to, amount);
						
						accounts[tran.getFrom()].inc(tran.getAmount());
						accounts[tran.getTo()].dec(tran.getAmount());

					} else {
						// hint the null transaction
						// the file end
						
						break;
					}
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
			}
			latch.countDown();
		}
	}
	
	/* read from file 
	 * setup transactions array
	 * */		
	private static void readFile(String path) {
		// read from file
		try {
			// StringBuilder sb = new StringBuilder("");
			
			// read lines from file
			File file = new File(path);
			BufferedReader reader = new BufferedReader(new FileReader(file));
			
			
			// read line by line until the end
			String line = null;
			while((line = reader.readLine()) != null) {
				// sb.append(line + "/n");
				
				// get info
				// info is String, so we have to change it into int
				String[] words = line.split(" ");
				int from = Integer.parseInt(words[0]);
				int to = Integer.parseInt(words[1]);
				int amount = Integer.parseInt(words[2]);
				Transaction tranasction = new Transaction(from, to, amount);
				try {
					// eclipse informs me to catch InterrupedExection
					blockingQueue.put(tranasction);
				} 
				catch(InterruptedException e) {
					e.printStackTrace();
				}
				
			}
			reader.close();
			
			// add nullTrans
			// we need to inform workersCount workers
			try{
				for(int i=0; i< workersCount;i++) {
					blockingQueue.put(nullTransaction);
				} 
			}
			catch(InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		catch(FileNotFoundException e) {
			e.printStackTrace();
		}
		catch(IOException e) {
			e.printStackTrace();
		}
		
	}
}
