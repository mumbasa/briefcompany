package com.akoo.data;

import java.util.Calendar;

public class Hasher {

	public static void main(String[] args) {
	Calendar g =Calendar.getInstance();
		int a =g.get(Calendar.MONTH);
		String as =String.format("%02d", a);
		System.out.println(as);
	}
	
}
