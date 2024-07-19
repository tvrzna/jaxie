package cz.tvrzna.jaxie;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import cz.tvrzna.jaxie.annotations.JaxieAttribute;
import cz.tvrzna.jaxie.annotations.JaxieElement;
import cz.tvrzna.jaxie.annotations.JaxieWrapper;

/**
 * The Class DeserializationMapper.
 *
 * @author michalt
 */
public class DeserializationMapper
{

	/**
	 * Deserialize.
	 *
	 * @param <T>
	 *          the generic type
	 * @param lstElements
	 *          the lst elements
	 * @param clazz
	 *          the clazz
	 * @param field
	 *          the field
	 * @return the t
	 * @throws Exception
	 *           the exception
	 */
	@SuppressWarnings("unchecked")
	protected static <T> T deserialize(List<XmlElement> lstElements, Class<T> clazz, Field field) throws Exception
	{
		if (lstElements == null || lstElements.isEmpty())
		{
			return null;
		}
		XmlElement el = lstElements.get(lstElements.size() - 1);

		if ((CommonUtils.SIMPLE_CLASSES.contains(clazz) || Enum.class.isAssignableFrom(clazz)) && !clazz.isArray())
		{
			return (T) deserializeValue(el.getTextContent(), clazz);
		}
		else if (Collection.class.isAssignableFrom(clazz))
		{
			Class<?> lstSubClazz = null;
			if (field != null)
			{
				lstSubClazz = getClassFromField(field, 0);
			}
			if (lstSubClazz == null)
			{
				lstSubClazz = Object.class;
			}
			return (T) deserializeList(lstElements, lstSubClazz);
		}
		else if (clazz.isArray())
		{
			Class<?> arrSubClazz = clazz.getComponentType();
			List<?> list = deserializeList(lstElements, arrSubClazz);
			if (CommonUtils.PRIMITIVE_CLASSES.contains(arrSubClazz))
			{
				return CommonUtils.convertArrayToPrimitive(list, arrSubClazz);
			}
			return (T) list.toArray((T[]) Array.newInstance(arrSubClazz, 0));
		}
		else if (Map.class.isAssignableFrom(clazz))
		{
			Class<?> keyClazz = null;
			Class<?> valueClazz = null;
			if (field != null)
			{
				keyClazz = getClassFromField(field, 0);
				valueClazz = getClassFromField(field, 1);
			}
			if (keyClazz == null || valueClazz == null)
			{
				keyClazz = Object.class;
				valueClazz = Object.class;
			}
			return (T) deserializeMap(lstElements.get(lstElements.size() - 1), keyClazz, valueClazz);
		}
		return deserializeObject(el, clazz);
	}

	/**
	 * Deserialize.
	 *
	 * @param <T>
	 *          the generic type
	 * @param el
	 *          the el
	 * @param clazz
	 *          the clazz
	 * @return the t
	 * @throws Exception
	 *           the exception
	 */
	protected static <T> T deserialize(XmlElement el, Class<T> clazz) throws Exception
	{
		return deserialize(Arrays.asList(el), clazz, null);
	}

	/**
	 * Deserialize object.
	 *
	 * @param <T>
	 *          the generic type
	 * @param el
	 *          the el
	 * @param clazz
	 *          the clazz
	 * @return the t
	 * @throws Exception
	 *           the exception
	 */
	private static <T> T deserializeObject(XmlElement el, Class<T> clazz) throws Exception
	{
		T result = clazz.getDeclaredConstructor().newInstance();

		for (Field field : CommonUtils.getFields(clazz))
		{
			if (field.isAnnotationPresent(JaxieAttribute.class))
			{
				JaxieAttribute attr = field.getAnnotation(JaxieAttribute.class);
				String name = attr.value().isEmpty() ? field.getName() : attr.value();

				List<XmlAttribute> lstAttributes = el.getAttributes(name);
				if (!lstAttributes.isEmpty())
				{
					fillField(result, lstAttributes.get(lstAttributes.size() - 1).getValue(), field);
				}
				continue;
			}

			XmlElement currentElement = el;
			if (field.isAnnotationPresent(JaxieWrapper.class))
			{
				JaxieWrapper wrapper = field.getAnnotation(JaxieWrapper.class);
				String name = wrapper.value();

				List<XmlElement> lstElements = el.get(name);
				if (lstElements.isEmpty())
				{
					continue;
				}
				currentElement = lstElements.get(lstElements.size() - 1);
			}

			JaxieElement jElement = field.getAnnotation(JaxieElement.class);
			String name = jElement == null || jElement.value().isEmpty() ? field.getName() : jElement.value();
			List<XmlElement> lstElements = currentElement.get(name);
			if (lstElements.isEmpty())
			{
				continue;
			}
			fillField(result, deserialize(lstElements, field.getType(), field), field);
		}

		return result;
	}

	/**
	 * Gets the class from field.
	 *
	 * @param field
	 *          the field
	 * @param index
	 *          the index
	 * @return the class from field
	 */
	private static Class<?> getClassFromField(Field field, int index)
	{
		Object o = ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[index];
		if (o instanceof Class)
		{
			return (Class<?>) o;
		}
		else if (o instanceof Type)
		{
			return o.getClass();
		}
		return null;
	}

	/**
	 * Deserialize value.
	 *
	 * @param value
	 *          the value
	 * @param clazz
	 *          the clazz
	 * @return the object
	 * @throws ParseException
	 *           the parse exception
	 */
	@SuppressWarnings(
	{ "rawtypes", "unchecked" })
	private static Object deserializeValue(String value, Class<?> clazz) throws ParseException
	{
		if ("null".equals(value))
		{
			return null;
		}
		else if (Boolean.class.equals(clazz) || boolean.class.equals(clazz))
		{
			return Boolean.parseBoolean(value);
		}
		else if (Short.class.equals(clazz) || short.class.equals(clazz))
		{
			return Short.parseShort(value);
		}
		else if (Integer.class.equals(clazz) || int.class.equals(clazz))
		{
			return Integer.parseInt(value);
		}
		else if (Long.class.equals(clazz) || long.class.equals(clazz))
		{
			return Long.parseLong(value);
		}
		else if (Float.class.equals(clazz) || float.class.equals(clazz))
		{
			return Float.parseFloat(value);
		}
		else if (Double.class.equals(clazz) || double.class.equals(clazz))
		{
			return Double.parseDouble(value);
		}
		else if (BigInteger.class.equals(clazz))
		{
			return new BigInteger(value);
		}
		else if (BigDecimal.class.equals(clazz))
		{
			return new BigDecimal(value);
		}
		else if (Date.class.equals(clazz))
		{
			DateFormat df = new SimpleDateFormat(CommonUtils.DATE_FORMAT_XML);
			return df.parseObject(value);
		}
		else if (Enum.class.isAssignableFrom(clazz))
		{
			return Enum.valueOf((Class<? extends Enum>) clazz, value);
		}
		return value.toString();
	}

	/**
	 * Deserialize list.
	 *
	 * @param <T>
	 *          the generic type
	 * @param lstElements
	 *          the lst elements
	 * @param clazz
	 *          the clazz
	 * @return the list
	 * @throws Exception
	 *           the exception
	 */
	private static <T> List<T> deserializeList(List<XmlElement> lstElements, Class<T> clazz) throws Exception
	{
		List<T> result = new ArrayList<>();
		for (XmlElement el : lstElements)
		{
			result.add(deserialize(el, clazz));
		}
		return result;
	}

	/**
	 * Deserialize map.
	 *
	 * @param <K>
	 *          the key type
	 * @param <V>
	 *          the value type
	 * @param root
	 *          the root
	 * @param keyClazz
	 *          the key clazz
	 * @param valueClazz
	 *          the value clazz
	 * @return the map
	 * @throws Exception
	 *           the exception
	 */
	private static <K, V> Map<K, V> deserializeMap(XmlElement root, Class<K> keyClazz, Class<V> valueClazz) throws Exception
	{
		Map<K, V> result = new HashMap<>();
		List<XmlElement> lstElements = root.get("entry");
		for (XmlElement el : lstElements)
		{
			result.put(deserialize(el.getFirst("key"), keyClazz), deserialize(el.getFirst("value"), valueClazz));
		}
		return result;
	}

	/**
	 * Fill field.
	 *
	 * @param <T>
	 *          the generic type
	 * @param <A>
	 *          the generic type
	 * @param result
	 *          the result
	 * @param value
	 *          the value
	 * @param field
	 *          the field
	 * @throws Exception
	 *           the exception
	 */
	private static <T, A> void fillField(T result, Object value, Field field) throws Exception
	{
		if (value == null)
		{
			return;
		}
		field.setAccessible(true);
		field.set(result, value);
	}
}
