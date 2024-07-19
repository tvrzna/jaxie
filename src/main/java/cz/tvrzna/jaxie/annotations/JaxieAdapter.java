package cz.tvrzna.jaxie.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

import cz.tvrzna.jaxie.Adapter;

/**
 * This annotation defines adapter property serialization and deserialization.
 *
 * @author michalt
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface JaxieAdapter
{

	/**
	 * Value.
	 *
	 * @return the class&lt;? extends adapter&lt;?&gt;&gt;
	 */
	Class<? extends Adapter<?>> value();
}
