package cz.tvrzna.jaxie;

import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.StringWriter;
import java.io.Writer;

public class Serializator
{
	private static final String NEW_LINE = "\r\n";
	private static final String INDENT_SYMBOL = "\t";
	private static final String XML_INFO = "<?xml version=\"1.0\" encoding=\"utf-8\"?>";

	private Serializator()
	{
	}

	/**
	 * Serialize <code>object</code> to <code>String</code>.
	 *
	 * @param w
	 *          the w
	 * @param object
	 *          the object
	 * @param config
	 *          the config
	 * @param indent
	 *          the indent
	 * @return the string
	 * @throws Exception
	 *           the exception
	 */
	protected static String serialize(XmlElement element, int indent) throws Exception
	{
		StringWriter sw = new StringWriter();
		serialize(sw, element, true, indent);
		return sw.toString();
	}

	/**
	 * Serialize <code>object</code> to <code>OutputStream</code>.
	 *
	 * @param w
	 *          the w
	 * @param object
	 *          the object
	 * @param config
	 *          the config
	 * @param indent
	 *          the indent
	 * @return the string
	 * @throws Exception
	 *           the exception
	 */
	protected static void serialize(OutputStream os, XmlElement element, int indent) throws Exception
	{
		OutputStreamWriter sw = new OutputStreamWriter(os);
		serialize(sw, element, true, indent);
		sw.close();
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
	private static void serialize(Writer w, XmlElement object, boolean pretty, int indentCount) throws Exception
	{
		String indent = "";
		String childIndent = "";

		if (object.isDisplayXmlInfo() && object.parent == null)
		{
			w.append(XML_INFO);
			if (pretty)
			{
				w.append(NEW_LINE);
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
				serialize(w, el, true, indentCount + 1);
			}
			if (object.value != null && !object.value.trim().isEmpty())
			{
				if (pretty && indentCount == 0)
				{
					w.append(NEW_LINE);
				}
				w.append(childIndent).append(CommonUtils.isCDATA(object.value) ? object.value : CommonUtils.normalizeText(object.value));
			}
			if (pretty && indentCount == 0)
			{
				w.append(NEW_LINE);
			}
			w.append(indent).append("</").append(object.name).append(">");
		}
	}
}
