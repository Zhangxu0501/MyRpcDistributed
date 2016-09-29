package zx.rpc.support;

import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import zx.rpc.protocal.Invocation;
import zx.rpc.utils.RPCUtils;


public class RPC implements Watcher{
	private static ZooKeeper zk=null;
	private static RPC r = new RPC();
	private static HashMap<String,Set<String>> servicePlace=new HashMap<String,Set<String>>();
	private static List<String> childern=null;
	private RPC()
	{
	}
	
	static
	{
		try {
			zk = new ZooKeeper("zx0", 5000, r);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		try {
			childern=zk.getChildren("/RPCServer", false);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		for(String child:childern)
		{
			try {
				String host=child;
				child="/RPCServer/"+child;
				byte [] bytes=zk.getData(child, false, new Stat());
				ConcurrentHashMap<String, Object> services=(ConcurrentHashMap<String, Object>) RPCUtils.BytesToObject(bytes);
				for(String serviceName:services.keySet())
				{
					if(servicePlace.containsKey(serviceName))
					{
						Set<String> hosts=servicePlace.get(serviceName);
						hosts.add(host);
					}
					else
					{
						Set<String> hosts=new HashSet<String>();
						hosts.add(host);
						servicePlace.put(serviceName, hosts);
					}
				}
				
			} catch (Exception e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}
	}
	
	
	private static CountDownLatch connectedSignal = new CountDownLatch(1);
	
	public static <T> T getProxy(final Class<T> clazz) throws ClassNotFoundException 
	{
		Set<String> hosts=servicePlace.get(RPCUtils.getInterfaceName(clazz.getName()));
		String host;
		int port;
		if(hosts==null)
		{
			throw new ClassNotFoundException("不支持这个服务");
		}
		else
		{
			Iterator<String> it =hosts.iterator();
			String host__port=it.next();
			String [] host_port= host__port.split("--");
			host=host_port[0];
			port=Integer.parseInt(host_port[1]);
					
		}
		final Client client = new Client(host,port);
		InvocationHandler handler = new InvocationHandler() {
			@Override
			public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
				//调用代理对象的方法的时候，以上三个参数分别对应，interface对象，调用的方法，传入的参数
				//将method封装到invo中
				Invocation invo = new Invocation();
				invo.setInterfaces(clazz);
				invo.setMethod(new zx.rpc.protocal.Method(method.getName(),method.getParameterTypes()));
				invo.setParams(args);
				client.invoke(invo);
				return invo.getResult();
			}
		};
		T t = (T) Proxy.newProxyInstance(RPC.class.getClassLoader(), new Class[] {clazz}, handler);
		return t;
	}

	
	@Override
	public void process(WatchedEvent event) {
		if(event.getState()==KeeperState.SyncConnected)
			connectedSignal.countDown();//计数器变为0，await()方法返回
	}


	
	
	
}
	



	
	
	

