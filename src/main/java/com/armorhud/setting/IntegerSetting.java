package com.armorhud.setting;

import com.armorhud.feature.Feature;

public class IntegerSetting extends Setting<Integer>
{

	private int value;

	public IntegerSetting(String name, int value, Feature feature)
	{
		super(name, feature);
		this.value = value;
	}

	@Override
	public void loadFromStringInternal(String string)
	{
		value = Integer.parseInt(string);
	}

	@Override
	public String storeAsString()
	{
		return Integer.toString(value);
	}

	@Override
	public Integer getValue()
	{
		return value;
	}
}
