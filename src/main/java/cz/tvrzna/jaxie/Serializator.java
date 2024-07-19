package cz.tvrzna.jaxie;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

/**
 * The Class Serializator.
 *
 * @author michalt
 */
public class Serializator
{
	private static final String XML_INFO = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	/**
	 * Instantiates a new serializator.
	 */
	private Serializator()
	{
	}

	/**
	 * Serialize <code>object</code> to <code>String</code>.
	 *
	 * @param element
	 *          the element
	 * @param indent
	 *          the indent
	 * @param config
	 *          the config
	 * @return the string
	 * @throws Exception
	 *           the exception
	 */
	protected static String serialize(XmlElement element, int indent, Config config) throws Exception
	{
		StringWriter sw = new StringWriter();
		serialize(sw, element, indent, config);
		return sw.toString();
	}

	/**
	 * Serialize <code>object</code> to <code>OutputStream</code>.
	 *
	 * @param os
	 *          the os
	 * @param element
	 *          the element
	 * @param indent
	 *          the indent
	 * @param config
	 *          the config
	 * @return the string
	 * @throws Exception
	 *           the exception
	 */
	protected static void serialize(OutputStream os, XmlElement element, int indent, Config config) throws Exception
	{
		OutputStreamWriter sw = new OutputStreamWriter(os);
		serialize(sw, element, indent, config);
		sw.close();
	}

	/**
	 * Converts <code>XmlElement</code> into <code>String</code>.
	 *
	 * @param w
	 *          the w
	 * @param object
	 *          the object
	 * @param pretty
	 *          the pretty
	 * @param indentCount
	 *          the indent count
	 * @param config
	 *          the config
	 * @return current element as String
	 * @throws Exception
	 *           the exception
	 */
	private static void serialize(Writer w, XmlElement object, int indentCount, Config config) throws Exception
	{
		String indent = "";
		String childIndent = "";

		if (object.isDisplayXmlInfo() && object.parent == null)
		{
			w.append(XML_INFO);
			if (config.isPrettyPrint())
			{
				w.append(config.getPrettyLineSymbol());
			}
		}

		if (config.isPrettyPrint())
		{
			StringBuilder strIndent = new StringBuilder();
			if (indentCount > 0)
			{
				strIndent.append(config.getPrettyLineSymbol());
			}
			for (int i = 0; i < indentCount; i++)
			{
				strIndent.append(config.getPrettyIndentSymbol());
			}
			indent = strIndent.toString();
			childIndent = indent.concat(config.getPrettyIndentSymbol());
		}

		w.append(indent).append("<").append(object.name);
		for (XmlAttribute attr : object.lstAttributes)
		{
			w.append(" ").append(attr.getName()).append(attr.getValue() != null ? "=\"" : "").append(attr.getValue() != null ? CommonUtils.normalizeText(attr.getValue()) : "")
					.append(attr.getValue() != null ? "\"" : "");
		}
		if (object.lstChildren.isEmpty() && (object.value == null || object.value.trim().isEmpty()))
		{
			w.append("/>");
		}
		else if (object.lstChildren.isEmpty() && object.value != null && !object.value.trim().isEmpty())
		{
			w.append(">").append(CommonUtils.isCDATA(object.value) ? object.value : CommonUtils.normalizeText(object.value)).append("</").append(object.name).append(">");
		}
		else
		{
			w.append(">");
			for (XmlElement el : object.lstChildren)
			{
				serialize(w, el, indentCount + 1, config);
			}
			if (object.value != null && !object.value.trim().isEmpty())
			{
				if (config.isPrettyPrint() && indentCount == 0)
				{
					w.append(config.getPrettyLineSymbol());
				}
				w.append(childIndent).append(CommonUtils.isCDATA(object.value) ? object.value : CommonUtils.normalizeText(object.value));
			}
			if (config.isPrettyPrint() && indentCount == 0)
			{
				w.append(config.getPrettyLineSymbol());
			}
			w.append(indent).append("</").append(object.name).append(">");
		}
	}
}
