package net.alxb.springframework.remoting.kryo;

import org.springframework.remoting.httpinvoker.HttpInvokerProxyFactoryBean;

/**
 * @author Alex Borisov
 * 
 */
public class KryoProxyFactoryBean extends HttpInvokerProxyFactoryBean {

	private KryoFactory kryoFactory = new DefaultKryoFactory();

	@Override
	public void afterPropertiesSet() {
		KryoHttpInvokerRequestExecutor executor = new KryoHttpInvokerRequestExecutor();
		executor.setKryoFactory(kryoFactory);
		setHttpInvokerRequestExecutor(executor);
		
		super.afterPropertiesSet();
	}

	public void setKryoFactory(KryoFactory kryoFactory) {
		this.kryoFactory = kryoFactory;
	}

}