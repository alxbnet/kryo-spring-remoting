package net.alxb.springframework.remoting.kryo;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.InitializingBean;
import org.springframework.remoting.support.RemoteInvocation;
import org.springframework.remoting.support.RemoteInvocationBasedExporter;
import org.springframework.remoting.support.RemoteInvocationResult;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.util.NestedServletException;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * 
 * @author Alex Borisov
 *
 */
public class KryoServiceExporter extends RemoteInvocationBasedExporter
		implements HttpRequestHandler, InitializingBean {

	public static final String CONTENT_TYPE_SERIALIZED_OBJECT = "application/x-kryo";

	private String contentType = CONTENT_TYPE_SERIALIZED_OBJECT;
	private KryoFactory kryoFactory = new DefaultKryoFactory();

	private Object proxy;

	@Override
	public void handleRequest(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		try {
			RemoteInvocation invocation = readRemoteInvocation(request);
			RemoteInvocationResult result = invokeAndCreateResult(invocation, getProxy());
			writeRemoteInvocationResult(request, response, result);
		}
		catch (ClassNotFoundException ex) {
			throw new NestedServletException("Class not found during deserialization", ex);
		}
	}

	private RemoteInvocation readRemoteInvocation(HttpServletRequest request)
			throws IOException, ClassNotFoundException {
		Input input = new Input(request.getInputStream());
		try {
			Kryo kryo = kryoFactory.create();
			RemoteInvocation invocation = kryo.readObjectOrNull(input, RemoteInvocation.class);
			return invocation;
		} finally {
			input.close();
		}
	}

	private void writeRemoteInvocationResult(
			HttpServletRequest request, HttpServletResponse response, RemoteInvocationResult result)
			throws IOException {

		response.setContentType(getContentType());
		Output output = new Output(response.getOutputStream());
		try {
			Kryo kryo = kryoFactory.create();
			kryo.writeObject(output, result);
		} finally {
			output.close();
		}
	}
	
	@Override
	protected RemoteInvocationResult invokeAndCreateResult(RemoteInvocation invocation, Object targetObject) {
		try {
			Object value = invoke(invocation, targetObject);
			return new KryoRemoteInvocationResult(value);
		}
		catch (Throwable ex) {
			return new KryoRemoteInvocationResult(ex);
		}
	}	

	@Override
	public void afterPropertiesSet() {
		initProxy();
	}

	public void initProxy() {
		this.proxy = getProxyForService();
	}

	private final Object getProxy() {
		return this.proxy;
	}
	
	public void setContentType(String contentType) {
		this.contentType = contentType;
	}

	public String getContentType() {
		return this.contentType;
	}
	
	public void setKryoFactory(KryoFactory kryoFactory) {
		this.kryoFactory = kryoFactory;
	}	

}
