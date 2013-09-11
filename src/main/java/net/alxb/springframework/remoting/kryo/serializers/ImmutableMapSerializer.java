package net.alxb.springframework.remoting.kryo.serializers;

import java.util.Iterator;
import java.util.Map.Entry;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.ImmutableMap.Builder;

/**
 * Kryo {@link Serializer<T>} for guava {@link ImmutableMap} class
 * 
 * @author Alex Borisov
 * 
 */
public class ImmutableMapSerializer<K, V> extends
		Serializer<ImmutableMap<K, V>> {

	private Class<K> keyClass;
	private Class<V> valueClass;
	private Serializer<K> keySerializer;
	private Serializer<V> valueSerializer;
	private Class<K> keyGenericType;
	private Class<V> valueGenericType;

	public ImmutableMapSerializer() {
		setImmutable(true);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	public void setGenerics(Kryo kryo, Class[] generics) {
		if (isNotNullAndFinal(kryo, generics, 0)) {
			keyGenericType = generics[0];
		}
		if (isNotNullAndFinal(kryo, generics, 1)) {
			valueGenericType = generics[1];
		}
	}

	private boolean isNotNullAndFinal(Kryo kryo, Class<?>[] generics, int index) {
		return generics[index] != null && kryo.isFinal(generics[index]);
	}

	public void setKeyClass(Class<K> keyClass, Serializer<K> keySerializer) {
		this.keyClass = keyClass;
		this.keySerializer = keySerializer;
	}

	public void setValueClass(Class<V> valueClass, Serializer<V> valueSerializer) {
		this.valueClass = valueClass;
		this.valueSerializer = valueSerializer;
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableMap<K, V> map) {
		int length = map.size();
		output.writeInt(length, true);

		Serializer<?> keySerializer = this.keySerializer;
		if (keyGenericType != null) {
			if (keySerializer == null)
				keySerializer = kryo.getSerializer(keyGenericType);
			keyGenericType = null;
		}
		Serializer<?> valueSerializer = this.valueSerializer;
		if (valueGenericType != null) {
			if (valueSerializer == null)
				valueSerializer = kryo.getSerializer(valueGenericType);
			valueGenericType = null;
		}

		for (Iterator<Entry<K, V>> iter = map.entrySet().iterator(); iter
				.hasNext();) {
			Entry<K, V> entry = iter.next();
			if (keySerializer != null) {
				kryo.writeObject(output, entry.getKey(), keySerializer);
			} else {
				kryo.writeClassAndObject(output, entry.getKey());
			}
			if (valueSerializer != null) {
				kryo.writeObject(output, entry.getValue(), valueSerializer);
			} else {
				kryo.writeClassAndObject(output, entry.getValue());
			}
		}

	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableMap<K, V> read(Kryo kryo, Input input,
			Class<ImmutableMap<K, V>> type) {
		Builder<K, V> builder = ImmutableMap.builder();

		int length = input.readInt(true);

		Class<K> keyClass = this.keyClass;
		Class<V> valueClass = this.valueClass;

		Serializer<K> keySerializer = this.keySerializer;
		if (keyGenericType != null) {
			keyClass = keyGenericType;
			if (keySerializer == null) {
				keySerializer = kryo.getSerializer(keyClass);
			}
			keyGenericType = null;
		}
		Serializer<V> valueSerializer = this.valueSerializer;
		if (valueGenericType != null) {
			valueClass = valueGenericType;
			if (valueSerializer == null) {
				valueSerializer = kryo.getSerializer(valueClass);
			}
			valueGenericType = null;
		}

		for (int i = 0; i < length; i++) {
			K key;
			if (keySerializer != null) {
				key = kryo.readObject(input, keyClass, keySerializer);
			} else {
				key = (K) kryo.readClassAndObject(input);
			}

			V value;
			if (valueSerializer != null) {
				value = kryo.readObject(input, valueClass, valueSerializer);
			} else {
				value = (V) kryo.readClassAndObject(input);
			}

			builder.put(key, value);
		}
		ImmutableMap<K, V> map = builder.build();
		kryo.reference(map);
		return map;
	}
}
