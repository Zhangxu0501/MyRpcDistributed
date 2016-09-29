package test;

import java.net.InetAddress;

import zx.rpc.support.RPC;
import zx.rpc.utils.RPCUtils;
import zx.rpc.zkServer.ZookeeperUtils;

public class Test {

	
	public static void sop(Object o)
	{
		System.out.println(o);
	}
	public static void main(String[] args)throws Exception 
	{
		//RPCUtils.remoteRegister(Sort.class, new ArraySort(), "127.0.0.1",12345);
		Sort s = RPC.getProxy(Sort.class);
		int [] a ={1,3,8,6,3,2,4,5};
		a=s.sort(a);
		for(int i:a)
			sop(i);
	}
}
