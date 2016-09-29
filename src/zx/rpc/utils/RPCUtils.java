package zx.rpc.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Map;

import zx.rpc.protocal.RemoteObject;

public class RPCUtils {
	
	public static Object BytesToObject(byte [] bytes)throws IOException, ClassNotFoundException
	{
		Object o=null;
		if(bytes!=null)
		{
			ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
			ObjectInputStream ois = new ObjectInputStream(bis);
			o=ois.readObject();
			bis.close();
			ois.close();
		}
		return o;
	}
	public static byte [] ObjectToBytes(Object o)throws IOException
	{
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		ObjectOutputStream oos = new ObjectOutputStream(baos);
		oos.writeObject(o);
		oos.flush();
		byte [] bytes = baos.toByteArray();
		baos.close();
		oos.close();
		return bytes;
	}
	public static void remoteRegister(Class inter,Object impl,String ip,int port) throws UnknownHostException, IOException, InstantiationException, IllegalAccessException 
	{
		Socket s = new Socket(ip,port);
		ObjectOutputStream oos =  new ObjectOutputStream(s.getOutputStream());
		oos.writeObject(new RemoteObject(impl,inter));
		s.close();
	}
	public static String getInterfaceName(String s)
	{
		String a[] =s.split("\\.");
		return a[a.length-1];
	}
	public static void save(String interfaceName,Object o)
	{
		File file =new File("\\RPCServerCatch");    
		//如果文件夹不存在则创建    
		if  (!file .exists()  && !file .isDirectory())      
		{       
		   System.out.println("目录不存在，新创建");  
		    file .mkdir();    
		}
		file=new File("\\RPCServerCatch\\"+interfaceName+".catch");
		try
		{
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(file));
			oos.writeObject(o);
			oos.close();
			System.out.println("写入磁盘成功！"+file.getAbsolutePath());
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	public static void loadCatch(Map<String ,Object> serviceEngine)
	{
		
	}
	
}
