package net.alxb.springframework.remoting.kryo.serializers;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.google.common.util.concurrent.AtomicDouble;

/**
 * Kryo {@link Serializer<T>} for {@link AtomicDouble} class
 * 
 * @author Alex Borisov
 *
 */
public class AtomicDoubleSerializer extends Serializer<AtomicDouble> {

	@Override
	public void write(Kryo kryo, Output output, AtomicDouble object) {
		double value = object.get();
		output.writeDouble(value);
	}

	@Override
	public AtomicDouble read(Kryo kryo, Input input, Class<AtomicDouble> type) {
		double value = input.readDouble();
		return new AtomicDouble(value);
	}

}
