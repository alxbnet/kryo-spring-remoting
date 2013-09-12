package net.alxb.springframework.remoting.kryo;

import java.util.Map;
import java.util.Map.Entry;

import org.objenesis.instantiator.ObjectInstantiator;
import org.objenesis.strategy.InstantiatorStrategy;
import org.springframework.remoting.support.RemoteInvocation;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Registration;
import com.esotericsoftware.kryo.Serializer;

/**
 * @author Alex Borisov
 * 
 */
public class DefaultKryoFactory implements KryoFactory {

	public static final int REMOTE_INVOCATION_REGISTRATION_ID = 100;
	public static final int REMOTE_INVOCATION_RESULT_REGISTRATION_ID = 101;

	private InstantiatorStrategy instantiatorStrategy;
	private Map<Class<?>, ObjectInstantiator> instantiators;
	private Map<Class<?>, Serializer<?>> serializers;
	
	@Override
	public Kryo create() {
		Kryo kryo = new Kryo();
		
		kryo.register(RemoteInvocation.class, REMOTE_INVOCATION_REGISTRATION_ID);
		kryo.register(KryoRemoteInvocationResult.class,
				REMOTE_INVOCATION_RESULT_REGISTRATION_ID);

		applyInstantiationStrategy(kryo);
		register(kryo);
				
		return kryo;
	}

	/** 
	 * Instantiators can be registered to instantiate classes w/o no-arg 
	 * constructor (e.g. immutable classes).  
	 */
	public void setInstantiators(Map<Class<?>, ObjectInstantiator> instantiators) {
		this.instantiators = instantiators;
	}

	public void setSerializers(Map<Class<?>, Serializer<?>> serializers) {
		this.serializers = serializers;
	}	
	
	public void setInstantiatorStrategy(InstantiatorStrategy instantiatorStrategy) {
		this.instantiatorStrategy = instantiatorStrategy;
	}

	private void applyInstantiationStrategy(Kryo kryo) {
		if (instantiatorStrategy != null) {
			kryo.setInstantiatorStrategy(instantiatorStrategy);
		}
	}

	private void register(Kryo kryo) {
		if (serializers != null) {
			registerSerializers(kryo);
		}
		if (instantiators != null) {
			registerInstantiators(kryo);			
		}
	}

	private void registerSerializers(Kryo kryo) {
		for (Entry<Class<?>, Serializer<?>> entry : serializers.entrySet()) {
			
			Class<?> clazz = entry.getKey();
			Serializer<?> serializer = entry.getValue();
								
			registerSerializer(kryo, clazz, serializer);
		}
	}

	private void registerInstantiators(Kryo kryo) {
		for (Entry<Class<?>, ObjectInstantiator> entry : instantiators.entrySet()) {
			
			Class<?> clazz = entry.getKey();
			ObjectInstantiator instantiator = entry.getValue();
			
			registerInstantiator(kryo, clazz, instantiator);
		}
	}

	private void registerSerializer(Kryo kryo, Class<?> clazz,
			Serializer<?> serializer) {
		kryo.addDefaultSerializer(clazz, serializer);
	}

	private void registerInstantiator(Kryo kryo, Class<?> clazz,
			ObjectInstantiator instantiator) {
		
		int registrationId = kryo.getNextRegistrationId();
		Registration registration = new 
				Registration(clazz, kryo.getDefaultSerializer(clazz), registrationId);
		registration.setInstantiator(instantiator);
		kryo.register(registration);
	}
	
}