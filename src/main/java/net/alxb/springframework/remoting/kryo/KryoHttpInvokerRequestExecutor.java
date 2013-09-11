package net.alxb.springframework.remoting.kryo;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import org.springframework.remoting.httpinvoker.CommonsHttpInvokerRequestExecutor;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationResult;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * @author Alex Borisov
 * 
 */
public class KryoHttpInvokerRequestExecutor extends
		CommonsHttpInvokerRequestExecutor {

	private KryoFactory kryoFactory = new DefaultKryoFactory();
	
	public String getContentType() {
		return KryoServiceExporter.CONTENT_TYPE_SERIALIZED_OBJECT;
	}

	@Override
	protected void writeRemoteInvocation(RemoteInvocation invocation,
			OutputStream os) throws IOException {
		Kryo kryo = kryoFactory.create();		
		Output output = new Output(os);
		try {
			kryo.writeObject(output, invocation);
		} finally {
			output.close();
		}
	}

	@Override
	protected RemoteInvocationResult readRemoteInvocationResult(InputStream is,
			String codebaseUrl) throws IOException, ClassNotFoundException {
		Kryo kryo = kryoFactory.create();		
		Input input = new Input(is);
		try {
			KryoRemoteInvocationResult result = kryo.readObjectOrNull(input,
					KryoRemoteInvocationResult.class);
			return result;
		} finally {
			input.close();
		}
	}

	public void setKryoFactory(KryoFactory kryoFactory) {
		this.kryoFactory = kryoFactory;
	}
	
}