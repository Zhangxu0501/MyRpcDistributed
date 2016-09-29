package zx.rpc.support;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zx.rpc.protocal.Invocation;
import zx.rpc.utils.RPCUtils;
import zx.rpc.zkServer.ZookeeperUtils;

public  class RPCServer implements Server{
	
	/*
	 * server��ʵ����
	 * ʵ�������·���
	 * 	
		public void start();//����һ��listener�̣߳��򿪷������˿ڣ����ϵ�ִ��   ��������---�ص� ���̣�ֱ����������isRunningΪfalse��
		public void register(Class interfaceDefiner,Class impl);	//ע������� ʵ���ࡣ ����newһ���������һ��hashmap�У��Ա����ʱʹ�á�
		public void call(Invocation invo);//��listenner�н��е��ã�����Invocation���ɿͻ��˴������Ķ���������listener��ִ�С�ִ�й�������
			������serviceEngine���õ�ʵ�����
		
		public boolean isRunning();
		public int getPort();
		public void stop();��running����Ϊfalse
	 */
	public static int port = 20382;
	private Listener listener; 
	private boolean isRuning = true;
	private static ConcurrentHashMap<String ,Object> serviceEngine = new ConcurrentHashMap<String, Object>();
	/**
	 * @param isRuning the isRuning to set
	 */
	public void setRuning(boolean isRuning) {
		this.isRuning = isRuning;
	}

	/**
	 * @return the port
	 */
	public int getPort() {
		return port;
	}

	/**
	 * @param port the port to set
	 */
	public void setPort(int port) {
		this.port = port;
	}

	
	
	
	@Override
	public void call(Invocation invo) {
		
		//���õ����������������õ����������֡�
		String ObjectName=RPCUtils.getInterfaceName(invo.getInterfaces().getName());
		Object obj = serviceEngine.get(ObjectName);
		
		if(obj!=null) {
			try {
				Method m = obj.getClass().getMethod(invo.getMethod().getMethodName(), invo.getMethod().getParams());
				Object result = m.invoke(obj, invo.getParams());
				invo.setResult(result);
			} catch (Throwable th) {
				th.printStackTrace();
			}
		} else {
			throw new IllegalArgumentException("has no these class");
		}
	}

	@Override
	public void register(Class interfaceDefiner, Class impl) {
		try {
			serviceEngine.put(RPCUtils.getInterfaceName(interfaceDefiner.getName()), impl.newInstance());
			System.out.println(serviceEngine);
		} catch (Throwable e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} 
	}

	@Override
	public void start() {
		System.out.println("����������");
		//RPCUtils.loadCatch(this.serviceEngine);
		listener = new Listener(this);
		this.isRuning = true;
		ConcurrentHashMap<String, Object> remoteEngine=ZookeeperUtils.getService();
		if(remoteEngine!=null)
		{
			remoteEngine.putAll(serviceEngine);
			serviceEngine=remoteEngine;
		}
	
		ZookeeperUtils.addService(serviceEngine);
		RemoteRegisterService rrs = new RemoteRegisterService(this,12345);
		rrs.start();
		listener.start();
	}

	@Override
	public void stop() {
		this.setRuning(false);
	}

	@Override
	public boolean isRunning() {
		return isRuning;
	}

	@Override
	public void remoteRegister(String interfaceName, Object object) {
		if(serviceEngine.containsKey(interfaceName))
		{
			System.out.println("�Ѵ��ڸýӿ�,����ԭ�ж���");
			serviceEngine.remove(interfaceName);
			serviceEngine.put(interfaceName, object);
		}
		else
		{
			serviceEngine.put(interfaceName, object);
		}
		ZookeeperUtils.addService(serviceEngine);
		System.out.println("ע��ɹ�");
	}
	
}
