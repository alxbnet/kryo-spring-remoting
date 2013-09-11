package net.alxb.springframework.remoting.kryo.serializers;

import java.io.ByteArrayOutputStream;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.Serializer;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;

/**
 * Abstract test base with round-trip serialization
 * 
 * @author Alex Borisov
 * 
 */
abstract class RoundtripSerializerTest<T> {

	Kryo kryo = new Kryo();
	
	T roundtripSerialization(T original, Serializer<T> serializer, Class<T> clazz) {
	
		Output output = write(original, serializer);
		T result = read(output, serializer, clazz);
		
		return result;
	}

	private T read(Output output, Serializer<T> serializer,
			Class<T> clazz) {
		Input input = new Input(output.getBuffer());
		T result;
		try {
			result = kryo.readObjectOrNull(input, clazz, serializer);
		} finally {
			input.close();
		}
		return result;
	}

	private Output write(T original, Serializer<T> serializer) {
		Output output = new Output(new ByteArrayOutputStream());
		try {
			kryo.writeObject(output, original, serializer);
		} finally {
			output.close();
		}
		return output;
	}

}
