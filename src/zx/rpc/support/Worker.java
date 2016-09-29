package zx.rpc.support;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import zx.rpc.protocal.Invocation;

public class Worker implements Runnable {
	
	public Worker(ObjectInputStream ois,ObjectOutputStream oos,Server server)
	{
		this.ois=ois;
		this.oos=oos;
		this.server=server;
	}
	private Server server;
	private ObjectInputStream ois;
	private ObjectOutputStream oos;
	
	@Override
	public void run() {
		// TODO Auto-generated method stub
		try
		{
			//���ݾ�����Ż����������ǲ��Ƿ����µ�invo
			Invocation invo = (Invocation)ois.readObject();
			System.out.println("Զ�̵���:" + invo);
			server.call(invo);
			oos.writeObject(invo);
			oos.flush();
			oos.close();
			ois.close();
		}
		catch(Exception e)
		{
			try{
				e.printStackTrace();
				oos.close();
				ois.close();
			}catch(Exception e1)
			{
				e1.printStackTrace();
			}
			
		}
		
		
		
	}

}
