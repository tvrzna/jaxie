package cz.tvrzna.jaxie;

import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import cz.tvrzna.jaxie.annotations.JaxieAttribute;
import cz.tvrzna.jaxie.annotations.JaxieElement;
import cz.tvrzna.jaxie.annotations.JaxieWrapper;

public class SerializationMapper
{
	protected static <T> XmlElement serialize(T o) throws Exception
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
		return processObject(o, name, null);
	}

	private static <T> XmlElement toXmlElement(T o, String name, XmlElement parent) throws Exception
	{
		if (o == null)
		{
			return null;
		}
		else if ((CommonUtils.SIMPLE_CLASSES.contains(o.getClass()) || Enum.class.isAssignableFrom(o.getClass())) && !o.getClass().isArray())
		{
			XmlElement el = new XmlElement(name, parent);
			el.setTextContent(serializeValue(o));
			return el;
		}
		else if (Map.class.isAssignableFrom(o.getClass()))
		{
			return processMap(o, name, parent);
		}
		else if (Collection.class.isAssignableFrom(o.getClass()))
		{
			processArray(((Collection<?>) o).toArray(), name, parent);
			return null;
		}
		else if (o.getClass().isArray())
		{
			if (CommonUtils.PRIMITIVE_CLASSES.contains(o.getClass().getComponentType()))
			{
				processArray(CommonUtils.convertPrimitiveArrayToObjects(o), name, parent);
				return null;
			}
			processArray((Object[]) o, name, parent);
			return null;
		}
		return processObject(o, name, parent);
	}

	private static <T> XmlElement processObject(T o, String name, XmlElement parent) throws Exception
	{
		XmlElement root = new XmlElement(name, parent);

		for (Field f : CommonUtils.getFields(o.getClass()))
		{
			f.setAccessible(true);
			Object value = f.get(o);
			if (value == null)
			{
				continue;
			}

			JaxieAttribute fAttr = f.getAnnotation(JaxieAttribute.class);
			if (fAttr != null)
			{
				root.addAttribute(fAttr.value().isEmpty() ? f.getName() : fAttr.value(), value != null ? serializeValue(value) : null);
				continue;
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

			XmlElement child = toXmlElement(value, fName, (wrapper != null ? wrapper : root));
			if (child != null)
			{
				(wrapper != null ? wrapper : root).add(child);
			}
		}

		return root;
	}

	private static XmlElement processMap(Object map, String name, XmlElement parent) throws Exception
	{
		XmlElement root = new XmlElement(name, parent);
		for (final Map.Entry<?, ?> entry : ((Map<?, ?>) map).entrySet())
		{
			XmlElement el = new XmlElement("entry", root);

			XmlElement key = toXmlElement(entry.getKey(), "key", el);
			el.add(key);

			XmlElement value = toXmlElement(entry.getValue(), "value", el);
			el.add(value);

			root.add(el);
		}
		return root;
	}

	private static void processArray(Object[] array, String name, XmlElement parent) throws Exception
	{
		for (Object obj : array)
		{
			XmlElement el = toXmlElement(obj, name, parent);
			if (el != null)
			{
				parent.add(el);
			}
		}
	}

	private static String serializeValue(Object value) throws Exception
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
			DateFormat df = new SimpleDateFormat(CommonUtils.DATE_FORMAT_XML);
			return df.format(value);
		}
		return value.toString();
	}
}
