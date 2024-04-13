package com.armorhud.setting;

import com.armorhud.feature.Feature;

public class TextSetting extends Setting<String>
{

	private String value;

	public TextSetting(String name, String value, Feature feature)
	{
		super(name,  feature);
		this.value = value;
	}

	public void setValue(String value)
	{
		this.value = value;
	}

	@Override
	public String getValue()
	{
		return value;
	}

	@Override
	public void loadFromStringInternal(String string)
	{
		value = string;
	}

	@Override
	public String storeAsString()
	{
		return value;
	}
}
