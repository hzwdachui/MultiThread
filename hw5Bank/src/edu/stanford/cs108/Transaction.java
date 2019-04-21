package edu.stanford.cs108;

public class Transaction {
	private int from;
	private int to;
	private int amount;
	
	/*
	 * Constructor
	 * 
	 * @param from: the from account id
	 * 
	 * @param to: the to account id
	 * 
	 * @param amount: the amount of money of a transaction
	 */
	public Transaction(int from, int to, int amount) {
		this.from = from;
		this.to = to;
		this.amount = amount;
	}
	
	/* to check if it is nullTrans or not */
	public Boolean realTransaction() {
		return !(from == -1);
	}
	
	/* return the id of from */
	protected int getFrom() {
		return from;
	}
	
	protected int getTo() {
		return to;
	}
	
	protected int getAmount() {
		return amount;
	}

}
