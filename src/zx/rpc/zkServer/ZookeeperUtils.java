package zx.rpc.zkServer;


import java.net.InetAddress;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CountDownLatch;

import org.apache.zookeeper.CreateMode;
import org.apache.zookeeper.KeeperException;
import org.apache.zookeeper.WatchedEvent;
import org.apache.zookeeper.Watcher;
import org.apache.zookeeper.Watcher.Event.KeeperState;
import org.apache.zookeeper.ZooDefs.Ids;
import org.apache.zookeeper.ZooKeeper;
import org.apache.zookeeper.data.Stat;

import zx.rpc.support.RPCServer;
import zx.rpc.utils.RPCUtils;

public class ZookeeperUtils implements Watcher{
	private static String servicePath=null;
	public static int port=RPCServer.port;
	private static final int TIMEOUT=5000;
	private static final boolean String = false;
	protected static ZooKeeper zk;
	private static ZookeeperUtils z=new ZookeeperUtils();
	private static CountDownLatch connectedSignal = new CountDownLatch(1);
	private static String ip = null;
	static{
		try {
			ip=InetAddress.getLocalHost().getHostAddress();
			connect("zx0");
			Stat s =zk.exists("/RPCServer", z);
			if(s==null)
				zk.create("/RPCServer", null,Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			servicePath="/RPCServer"+"/"+ip+"--"+port;
			s =zk.exists(servicePath, z);
			if(s==null)
				zk.create(servicePath, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);
			
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public static void connect(String host)throws Exception	//创建一个zk实例，用于维护客户端和zk集群之间的连接
	{
		zk=new ZooKeeper(host,TIMEOUT,z);//host地址，回话超时参数，Watcher对象实例，Watcher用于接受zk的回调消息，来获得事件的通知
		//这里会建立一个线程与zk关联来完成连接
	}
	
	@Override
	public void process(WatchedEvent event) 
	{//Watcher类的实现方法，当服务建立成功后，这个方法会被立即执行。运行完process后，connect方法才会返回
		if(event.getState()==KeeperState.SyncConnected)
			connectedSignal.countDown();//计数器变为0，await()方法返回
	}
	

	public static void close()throws Exception
	{
		zk.close();
	}
	
	public static void create(String groupName)throws Exception
	{
		String path="/"+groupName;
		String createPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);//data如果有数据应该是字节数组
		//数据，访问列表以及是否允许客户端对znode读写操作，接下来说明连接是短暂的还是持久的
		//短暂的连接znode失去连接是会被删除，持久的不论什么原因都不会删除
		System.out.println("CreateD" + createPath);
	}
	
	public static void join(String groupName,String memberName) throws Exception
	{
		String path="/"+groupName+"/"+memberName;
		zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);// Ids.OPEN_ACL_UNSAFE将所有权限给每个人。
	}
	public static void main(String args[]) throws Exception
	{	
		//create("RPCServer");
		Stat s =zk.exists("/RPCServera", z);
		System.out.println(s);
	}
	
	public static void addService(ConcurrentHashMap<java.lang.String, Object> serviceEngine)
	{
		try{
			zk.setData(servicePath, RPCUtils.ObjectToBytes(serviceEngine), -1);//-1跳过版本检查。
			}catch(Exception e)
			{
				e.printStackTrace();
			}
	}
	public static ConcurrentHashMap<String,Object> getService()
	{
		try
		{
			return (ConcurrentHashMap<String,Object>)RPCUtils.BytesToObject(zk.getData(servicePath, false, new Stat()));
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return null;
	}
	public static void Clear() 
	{
		try
		{
			List<String> hosts = zk.getChildren("/RPCServer",false );
			for(String s:hosts)
			{
				zk.delete("/RPCServer/"+s, -1);
			}
			zk.delete("/RPCServer", -1);
			zk.close();
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		{
			
		}
	}
	public static void deleteMy()
	{
		try {
			zk.delete(servicePath, -1);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (KeeperException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

