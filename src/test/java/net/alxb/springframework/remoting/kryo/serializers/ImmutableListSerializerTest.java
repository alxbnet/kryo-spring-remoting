package net.alxb.springframework.remoting.kryo.serializers;

import static com.google.common.collect.ImmutableList.of;
import static com.google.common.collect.Lists.newArrayList;
import static junitparams.JUnitParamsRunner.$;
import static org.fest.assertions.Assertions.assertThat;

import java.util.Date;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.esotericsoftware.kryo.Serializer;
import com.google.common.collect.ImmutableList;

@RunWith(JUnitParamsRunner.class)
public class ImmutableListSerializerTest extends
		RoundtripSerializerTest<ImmutableList<Object>> {

	Serializer<ImmutableList<Object>> serializer = new ImmutableListSerializer<Object>();

	@Parameters(method = "getLists")
	@Test
	public void roundtripSerialization(ImmutableList<Object> original) {

		@SuppressWarnings("unchecked")
		Class<ImmutableList<Object>> clazz = (Class<ImmutableList<Object>>) original
				.getClass();

		ImmutableList<Object> result = super.roundtripSerialization(original,
				serializer, clazz);

		assertThat(result).isNotNull().isNotSameAs(original)
				.isEqualTo(original);
	}

	public Object getLists() {

		return $(of("One", "Two"), 
				of("Key", 2, "Key_2", 3, "Key_3", 100500),
				of(1, 20, 300, 4000, 50000, 600000, 7000000, 80000000, 900000000),
				of(newArrayList(new Date())));
	}

}
