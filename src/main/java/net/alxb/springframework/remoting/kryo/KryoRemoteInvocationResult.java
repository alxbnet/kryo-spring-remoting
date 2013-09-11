package net.alxb.springframework.remoting.kryo;

import org.springframework.remoting.support.RemoteInvocationResult;

/**
 * Adds no-arg constructor
 * 
 * @author Alex Borisov
 *
 */
public class KryoRemoteInvocationResult extends RemoteInvocationResult {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6595601136896669395L;

	public KryoRemoteInvocationResult() {
		super(null);
	}

	public KryoRemoteInvocationResult(Object value) {
		super(value);
	}

}