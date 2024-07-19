package cz.tvrzna.jaxie;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;

/**
 * The Class Deserializator.
 *
 * @author michalt
 */
public class Deserializator
{

	/**
	 * Instantiates a new deserializator.
	 */
	private Deserializator()
	{
	}

	/**
	 * Parses the.
	 *
	 * @param content
	 *          the content
	 * @return the xml element
	 */
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

	/**
	 * Parses the.
	 *
	 * @param reader
	 *          the reader
	 * @return the xml element
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
	public static XmlElement parse(Reader reader) throws IOException
	{
		return parse(reader, null);
	}

	/**
	 * Parses the.
	 *
	 * @param reader
	 *          the reader
	 * @param parent
	 *          the parent
	 * @return the xml element
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Parses the attribute.
	 *
	 * @param reader
	 *          the reader
	 * @param element
	 *          the element
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Parses the comment.
	 *
	 * @param reader
	 *          the reader
	 * @return true, if successful
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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

	/**
	 * Parses the header.
	 *
	 * @param reader
	 *          the reader
	 * @return true, if successful
	 * @throws IOException
	 *           Signals that an I/O exception has occurred.
	 */
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
}
