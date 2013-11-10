package com.example.sonysignin;

public class SignIn
{
	private String m_name;
	private String m_company;
	private String m_seeking;
	private String m_timein;
	private String m_timeout;
	private String m_current;
	
	// Constructor
	public SignIn(String name, String company, String seeking, String timein, String timeout, String current)
	{
		m_name = name;
		m_company = company;
		m_seeking = seeking;
		m_timein = timein;
		m_timeout = timeout;
		m_current = current;
	}

	// Getters and setters
	public String getName()
	{
		return m_name;
	}

	public void setName(String name)
	{
		m_name = name;
	}
	
	public String getCompany()
	{
		return m_company;
	}

	public void setCompany(String company)
	{
		m_company = company;
	}
	
	public String getSeeking()
	{
		return m_seeking;
	}

	public void setSeeking(String seeking)
	{
		m_seeking = seeking;
	}
	
	public String getTimeIn()
	{
		return m_timein;
	}

	public void setTimeIn(String timein)
	{
		m_timein = timein;
	}
	
	public String getTimeOut()
	{
		return m_timeout;
	}

	public void setTimeOut(String timeout)
	{
		m_timeout = timeout;
	}
	
	public String getCurrent()
	{
		return m_current;
	}

	public void setCurrent(String current)
	{
		m_current = current;
	}

	// Will be used by the ArrayAdapter in the ListView
	@Override
	public String toString()
	{
		return m_name;
	}
}
