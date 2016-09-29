package test;

import zx.rpc.support.RPCServer;
import zx.rpc.support.Server;
import zx.rpc.zkServer.ZookeeperUtils;


public class Main {
	public static void main(String[] args) {
		Server server = new RPCServer();
		server.start();
		
	}
}
