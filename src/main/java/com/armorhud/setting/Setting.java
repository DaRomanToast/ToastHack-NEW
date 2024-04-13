package com.armorhud.setting;

import com.armorhud.feature.Feature;

public abstract class Setting<T> {
	private final String name;

	private final Feature feature;

	public Setting(String name, Feature feature) {
		this.name = name;

		this.feature = feature;
		feature.addSetting(this);
	}

	protected Feature getFeature() {
		return feature;
	}

	public String getName() {
		return name;
	}


	public abstract T getValue();

	public void loadFromString(String string) {
		loadFromStringInternal(string);
		feature.onChangeSetting(this);
	}

	protected abstract void loadFromStringInternal(String string);

	public abstract String storeAsString();
}
