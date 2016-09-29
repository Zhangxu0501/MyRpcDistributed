package zx.rpc.protocal;

import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class AddNewClass{
	public static void main(String [] args) throws Exception
	{
		ServerSocket s = new ServerSocket(15000);
		System.out.println("µÈ´ýÁ¬½Ó");
		Socket socket =s.accept();
		ObjectInputStream ois = new ObjectInputStream(socket.getInputStream());
		Object o = ois.readObject();
		socket.close();
		java.lang.reflect.Method m = o.getClass().getMethod("sort", int [].class);
		int [] a={1,4,3,8,6};
		a=(int [])m.invoke(o,a );
		for(int i:a)
			System.out.println(i);
	}
	
	

}
