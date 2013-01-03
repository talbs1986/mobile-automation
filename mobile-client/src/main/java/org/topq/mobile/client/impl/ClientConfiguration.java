package org.topq.mobile.client.impl;

import java.util.HashMap;
import java.util.Map;
import java.util.Properties;

import org.topq.mobile.common.client.enums.ClientProperties;

public class ClientConfiguration {
	
	private Properties clientProperties;
	
	public ClientConfiguration() {
		this.clientProperties = new Properties();
	}
	
	public ClientConfiguration buildProperty(ClientProperties property,String value) {
		this.clientProperties.put(property.name(), value);
		return this;
	}
	
	public boolean isPropertyExist(ClientProperties property) {
		final String value = this.clientProperties.getProperty(property.name());
		return value != null && !value.isEmpty();
	}
	
	public String getProperty(ClientProperties property) {
		return this.clientProperties.getProperty(property.name());
	}
	
	public boolean isEmpty() {
		return this.clientProperties.isEmpty();
	}
	
	public Map<String,String> toMap() {
		Map<String,String> result = new HashMap<String, String>();
		for (Object property : this.clientProperties.keySet()) {
			String propertyString = (String)property;
			result.put(propertyString, this.clientProperties.getProperty(propertyString));
		}
		return result;
	}

}
