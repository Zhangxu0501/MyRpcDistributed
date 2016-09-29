package library;

import java.lang.reflect.Proxy;

public class test {

	public static void main(String[] args) {
		RealSubject real = new RealSubject();
		Subject s =	(Subject)Proxy.newProxyInstance(test.class.getClassLoader(),new Class []{Subject.class}, new ProxyHandler(real));
		//第一个参数随意，但必须是这种形式，第二个参数必须为代理的接口，第三个参数用来处理调用，当调用接口的对应方法时，调用inovke方法，我们在该方法中写入自己的逻辑即可。
		s.doSomething();

	}

}
