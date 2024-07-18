package cz.tvrzna.jaxie;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * The Class XmlElement.
 *
 * @author michalt
 */
public class XmlElement
{
	private static final String NEW_LINE = "\r\n";
	private static final String INDENT_SYMBOL = "\t";
	private static final String XML_INFO = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	protected final String name;
	protected final XmlElement parent;
	protected final List<XmlElement> lstChildren;
	protected final List<XmlAttribute> lstAttributes;
	protected String value;

	private boolean displayXmlInfo = false;

	/**
	 * Instantiates a new xml element.
	 *
	 * @param name
	 *          the name
	 * @param parent
	 *          the parent
	 */
	protected XmlElement(String name, XmlElement parent)
	{
		this.parent = parent;
		this.name = name;
		lstChildren = new ArrayList<>();
		lstAttributes = new ArrayList<>();
	}

	/**
	 * Converts <code>XmlElement</code> into <code>String</code>.
	 *
	 * @return current element as String
	 */
	@Override
	public String toString()
	{
		return toString(true);
	}

	/**
	 * Converts <code>XmlElement</code> into <code>String</code>.
	 *
	 * @param pretty
	 *          the pretty
	 * @return current element as String
	 */
	public String toString(boolean pretty)
	{
		return toString(pretty, 0);
	}

	/**
	 * Converts <code>XmlElement</code> into <code>String</code>.
	 *
	 * @param pretty
	 *          the pretty
	 * @param indentCount
	 *          the indent count
	 * @return current element as String
	 */
	private String toString(boolean pretty, int indentCount)
	{
		StringBuilder result = new StringBuilder();
		String indent = "";
		String childIndent = "";

		if (displayXmlInfo && parent == null)
		{
			result.append(XML_INFO);
			if (pretty)
			{
				result.append(NEW_LINE);
			}
		}

		if (pretty)
		{
			StringBuilder strIndent = new StringBuilder();
			if (indentCount > 0)
			{
				strIndent.append(NEW_LINE);
			}
			for (int i = 0; i < indentCount; i++)
			{
				strIndent.append(INDENT_SYMBOL);
			}
			indent = strIndent.toString();
			childIndent = indent.concat(INDENT_SYMBOL);
		}

		result.append(indent).append("<").append(this.name);
		lstAttributes.forEach(attr -> result.append(" ").append(attr.getName()).append(attr.getValue() != null ? "=\"" : "")
				.append(attr.getValue() != null ? Jaxie.normalizeText(attr.getValue()) : "").append(attr.getValue() != null ? "\"" : ""));
		if (lstChildren.isEmpty() && (value == null || value.trim().isEmpty()))
		{
			result.append("/>");
		}
		else if (lstChildren.isEmpty() && value != null && !value.trim().isEmpty())
		{
			result.append(">").append(value).append("</").append(this.name).append(">");
		}
		else
		{
			result.append(">");
			lstChildren.forEach(el -> result.append(el.toString(pretty, indentCount + 1)));
			if (value != null && !value.trim().isEmpty())
			{
				if (pretty && indentCount == 0)
				{
					result.append(NEW_LINE);
				}
				result.append(childIndent).append(value);
			}
			if (pretty && indentCount == 0)
			{
				result.append(NEW_LINE);
			}
			result.append(indent).append("</").append(this.name).append(">");
		}
		return result.toString();
	}

	/**
	 * Adds new child element in tags, that matches <code>name</code>.
	 *
	 * @param name
	 *          the name
	 * @return newly created child element
	 */
	public XmlElement add(String name)
	{
		add(name, null, null);
		return this;
	}

	/**
	 * Adds new child element in tags, that matches <code>name</code> with
	 * predefined content value.
	 *
	 * @param name
	 *          the name
	 * @param value
	 *          the value
	 * @return newly created child element
	 */
	public XmlElement add(String name, String value)
	{
		add(name, value, null);
		return this;
	}

	/**
	 * Adds new child element in tags, that matches <code>name</code>lambda
	 * function, that handles newly created child element.
	 *
	 * @param name
	 *          the name
	 * @param childElementFunction
	 *          the child element function
	 * @return newly created child element
	 */
	public XmlElement add(String name, Consumer<XmlElement> childElementFunction)
	{
		add(name, null, childElementFunction);
		return this;
	}

	/**
	 * Adds new child element in tags, that matches <code>name</code> with
	 * predefined content value and lambda function, that handles newly created
	 * child element.
	 *
	 * @param name
	 *          the name
	 * @param value
	 *          the value
	 * @param childElementFunction
	 *          the child element function
	 * @return current element
	 */
	public XmlElement add(String name, String value, Consumer<XmlElement> childElementFunction)
	{
		XmlElement childElement = new XmlElement(name, this);
		if (value != null)
		{
			childElement.setTextContent(value);
		}
		if (childElementFunction != null)
		{
			childElementFunction.accept(childElement);
		}
		lstChildren.add(childElement);
		return this;
	}

	/**
	 * Adds the child element, if parent of child is the same as current element..
	 *
	 * @param childElement
	 *          the child element
	 * @return the xml element
	 */
	public XmlElement add(XmlElement childElement)
	{
		if (childElement.parent.equals(this))
		{
			lstChildren.add(childElement);
		}
		return this;
	}

	/**
	 * Gets the name.
	 *
	 * @return the name
	 */
	public String getName()
	{
		return name;
	}

	/**
	 * Gets the list of all elements with defined name.
	 *
	 * @param name
	 *          the name
	 * @return the list
	 */
	public List<XmlElement> get(String name)
	{
		if (name != null)
		{
			return lstChildren.stream().filter(e -> name.equals(e.name)).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	/**
	 * Gets the first of all elements with defined name.
	 *
	 * @param name
	 *          the name
	 * @return the list
	 */
	public XmlElement getFirst(String name)
	{
		if (name != null)
		{
			return lstChildren.stream().filter(e -> name.equals(e.name)).findFirst().orElse(null);
		}
		return null;
	}

	/**
	 * Gets size of list of child elements.
	 *
	 * @return the int
	 */
	public int size()
	{
		return lstChildren.size();
	}

	/**
	 * Adds attribute to current element.
	 *
	 * @param name
	 *          the name
	 * @param value
	 *          the value
	 * @return current element object
	 */
	public XmlElement addAttribute(String name, String value)
	{
		lstAttributes.add(new XmlAttribute(name, value));
		return this;
	}

	/**
	 * Gets the list of all attributes with defined name.
	 *
	 * @param name
	 *          the name
	 * @return the list
	 */
	public List<XmlAttribute> getAttributes(String name)
	{
		if (name != null)
		{
			return lstAttributes.stream().filter(a -> name.equals(a.getName())).collect(Collectors.toList());
		}
		return new ArrayList<>();
	}

	/**
	 * Gets the text content.
	 *
	 * @return the text content
	 */
	public String getTextContent()
	{
		if (value != null && value.startsWith("<![CDATA[") && value.endsWith("]]>"))
		{
			return value.substring("<![CDATA[".length(), value.lastIndexOf("]]>"));
		}

		return value;
	}

	/**
	 * Sets content value of current element.
	 *
	 * @param value
	 *          the value
	 * @return current element object
	 */
	public XmlElement setTextContent(String value)
	{
		return setTextContent(value, false);
	}

	/**
	 * Sets content value of current element in CDATA tag.
	 *
	 * @param value
	 *          the value
	 * @return current element object
	 */
	public XmlElement setTextContentAsCDATA(String value)
	{
		return setTextContent(value, true);
	}

	/**
	 * Sets content value of current element as CDATA.
	 *
	 * @param value
	 *          the value
	 * @param cdata
	 *          the cdata
	 * @return current element object
	 */
	private XmlElement setTextContent(String value, boolean cdata)
	{
		if (value != null)
		{
			if (cdata)
			{
				this.value = "<![CDATA[".concat(value).concat("]]>");
			}
			else
			{
				this.value = Jaxie.normalizeText(value);
			}
		}
		else
		{
			this.value = null;
		}
		return this;
	}

	/**
	 * Checks if is display xml info.
	 *
	 * @return true, if is display xml info
	 */
	public boolean isDisplayXmlInfo()
	{
		return displayXmlInfo;
	}

	/**
	 * Sets the display xml info.
	 *
	 * @param displayXmlInfo
	 *          the new display xml info
	 * @return the xml element
	 */
	public XmlElement setDisplayXmlInfo(boolean displayXmlInfo)
	{
		this.displayXmlInfo = displayXmlInfo;
		return this;
	}
}