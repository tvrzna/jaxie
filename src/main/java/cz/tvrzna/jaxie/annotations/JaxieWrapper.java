package cz.tvrzna.jaxie.annotations;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Documented;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;

/**
 * The Interface JaxieWrapper.
 *
 * @author michalt
 */
@Documented
@Retention(RUNTIME)
@Target(FIELD)
public @interface JaxieWrapper
{

	/**
	 * Value.
	 *
	 * @return the string
	 */
	String value();
}
