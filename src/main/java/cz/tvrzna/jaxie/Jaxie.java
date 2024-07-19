package cz.tvrzna.jaxie;

import java.io.OutputStream;

/**
 * Main <code>jaxie</code> class, that provides converting of objects to XML as
 * <code>String</code> and XML <code>String</code> to objects.
 *
 * @author michalt
 */
public class Jaxie
{

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
			XmlElement el = SerializationMapper.serialize(object);
			return Serializator.serialize(el, 0);
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
			XmlElement el = SerializationMapper.serialize(object);
			Serializator.serialize(os, el, 0);
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
			return DeserializationMapper.deserialize(el, clazz);
		}
		catch (Exception e)
		{
			throw new RuntimeException(e);
		}
	}

}
