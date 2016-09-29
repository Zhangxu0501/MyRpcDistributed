package zx.rpc.protocal;

import java.io.Serializable;

import zx.rpc.utils.RPCUtils;

public class RemoteObject implements Serializable {
	private static final long serialVersionUID = 1L;
	public Object o;
	public String interfaceName;
	public RemoteObject(Object o,Class s)
	{
		this.interfaceName=RPCUtils.getInterfaceName(s.getName());
		this.o=o;
	}
	public RemoteObject(Object o)
	{
		this.o=o;
	}

}
