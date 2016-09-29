package test;

import java.io.Serializable;
import java.util.Arrays;

public class ArraySort implements Sort,Serializable {
	private static final long serialVersionUID = 1L;
	public int [] sort(int [] a)
	{
		Arrays.sort(a);
		return a;
	}
	
	@Override
	public String sortt(String s) {
		return "来自服务器:"+s;
	}
}
