package cz.tvrzna.jaxie;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * Main <code>jaxie</code> class, that provides converting of objects to XML as
 * <code>String</code> and XML <code>String</code> to objects.
 *
 * @author michalt
 */
public class Jaxie
{
	private static final Map<String, String> MAP_XML_ESCAPE = new HashMap<>();

	static
	{
		MAP_XML_ESCAPE.put("<", "&lt;");
		MAP_XML_ESCAPE.put(">", "&gt;");
		MAP_XML_ESCAPE.put("'", "&apos;");
		MAP_XML_ESCAPE.put("\"", "&quot;");
	}

	private Jaxie()
	{
	}

	public static XmlElement parse(String content)
	{
		try
		{
			return parse(new StringReader(content));
		}
		catch (Exception e)
		{
			return null;
		}
	}

	public static XmlElement parse(Reader reader) throws IOException
	{
		return parse(reader, null);
	}

	private static XmlElement parse(Reader reader, XmlElement parent) throws IOException
	{
		if (parent == null)
		{
			parseHeader(reader);
		}
		// TODO: define document (extension of element)

		StringWriter sw = new StringWriter();
		boolean element = false;
		boolean endElement = false;
		XmlElement result = null;

		int c;
		while ((c = reader.read()) >= 0)
		{
			// element
			if (c == '<')
			{
				if (!parseComment(reader))
				{
					if (!element && result != null)
					{
						result.value = sw.toString();
					}
					if (!element && result == null && parent != null && sw.getBuffer().length() > 0)
					{
						parent.value = sw.toString();
					}
					sw.getBuffer().setLength(0);
					element = true;
				}
			}
			else if (c == '>' && element)
			{
				element = false;
				if (result == null && (parent == null || (parent != null && !sw.toString().equals(parent.name))))
				{
					result = new XmlElement(sw.toString().trim(), parent);
					if (parent != null)
					{
						parent.lstChildren.add(result);
					}
					sw.getBuffer().setLength(0);
				}
				if (endElement)
				{
					return result;
				}
				else
				{
					parse(reader, result);
					if (parent != null)
					{
						result = null;
					}
				}
			}
			else if (c == '/' && element)
			{
				endElement = true;
			}
			// Attributes
			else if (c == ' ' && element)
			{
				if (result == null)
				{
					result = new XmlElement(sw.toString().trim(), parent);
					if (parent != null)
					{
						parent.lstChildren.add(result);
					}
				}
				sw.getBuffer().setLength(0);
				parseAttribute(reader, result);
			}
			// The rest
			else
			{
				sw.write(c);
			}
		}
		return result;
	}

	private static void parseAttribute(Reader reader, XmlElement element) throws IOException
	{
		StringWriter sw = new StringWriter();
		boolean readValue = false;
		String name = null;

		int c;
		while ((c = reader.read()) >= 0)
		{
			if (c == '=')
			{
				name = sw.toString().trim();
				sw.getBuffer().setLength(0);
			}
			else if (c == '"')
			{
				if (name == null)
				{
					return;
				}
				if (readValue)
				{
					element.addAttribute(name, sw.toString());
					sw.getBuffer().setLength(0);
					name = null;
					readValue = false;
				}
				else
				{
					readValue = true;
				}
			}
			else if ((c == ' ' || c == '>') && !readValue)
			{
				name = sw.toString().trim();
				if (name != null && !name.isEmpty())
				{
					element.addAttribute(name, null);
				}

				if (c == ' ')
				{
					parseAttribute(reader, element);
				}
				else
				{
					reader.skip(-1l);
				}

				return;
			}
			else
			{
				sw.write(c);
			}
		}
	}

	private static boolean parseComment(Reader reader) throws IOException
	{
		int c = reader.read();
		if (c == '!')
		{
			c = reader.read();
			if (c == '-')
			{
				c = reader.read();
				if (c != '-')
				{
					reader.skip(-3l);
					return false;
				}
			}
			else
			{
				reader.skip(-2l);
				return false;
			}
		}
		else
		{
			reader.skip(-1l);
			return false;
		}
		int counter = 0;
		while ((c = reader.read()) >= 0)
		{
			if (c == '-')
			{
				counter++;
			}
			else if (c == '>' && counter > 1)
			{
				return true;
			}
			else
			{
				counter = 0;
			}
		}
		return true;
	}

	private static boolean parseHeader(Reader reader) throws IOException
	{
		StringWriter sw = new StringWriter();
		boolean possibleEnd = false;

		int c = reader.read();
		if (c == '<')
		{
			c = reader.read();
			if (c != '?')
			{
				reader.skip(-2l);
				return false;
			}
		}
		else
		{
			reader.skip(-1l);
			return false;
		}
		while ((c = reader.read()) >= 0)
		{
			if (c == '?')
			{
				possibleEnd = true;
			}
			else if (c == '>' && possibleEnd)
			{
				// TODO: Something with read header
				return true;
			}
			else
			{
				possibleEnd = false;
				sw.write(c);
			}
		}
		return true;
	}

	/**
	 * Creates new root <code>XmlElement</code>.
	 *
	 * @param name
	 *          the name
	 * @return newly created root element
	 */
	public static XmlElement create(String name)
	{
		return new XmlElement(name, null);
	}

	/**
	 * Normalize text.
	 *
	 * @param str
	 *          the str
	 * @return the string
	 */
	protected static String normalizeText(String str)
	{
		String result = str;
		result = result.replace("&", "&amp;");
		for (Map.Entry<String, String> entry : MAP_XML_ESCAPE.entrySet())
		{
			result = result.replace(entry.getKey(), entry.getValue());
		}
		return result;
	}

}
