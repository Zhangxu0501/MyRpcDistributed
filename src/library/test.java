package library;

import java.lang.reflect.Proxy;

public class test {

	public static void main(String[] args) {
		RealSubject real = new RealSubject();
		Subject s =	(Subject)Proxy.newProxyInstance(test.class.getClassLoader(),new Class []{Subject.class}, new ProxyHandler(real));
		//��һ���������⣬��������������ʽ���ڶ�����������Ϊ����Ľӿڣ���������������������ã������ýӿڵĶ�Ӧ����ʱ������inovke�����������ڸ÷�����д���Լ����߼����ɡ�
		s.doSomething();

	}

}
