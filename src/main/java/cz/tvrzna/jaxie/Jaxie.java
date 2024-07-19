package cz.tvrzna.jaxie;

import java.io.OutputStream;
import java.text.DateFormat;

/**
 * Main <code>jaxie</code> class, that provides converting of objects to XML as
 * <code>String</code> and XML <code>String</code> to objects.
 *
 * @author michalt
 */
public class Jaxie
{
	private Config config = new Config();

	/**
	 * To xml.
	 *
	 * @param <T>
	 *          the generic type
	 * @param object
	 *          the object
	 * @return the string
	 */
	public <T> String toXml(T object)
	{
		try
		{
			XmlElement el = SerializationMapper.serialize(object, config);
			return Serializator.serialize(el, 0, config);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * To xml.
	 *
	 * @param <T>
	 *          the generic type
	 * @param object
	 *          the object
	 * @param os
	 *          the os
	 */
	public <T> void toXml(T object, OutputStream os)
	{
		try
		{
			XmlElement el = SerializationMapper.serialize(object, config);
			Serializator.serialize(os, el, 0, config);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * From xml.
	 *
	 * @param <T>
	 *          the generic type
	 * @param xml
	 *          the xml
	 * @param clazz
	 *          the clazz
	 * @return the t
	 */
	public <T> T fromXml(String xml, Class<T> clazz)
	{
		try
		{
			XmlElement el = Deserializator.parse(xml);
			return DeserializationMapper.deserialize(el, clazz, config);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

	/**
	 * Uses custom date format for each date operation. If default date format
	 * should be used, just set <code>null</code>.
	 *
	 * @param customDateFormat
	 *          the custom date format
	 * @return the jaxie
	 */
	public Jaxie withCustomDateFormat(DateFormat customDateFormat)
	{
		config.setDateFormat(customDateFormat);
		return this;
	}

	/**
	 * Printing to XML with pretty print.
	 *
	 * @return the jaxie
	 */
	public Jaxie withPrettyPrint()
	{
		return withPrettyPrint(true);
	}

	/**
	 * Printing to XML with or without pretty print.
	 *
	 * @param prettyPrint
	 *          the pretty print
	 * @return the jaxie
	 */
	public Jaxie withPrettyPrint(boolean prettyPrint)
	{
		config.setPrettyPrint(prettyPrint);
		return this;
	}

	/**
	 * Sets custom symbol for new line. Works only with pretty print. If is set to
	 * <code>null</code>, it uses default <code>\n</code>.
	 *
	 * @param symbol
	 *          the symbol
	 * @return the jaxie
	 */
	public Jaxie withLineIndent(String symbol)
	{
		config.setPrettyLineSymbol(symbol);
		return this;
	}

	/**
	 * Sets custom symbol for tab indentation. Works only with pretty print. If is
	 * set to <code>null</code>, it uses default <code>\t</code>.
	 *
	 * @param symbol
	 *          the symbol
	 * @return the jaxie
	 */
	public Jaxie withTabIndent(String symbol)
	{
		config.setPrettyIndentSymbol(symbol);
		return this;
	}

	/**
	 * Gets the config.
	 *
	 * @return the config
	 */
	protected Config getConfig()
	{
		return config;
	}

}
