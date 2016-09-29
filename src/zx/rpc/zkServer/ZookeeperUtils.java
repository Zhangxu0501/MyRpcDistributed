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
	public static void connect(String host)throws Exception	//����һ��zkʵ��������ά���ͻ��˺�zk��Ⱥ֮�������
	{
		zk=new ZooKeeper(host,TIMEOUT,z);//host��ַ���ػ���ʱ������Watcher����ʵ����Watcher���ڽ���zk�Ļص���Ϣ��������¼���֪ͨ
		//����Ὠ��һ���߳���zk�������������
	}
	
	@Override
	public void process(WatchedEvent event) 
	{//Watcher���ʵ�ַ��������������ɹ�����������ᱻ����ִ�С�������process��connect�����Ż᷵��
		if(event.getState()==KeeperState.SyncConnected)
			connectedSignal.countDown();//��������Ϊ0��await()��������
	}
	

	public static void close()throws Exception
	{
		zk.close();
	}
	
	public static void create(String groupName)throws Exception
	{
		String path="/"+groupName;
		String createPath = zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.PERSISTENT);//data���������Ӧ�����ֽ�����
		//���ݣ������б��Լ��Ƿ�����ͻ��˶�znode��д������������˵�������Ƕ��ݵĻ��ǳ־õ�
		//���ݵ�����znodeʧȥ�����ǻᱻɾ�����־õĲ���ʲôԭ�򶼲���ɾ��
		System.out.println("CreateD" + createPath);
	}
	
	public static void join(String groupName,String memberName) throws Exception
	{
		String path="/"+groupName+"/"+memberName;
		zk.create(path, null, Ids.OPEN_ACL_UNSAFE, CreateMode.EPHEMERAL);// Ids.OPEN_ACL_UNSAFE������Ȩ�޸�ÿ���ˡ�
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
			zk.setData(servicePath, RPCUtils.ObjectToBytes(serviceEngine), -1);//-1�����汾��顣
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

