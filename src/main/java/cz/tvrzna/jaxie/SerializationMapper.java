package cz.tvrzna.jaxie;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.Optional;

import cz.tvrzna.jaxie.annotations.JaxieAdapter;
import cz.tvrzna.jaxie.annotations.JaxieAttribute;
import cz.tvrzna.jaxie.annotations.JaxieElement;
import cz.tvrzna.jaxie.annotations.JaxieWrapper;

/**
 * The Class SerializationMapper.
 *
 * @author michalt
 */
public class SerializationMapper
{

	/**
	 * Serialize.
	 *
	 * @param <T>
	 *          the generic type
	 * @param o
	 *          the o
	 * @param config
	 *          the config
	 * @return the xml element
	 * @throws Exception
	 *           the exception
	 */
	protected static <T> XmlElement serialize(T o, Config config) throws Exception
	{
		if (o == null)
		{
			return null;
		}

		String name = "root";
		JaxieElement j = o.getClass().getAnnotation(JaxieElement.class);
		if (j != null && !j.value().isEmpty())
		{
			name = j.value();
		}
		return processObject(o, name, null, config);
	}

	/**
	 * To xml element.
	 *
	 * @param <T>
	 *          the generic type
	 * @param o
	 *          the o
	 * @param name
	 *          the name
	 * @param parent
	 *          the parent
	 * @param config
	 *          the config
	 * @return the xml element
	 * @throws Exception
	 *           the exception
	 */
	private static <T> XmlElement toXmlElement(T o, String name, XmlElement parent, Config config) throws Exception
	{
		if (o == null)
		{
			return null;
		}
		else if ((CommonUtils.SIMPLE_CLASSES.contains(o.getClass()) || Enum.class.isAssignableFrom(o.getClass())) && !o.getClass().isArray())
		{
			XmlElement el = new XmlElement(name, parent);
			el.setTextContent(serializeValue(o, config));
			return el;
		}
		else if (Map.class.isAssignableFrom(o.getClass()))
		{
			return processMap(o, name, parent, config);
		}
		else if (Collection.class.isAssignableFrom(o.getClass()))
		{
			processArray(((Collection<?>) o).toArray(), name, parent, config);
			return null;
		}
		else if (o.getClass().isArray())
		{
			if (CommonUtils.PRIMITIVE_CLASSES.contains(o.getClass().getComponentType()))
			{
				processArray(CommonUtils.convertPrimitiveArrayToObjects(o), name, parent, config);
				return null;
			}
			processArray((Object[]) o, name, parent, config);
			return null;
		}
		return processObject(o, name, parent, config);
	}

	/**
	 * Process object.
	 *
	 * @param <T>
	 *          the generic type
	 * @param o
	 *          the o
	 * @param name
	 *          the name
	 * @param parent
	 *          the parent
	 * @param config
	 *          the config
	 * @return the xml element
	 * @throws Exception
	 *           the exception
	 */
	private static <T> XmlElement processObject(T o, String name, XmlElement parent, Config config) throws Exception
	{
		XmlElement root = new XmlElement(name, parent);

		for (Field f : CommonUtils.getFields(o.getClass()))
		{
			processField(o, root, f, config);
		}

		return root;
	}

	@SuppressWarnings("unchecked")
	private static <T, A> void processField(T o, XmlElement root, Field f, Config config) throws Exception
	{
		f.setAccessible(true);
		Object value = f.get(o);
		if (value == null)
		{
			return;
		}

		JaxieAdapter adapter = f.getAnnotation(JaxieAdapter.class);
		Adapter<A> adapterHandler = null;
		if (adapter != null)
		{
			adapterHandler = (Adapter<A>) adapter.value().getDeclaredConstructor().newInstance();
		}

		JaxieAttribute fAttr = f.getAnnotation(JaxieAttribute.class);
		if (fAttr != null)
		{
			root.addAttribute(fAttr.value().isEmpty() ? f.getName() : fAttr.value(),
					value != null ? (adapterHandler != null ? adapterHandler.serialize((A) value) : serializeValue(value, config)) : null);
			return;
		}

		XmlElement wrapper = null;
		JaxieWrapper fWrapper = f.getAnnotation(JaxieWrapper.class);
		if (fWrapper != null)
		{
			wrapper = new XmlElement(fWrapper.value(), root);
			root.add(wrapper);
		}

		String fName = f.getName();
		JaxieElement fEl = f.getAnnotation(JaxieElement.class);
		if (fEl != null && !fEl.value().isEmpty())
		{
			fName = fEl.value();
		}

		XmlElement child = toXmlElement(adapterHandler != null ? adapterHandler.serialize((A) value) : value, fName, (wrapper != null ? wrapper : root), config);
		if (child != null)
		{
			(wrapper != null ? wrapper : root).add(child);
		}
	}

	/**
	 * Process map.
	 *
	 * @param map
	 *          the map
	 * @param name
	 *          the name
	 * @param parent
	 *          the parent
	 * @param config
	 *          the config
	 * @return the xml element
	 * @throws Exception
	 *           the exception
	 */
	private static XmlElement processMap(Object map, String name, XmlElement parent, Config config) throws Exception
	{
		XmlElement root = new XmlElement(name, parent);
		for (final Map.Entry<?, ?> entry : ((Map<?, ?>) map).entrySet())
		{
			XmlElement el = new XmlElement("entry", root);

			XmlElement key = toXmlElement(entry.getKey(), "key", el, config);
			el.add(key);

			XmlElement value = toXmlElement(entry.getValue(), "value", el, config);
			el.add(value);

			root.add(el);
		}
		return root;
	}

	/**
	 * Process array.
	 *
	 * @param array
	 *          the array
	 * @param name
	 *          the name
	 * @param parent
	 *          the parent
	 * @param config
	 *          the config
	 * @throws Exception
	 *           the exception
	 */
	private static void processArray(Object[] array, String name, XmlElement parent, Config config) throws Exception
	{
		for (Object obj : array)
		{
			XmlElement el = toXmlElement(obj, name, parent, config);
			if (el != null)
			{
				parent.add(el);
			}
		}
	}

	/**
	 * Serialize value.
	 *
	 * @param value
	 *          the value
	 * @param config
	 *          the config
	 * @return the string
	 * @throws Exception
	 *           the exception
	 */
	private static String serializeValue(Object value, Config config) throws Exception
	{
		if (value == null)
		{
			return "null";
		}
		else if (value instanceof Number)
		{
			return value.toString().replace(",", ".");
		}
		else if (value instanceof Date)
		{
			DateFormat df = Optional.ofNullable(config.getDateFormat()).orElse(new SimpleDateFormat(CommonUtils.DATE_FORMAT_XML));
			return df.format(value);
		}
		return value.toString();
	}
}
