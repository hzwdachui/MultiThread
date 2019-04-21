package edu.stanford.cs108;

import java.security.*;
import java.util.*;
import java.util.concurrent.CountDownLatch;

public class Cracker {
	// Array of chars used to produce strings
	public static final char[] CHARS = "abcdefghijklmnopqrstuvwxyz0123456789.,-!".toCharArray();

	/*
	 * Given a byte[] array, produces a hex String, such as "234a6f". with 2 chars
	 * for each byte in the array. (provided code)
	 */
	public static String hexToString(byte[] bytes) {
		StringBuffer buff = new StringBuffer();
		for (int i = 0; i < bytes.length; i++) {
			int val = bytes[i];
			val = val & 0xff; // remove higher bits, sign
			if (val < 16)
				buff.append('0'); // leading 0
			buff.append(Integer.toString(val, 16));
		}
		return buff.toString();
	}

	/*
	 * Given a string of hex byte values such as "24a26f", creates a byte[] array of
	 * those values, one byte value -128..127 for each 2 chars. (provided code)
	 */
	public static byte[] hexToArray(String hex) {
		byte[] result = new byte[hex.length() / 2];
		for (int i = 0; i < hex.length(); i += 2) {
			result[i / 2] = (byte) Integer.parseInt(hex.substring(i, i + 2), 16);
		}
		return result;
	}

	// possible test values:
	// a ca978112ca1bbdcafac231b39a23dc4da786eff8147c4e72b9807785afee48bb
	// fm 440f3041c89adee0f2ad780704bcc0efae1bdb30f8d77dc455a2f6c823b87ca0
	// a! 242ed53862c43c5be5f2c5213586d50724138dea7ae1d8760752c91f315dcd31
	// xyz 3608bca1e44ea6c4d268eb6db02260269892c0b42b86bbf1e77a6fa16c3c9282

	private static List<String> hackResult = Collections.synchronizedList(new ArrayList<String>());
	private static String password;
	private static CountDownLatch latch;
	private static byte[] shaArray;
	private static int maxLen;
	private static int workersCount;

	public static void main(String[] args) {
		if (args.length == 1) {
			// generation mode
			password = args[0];
			System.out.println(hexToString(generate(password)));
		} else if (args.length == 3) {
			// cracking mode
			hackResult = new ArrayList<String>();
			shaArray = hexToArray(args[0]);
			maxLen = Integer.parseInt(args[1]);
			workersCount = Integer.parseInt(args[2]);
			latch = new CountDownLatch(workersCount);

			// create and start workers
			Worker[] workers = new Worker[workersCount];
			int segment = CHARS.length / workersCount; // divide work to workers
			for (int i = 0; i < workersCount - 1; i++) {
				workers[i] = new Worker(i * segment, (i + 1) * segment);
			}
			// the remain will be put in the last worker
			workers[workersCount - 1] = new Worker((workersCount - 1) * segment, CHARS.length);

			for (int i = 0; i < workersCount; i++) {
				workers[i].start();
			}

			// print all results
			try {
				latch.await();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			for (String res : hackResult) {
				System.out.println(res);
			}
			System.out.println("all done");

		} else {
			System.out.println("Invalid input");
		}
		return;
	}

	/* Define Thread */
	public static class Worker extends Thread {
		private int start;
		private int end;

		public Worker(int start, int end) {
			this.start = start;
			this.end = end;
		}

		/* Override run */
		@Override
		public void run() {
			String resString = "";
			crack(resString, 0);
			latch.countDown();
		}

		// don't need lock because they don't share memory
		private void crack(String resString, int pos) {
			if (pos > maxLen) {
				return;
			}

			if (Arrays.equals(shaArray, generate(resString))) {
				// System.out.println(resString);
				hackResult.add(resString);
			}
			// the head of a string
			if (pos == 0) {
				for (int i = start; i < end; i++) {
					crack(resString + CHARS[i], pos + 1);
				}
			} else {
				// the rest of the string
				for (int i = 0; i < CHARS.length; i++) {
					crack(resString + CHARS[i], pos + 1);
				}
			}
		}
	}

	/*
	 * pass in the password generate the hash, return type is byte[]
	 * 
	 * @param: String
	 */
	public static byte[] generate(String password) {
		byte[] result = null;
		try {
			MessageDigest md = MessageDigest.getInstance("SHA-256");
			md.update(password.getBytes()); // change password into byte[]
			result = md.digest();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}

		return result;
	}

}
