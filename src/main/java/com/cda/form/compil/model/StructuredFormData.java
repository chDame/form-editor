package com.cda.form.compil.model;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cda.form.model.FormData;

public class StructuredFormData {
	private Map<Long, List<FormData>> orderedData;
	private Map<String, String> dataCreationMethodMap = new HashMap<>();
	private Map<String, Set<String>> watches;
	private Set<String> dependsOnUrl = new HashSet<>();
	private StringBuilder dataStore = new StringBuilder();
	private StringBuilder methods = new StringBuilder();
	private StringBuilder watchers = new StringBuilder();
	private StringBuilder created = new StringBuilder();
	public Map<Long, List<FormData>> getOrderedData() {
		return orderedData;
	}
	public void setOrderedData(Map<Long, List<FormData>> ordered) {
		this.orderedData = ordered;
	}
	public Map<String, Set<String>> getWatches() {
		return watches;
	}
	public void setWatches(Map<String, Set<String>> watches) {
		this.watches = watches;
	}
	public StringBuilder getDataStore() {
		return dataStore;
	}
	public void setDataStore(StringBuilder dataStore) {
		this.dataStore = dataStore;
	}
	public StringBuilder getMethods() {
		return methods;
	}
	public void setMethods(StringBuilder methods) {
		this.methods = methods;
	}
	public StringBuilder getWatchers() {
		return watchers;
	}
	public void setWatchers(StringBuilder watchers) {
		this.watchers = watchers;
	}
	public StringBuilder getCreated() {
		return created;
	}
	public void setCreated(StringBuilder created) {
		this.created = created;
	}
	public Map<String, String> getDataCreationMethodMap() {
		return dataCreationMethodMap;
	}
	public void setDataCreationMethodMap(Map<String, String> dataCreationMethodMap) {
		this.dataCreationMethodMap = dataCreationMethodMap;
	}
	public Set<String> getDependsOnUrl() {
		return dependsOnUrl;
	}
	public void setDependsOnUrl(Set<String> dependsOnUrl) {
		this.dependsOnUrl = dependsOnUrl;
	}
	
}
