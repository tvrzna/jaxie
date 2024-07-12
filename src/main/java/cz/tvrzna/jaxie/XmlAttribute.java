package cz.tvrzna.jaxie;

/**
 * The Class XmlAttribute.
 *
 * @author michalt
 */
public class XmlAttribute
{
	private final String name;
	private final String value;

	/**
	 * Instantiates a new xml attribute.
	 *
	 * @param name
	 *          the name
	 * @param value
	 *          the value
	 */
	protected XmlAttribute(String name, String value)
	{
		super();
		this.name = name;
		this.value = value;
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
	 * Gets the value.
	 *
	 * @return the value
	 */
	public String getValue()
	{
		return value;
	}

}
