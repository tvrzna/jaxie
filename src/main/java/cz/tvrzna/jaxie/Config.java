package cz.tvrzna.jaxie;

import java.text.DateFormat;

/**
 * This class is carrier of all configuration applicable.
 *
 * @author michalt
 */
public class Config
{
	private DateFormat dateFormat;
	private boolean prettyPrint = false;
	private String prettyLineSymbol = null;
	private String prettyIndentSymbol = null;

	/**
	 * Gets the date format.
	 *
	 * @return the date format
	 */
	public DateFormat getDateFormat()
	{
		return dateFormat;
	}

	/**
	 * Sets the date format.
	 *
	 * @param dateFormat
	 *          the new date format
	 */
	public void setDateFormat(DateFormat dateFormat)
	{
		this.dateFormat = dateFormat;
	}

	/**
	 * Checks if is pretty print.
	 *
	 * @return true, if is pretty print
	 */
	public boolean isPrettyPrint()
	{
		return prettyPrint;
	}

	/**
	 * Sets the pretty print.
	 *
	 * @param prettyPrint
	 *          the new pretty print
	 */
	public void setPrettyPrint(boolean prettyPrint)
	{
		this.prettyPrint = prettyPrint;
	}

	/**
	 * Gets the pretty line symbol.
	 *
	 * @return the pretty line symbol
	 */
	public String getPrettyLineSymbol()
	{
		if (prettyLineSymbol == null)
		{
			return "\n";
		}
		return prettyLineSymbol;
	}

	/**
	 * Sets the pretty line symbol.
	 *
	 * @param prettyLineSymbol
	 *          the new pretty line symbol
	 */
	public void setPrettyLineSymbol(String prettyLineSymbol)
	{
		this.prettyLineSymbol = prettyLineSymbol;
	}

	/**
	 * Gets the pretty indent symbol.
	 *
	 * @return the pretty indent symbol
	 */
	public String getPrettyIndentSymbol()
	{
		if (prettyIndentSymbol == null)
		{
			return "\t";
		}
		return prettyIndentSymbol;
	}

	/**
	 * Sets the pretty indent symbol.
	 *
	 * @param prettyIndentSymbol
	 *          the new pretty indent symbol
	 */
	public void setPrettyIndentSymbol(String prettyIndentSymbol)
	{
		this.prettyIndentSymbol = prettyIndentSymbol;
	}

}
