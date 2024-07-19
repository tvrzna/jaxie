package cz.tvrzna.jaxie;

/**
 * The Interface Adapter.
 *
 * @author michalt
 * @param <T>
 *          the generic type
 */
public interface Adapter<T>
{

	/**
	 * Deserialize <code>String</code> value into object.
	 *
	 * @param text
	 *          the text
	 * @return the t
	 */
	public T deserialize(String text);

	/**
	 * Serialize object into <code>String</code>.
	 *
	 * @param value
	 *          the value
	 * @return the string
	 */
	public String serialize(T value);
}
