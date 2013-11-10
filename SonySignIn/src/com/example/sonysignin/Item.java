package com.example.sonysignin;

public class Item
{
	private String m_price;
	private String m_name;
	
	// Constructor
	public Item(String price, String name)
	{
		m_price = price;
		m_name = name;
	}

	// Getters and setters
	public String getPrice()
	{
		return m_price;
	}

	public void setPrice(String price)
	{
		m_price = price;
	}

	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString()
	{
		return m_name;
	}
}
