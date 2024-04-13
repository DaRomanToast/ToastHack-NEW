package com.armorhud.setting;

import com.armorhud.feature.Feature;

public class BooleanSetting extends Setting<Boolean>
{
	private boolean value;

	public BooleanSetting(String name,boolean value, Feature feature)
	{
		super(name,feature);
		this.value = value;
	}

	@Override
	public Boolean getValue()
	{
		return value;
	}

	public void setValue(boolean value) {
		this.value = value;
	}

	@Override
	public void loadFromStringInternal(String string)
	{
		value = Boolean.parseBoolean(string);
	}

	@Override
	public String storeAsString()
	{
		return Boolean.toString(value);
	}

	public boolean isEnabled(){
		return false;
	}
}
