package net.alxb.springframework.remoting.kryo;

import com.esotericsoftware.kryo.Kryo;

/**
 * Creates {@link Kryo} class instances
 * 
 * @author Alex Borisov
 *
 */
public interface KryoFactory {
	Kryo create();
}