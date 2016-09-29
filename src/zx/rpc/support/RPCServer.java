package zx.rpc.support;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import zx.rpc.protocal.Invocation;
import zx.rpc.utils.RPCUtils;
import zx.rpc.zkServer.ZookeeperUtils;

public  class RPCServer implements Server{
	
	/*
	 * server的实现类
	 * 实现了如下方法
	 * 	
		public void start();//开启一个listener线程，打开服务器端口，不断地执行   接收请求---回调 过程，直到服务器的isRunning为false。
		public void register(Class interfaceDefiner,Class impl);	//注册对象及其 实现类。 并且new一个对象放入一个hashmap中，以便调用时使用。
		public void call(Invocation invo);//在listenner中进行调用，其中Invocation是由客户端传过来的对象流。在listener中执行。执行过程如下
			首先在serviceEngine中拿到实体对象。
		
		public boolean isRunning();
		public int getPort();
		public void stop();把running设置为false
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
		
		//得拿到类名，而不可以拿到带包的名字。
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
		System.out.println("启动服务器");
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
			System.out.println("已存在该接口,覆盖原有对象");
			serviceEngine.remove(interfaceName);
			serviceEngine.put(interfaceName, object);
		}
		else
		{
			serviceEngine.put(interfaceName, object);
		}
		ZookeeperUtils.addService(serviceEngine);
		System.out.println("注册成功");
	}
	
}
