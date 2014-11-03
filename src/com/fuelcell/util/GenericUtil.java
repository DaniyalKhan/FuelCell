package com.fuelcell.util;

import java.lang.reflect.Array;

public class GenericUtil {

	@SuppressWarnings("unchecked")
	public static <T> T[] arrayMerge(T[] a1, T[] a2) {
		if (a1.length == 0) return a2;
		if (a2.length == 0) return a1;
		T[] a3 = (T[]) Array.newInstance(a1[0].getClass(), a1.length + a2.length);
		System.arraycopy(a1, 0, a3, 0, a1.length);
		System.arraycopy(a2, 0, a3, a1.length, a2.length);
		return a3;
	}

}
