package com.dynatrace.http;

public class HttpMain {

	public static void main(String[] args) {
		HttpMain httpMain = new HttpMain();
		try {
			httpMain.execute(args);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void execute(String[] args) throws Exception {
		for (String arg : args) {
			System.out.println(arg);
		}
//		if (true) {
//			return;
//		}
//		if ((args == null) || (args.length == 0)) {
//			throw new Exception();
//		}
//		String command = args[0];
	}

}
