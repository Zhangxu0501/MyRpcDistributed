package zx.rpc.support;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import zx.rpc.protocal.RemoteObject;

public class RemoteRegisterService extends Thread {

	private Server server;
	private int port;
	public RemoteRegisterService(Server server,int port)
	{
		this.server=server;
		this.port=port;
	}
	@Override
	public void run(){
		ServerSocket s = null;
		System.out.println("监听远程对象注册服务器启动");
		try {
			s = new ServerSocket(port);
		} catch (IOException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		try
		{
			while(server.isRunning())
			{
				System.out.println("监听远程对象注册中..."+"端口为："+port);
				Socket socket=s.accept();
				ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
				RemoteObject o = (RemoteObject)ois.readObject();
				server.remoteRegister(o.interfaceName,o.o);
				socket.close();
			}
			System.out.println("远程对象注册服务关闭");
		}
		catch(IOException e)
		{
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}

}
