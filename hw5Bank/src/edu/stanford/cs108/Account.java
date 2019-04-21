package edu.stanford.cs108;

public class Account {
	private int id;
	private int balance;
	private int transactionCount;
	
	/*
	 * Constructor
	 * 
	 * @param id(int)
	 * 
	 * @param balance
	 * 
	 * @param transactionCount
	 */
	public Account(int id, int balance, int transactionCount) {
		this.id = id;
		this.balance = balance;
		this.transactionCount = transactionCount;
	}
	
	/* write */
//	protected synchronized void transfer(Account other, int amount) {
//		this.balance -= amount;
//		other.balance += amount;
//		this.transactionCount ++;
//		other.transactionCount ++;
//	}
	
	/*Override toString()*/
	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder("");
		sb.append("acct:");
		sb.append(id);
		sb.append(" bal:");
		sb.append(balance);
		sb.append(" trans:");
		sb.append(transactionCount);
		return sb.toString();
	}
	
	/* write */
	public synchronized void inc(int money) {
		balance += money;
		transactionCount++;
	}
	
	public synchronized void dec(int money) {
		balance -= money;
		transactionCount++;
	}

}
