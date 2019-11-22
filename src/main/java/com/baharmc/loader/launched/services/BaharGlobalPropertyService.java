package com.baharmc.loader.launched.services;

import com.baharmc.loader.launched.common.BaharLaunched;
import org.spongepowered.asm.service.IGlobalPropertyService;

public class BaharGlobalPropertyService implements IGlobalPropertyService {

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key) {
		return (T) BaharLaunched.getInstance().getProperties().get(key);
	}

	@Override
	public void setProperty(String key, Object value) {
		BaharLaunched.getInstance().getProperties().put(key, value);
	}

	@SuppressWarnings("unchecked")
	@Override
	public <T> T getProperty(String key, T defaultValue) {
		return (T) BaharLaunched.getInstance().getProperties().getOrDefault(key, defaultValue);
	}

	@Override
	public String getPropertyString(String key, String defaultValue) {
		Object o = BaharLaunched.getInstance().getProperties().get(key);
		return o != null ? o.toString() : defaultValue;
	}

}
