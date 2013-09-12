package net.alxb.springframework.remoting.kryo.serializers;

import static com.google.common.collect.ImmutableMap.of;
import static junitparams.JUnitParamsRunner.$;
import static org.fest.assertions.Assertions.assertThat;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.esotericsoftware.kryo.Serializer;
import com.google.common.collect.ImmutableMap;

@RunWith(JUnitParamsRunner.class)
public class ImmutableMapSerializerTest extends
		RoundtripSerializerTest<ImmutableMap<Object, Object>> {

	Serializer<ImmutableMap<Object, Object>> serializer = new ImmutableMapSerializer<Object, Object>();

	@Parameters(method = "getMaps")
	@Test
	public void roundtripSerialization(ImmutableMap<Object, Object> original) {

		@SuppressWarnings("unchecked")
		Class<ImmutableMap<Object, Object>> clazz = (Class<ImmutableMap<Object, Object>>) original
				.getClass();

		ImmutableMap<Object, Object> result = super.roundtripSerialization(
				original, serializer, clazz);

		assertThat(result).isNotNull().isNotSameAs(original)
				.isEqualTo(original);
	}

	public Object getMaps() {
		ImmutableMap<Long, Integer> map = ImmutableMap
				.<Long, Integer> builder().put(1L, -1).put(2000L, 3000)
				.put(5000L, 5000).put(123457890L, -987654321).build();

		return $(of("One", "Two"), 
				of("Key", 2, "Key_2", 3, "Key_3", 100500),
				of(1000L, "Value"), 
				map);
	}

}
