package net.alxb.springframework.remoting.kryo.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableList.Builder;

/**
 * Kryo {@link Serializer<T>} for guava {@link ImmutableList} class
 * 
 * @author Alex Borisov
 * 
 */
public class ImmutableListSerializer<E> extends Serializer<ImmutableList<E>> {

	private Serializer<E> serializer;
	private Class<E> elementClass;
	private Class<E> genericType;

	public ImmutableListSerializer() {
		setImmutable(true);
	}

	public void setElementClass(Class<E> elementClass, Serializer<E> serializer) {
		this.elementClass = elementClass;
		this.serializer = serializer;
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void setGenerics(Kryo kryo, Class[] generics) {
		if (kryo.isFinal(generics[0])) {
			genericType = generics[0];
		}
	}

	@Override
	public void write(Kryo kryo, Output output, ImmutableList<E> collection) {
		int length = collection.size();
		output.writeInt(length, true);
		Serializer<?> serializer = this.serializer;
		if (genericType != null) {
			if (serializer == null) {
				serializer = kryo.getSerializer(genericType);
			}
			genericType = null;
		}
		if (serializer != null) {
			for (Object element : collection) {
				kryo.writeObject(output, element, serializer);
			}
		} else {
			for (Object element : collection)
				kryo.writeClassAndObject(output, element);
		}
	}

	@SuppressWarnings("unchecked")
	@Override
	public ImmutableList<E> read(Kryo kryo, Input input,
			Class<ImmutableList<E>> type) {

		int length = input.readInt(true);
		Builder<E> builder = ImmutableList.builder();
		
		Class<E> elementClass = this.elementClass;
		Serializer<E> serializer = this.serializer;
		if (genericType != null) {
			if (serializer == null) {
				elementClass = genericType;
				serializer = kryo.getSerializer(genericType);
			}
			genericType = null;
		}
		if (serializer != null) {
			for (int i = 0; i < length; i++) {
				builder.add(kryo.readObject(input, elementClass, serializer));
			}
		} else {
			for (int i = 0; i < length; i++)
				builder.add((E)kryo.readClassAndObject(input));
		}

		ImmutableList<E> result = builder.build();
		kryo.reference(result);

		return result;
	}
}