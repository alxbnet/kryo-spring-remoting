package net.alxb.springframework.remoting.kryo.serializers;

import static junitparams.JUnitParamsRunner.$;
import static org.fest.assertions.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.esotericsoftware.kryo.Serializer;
import com.google.common.util.concurrent.AtomicDouble;

@RunWith(JUnitParamsRunner.class)
public class AtomicDoubleSerializerTest extends
		RoundtripSerializerTest<AtomicDouble> {

	private static final double COMPARATION_PRECISION = .0001d;
	Serializer<AtomicDouble> serializer = new AtomicDoubleSerializer();

	@Parameters(method = "getData")
	@Test
	public void roundtripSerialization(AtomicDouble original) {

		AtomicDouble result = roundtripSerialization(original, serializer,
				AtomicDouble.class);

		assertThat(result).isNotNull().isNotSameAs(original);
		assertEquals(original.get(), result.get(), COMPARATION_PRECISION);
	}

	public Object getData() {
		return $(create(2.5), create(20000.5), create(.01d), create(100500.),
				new AtomicDouble());
	}

	private AtomicDouble create(double value) {
		return new AtomicDouble(value);
	}

}