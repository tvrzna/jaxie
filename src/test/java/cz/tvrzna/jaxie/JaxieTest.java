package cz.tvrzna.jaxie;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import cz.tvrzna.jaxie.annotations.JaxieAdapter;
import cz.tvrzna.jaxie.annotations.JaxieAttribute;
import cz.tvrzna.jaxie.annotations.JaxieElement;
import cz.tvrzna.jaxie.annotations.JaxieWrapper;

public class JaxieTest
{
	private enum Rating
	{
		FIRST, SECOND;
	}

	@JaxieElement("rootElement")
	public static class TestClass implements Serializable
	{
		private static final long serialVersionUID = 1L;

		@JaxieElement
		private Long id;

		private Date date;

		@JaxieWrapper("children")
		@JaxieElement("child")
		private List<TestClass> children;

		@JaxieAttribute
		private String attr;

		private String anonymousElement;

		private Integer counter;

		private Rating rating;

		@JaxieElement("sophisticatedMap")
		private Map<Long, TestClass> map;

		@JaxieWrapper("values")
		private int[] value;

		@JaxieAdapter(ByteAdapter.class)
		private byte[] arr;
	}

	public static class ByteAdapter implements Adapter<byte[]>
	{
		@Override
		public byte[] deserialize(String text)
		{
			return new byte[]
			{ 100, 50, 100 };
		}

		@Override
		public String serialize(byte[] value)
		{
			return "AAAAAA";
		}
	}

	@Test
	public void basicTest()
	{
		Jaxie jaxie = new Jaxie();

		final String xml = "<rootElement><id>1</id><date>2024-07-18 13:15:23</date><children><child attr=\"boo\"><id>2</id><counter>22</counter><rating>SECOND</rating></child><child attr=\"boo2\"><id>3</id><counter>33</counter><rating>SECOND</rating></child></children><rating>FIRST</rating><sophisticatedMap><entry><key>2</key><value attr=\"boo\"><id>2</id><counter>22</counter><rating>SECOND</rating></value></entry><entry><key>3</key><value attr=\"boo2\"><id>3</id><counter>33</counter><rating>SECOND</rating></value></entry></sophisticatedMap><values><value>1</value><value>2</value><value>3</value><value>4</value><value>5</value><value>6</value></values><arr>AAAAA</arr></rootElement>";
		XmlElement el = Deserializator.parse(xml);

		TestClass c = new TestClass();
		c.id = 1l;
		c.children = new ArrayList<>();
		c.date = new Date();
		c.map = new HashMap<>();
		c.rating = Rating.FIRST;
		c.value = new int[]
		{ 1, 2, 3, 4, 5, 6 };
		c.arr = new byte[]
		{ 100, 50, 100 };

		TestClass c1 = new TestClass();
		c1.id = 2l;
		c1.attr = "boo";
		c1.counter = 22;
		c1.rating = Rating.SECOND;
		c.children.add(c1);
		c.map.put(c1.id, c1);

		TestClass c2 = new TestClass();
		c2.id = 3l;
		c2.attr = "boo2";
		c2.counter = 33;
		c2.rating = Rating.SECOND;
		c.children.add(c2);
		c.map.put(c2.id, c2);

		try
		{
			String result = jaxie.toXml(c);

			TestClass dc = DeserializationMapper.deserialize(el, TestClass.class, jaxie.getConfig());
			System.out.print(result);
		}
		catch (Exception e)
		{
			Assertions.fail(e);
		}
	}
}
