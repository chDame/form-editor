package com.cda.form.compil.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cda.form.compil.model.StructuredFormData;
import com.cda.form.exception.TechnicalException;
import com.cda.form.model.FormData;

public class FormDataUtils {

	private FormDataUtils() {}
	
	public static StructuredFormData getStructuredData(List<FormData> dataList) {
		StructuredFormData result = new StructuredFormData();
		Map<String, FormData> dataMap = new HashMap<>();
		Map<String, Set<String>> dependsOn = new HashMap<>();
		Map<String, Set<String>> dependsOnFull = new HashMap<>();
		Map<String, Long> indices = new HashMap<>();
		Map<Long, List<FormData>> ordered = new HashMap<>();
		result.setOrderedData(ordered);
		ordered.put(0L, new ArrayList<>());
		for (FormData data : dataList) {
			if (data.getType().equals("String") || data.getType().equals("JSON") || data.getType().equals("URL Parameter")) {
				indices.put(data.getName(), 0L);
				ordered.get(0L).add(data);
			} else {
				Set<String> dependancies = FormDataUtils.getShortenDependancies(data);
				Set<String> watchDependancies = FormDataUtils.getFullDependancies(data);
				if (dependancies.isEmpty()) {
					indices.put(data.getName(), 1L);
					if (!ordered.containsKey(1L)) {
						ordered.put(1L, new ArrayList<>());
					}
					ordered.get(1L).add(data);
				} else {
					dependsOn.put(data.getName(), dependancies);
					dependsOnFull.put(data.getName(), watchDependancies);
					dataMap.put(data.getName(), data);
				}
			}
		}
		result.setWatches(new HashMap<>());

		for (Map.Entry<String, Set<String>> depOn : dependsOnFull.entrySet()) {
			for (String dep : depOn.getValue()) {
				if (!result.getWatches().containsKey(dep)) {
					result.getWatches().put(dep, new HashSet<>());
				}
				result.getWatches().get(dep).add(depOn.getKey());
			}
		}
		for (Map.Entry<String, Set<String>> depOn : dependsOn.entrySet()) {
			for (String dep : depOn.getValue()) {
				if (dataMap.containsKey(dep) && dataMap.get(dep).getType().equals("URL")) {
					result.getDependsOnUrl().add(depOn.getKey());
				}
			}
		}
		
		int prevouslyRemaining = -1;
		while (prevouslyRemaining!=dependsOn.size()) {
			prevouslyRemaining = dependsOn.size();
			Set<String> keys = new HashSet<>(dependsOn.keySet());
			for(String dataName : keys) {
				Set<String> dependancies = dependsOn.get(dataName);
				boolean completed = indices.keySet().containsAll(dependancies);
				if (completed) {
					Long max = 0L;
					for (String dep : dependancies) {
						Long idx = indices.get(dep);
						max=idx>max?idx:max;
					}
					max+=1;
					indices.put(dataName, max);
					if (!ordered.containsKey(max)) {
						ordered.put(max, new ArrayList<>());
					}
					ordered.get(max).add(dataMap.get(dataName));
					dependsOn.remove(dataName);
				}
			}
		}
		//if there are still dependencies, they are cyclic
		if (dependsOn.size()>0) {
			throw new TechnicalException("It seems you have cyclic dependancies in your data store : "+String.join(",", dependsOn.keySet()));
		}
		
		buildDataStore(result);
		buildMethods(result);
		buildCreated(result);
		buildWatch(result);
		
		return result;
	}

	public static Set<String> getShortenDependancies(FormData data) {
		return getDependancies(data, "(this\\.\\$store\\.[a-zA-Z0-9]+)");
	}
	public static Set<String> getFullDependancies(FormData data) {
		return getDependancies(data, "(this\\.\\$store\\.[a-zA-Z0-9\\.]+)");
	}
	public static Set<String> getDependancies(FormData data, String depPattern) {
		Set<String> dependencies = new HashSet<>();
		String val = data.getValue();

		if (!val.contains("this.$store")) {
			return dependencies;
		}
		if (data.getType().equals("URL")) {
			val = buildUrl(val);
		}
		val = simplify(val);
		if (!val.contains("this.$store")) {
			return dependencies;
		}
				
		Pattern pattern = Pattern.compile(depPattern);
        Matcher matcher = pattern.matcher(val);

        while(matcher.find()) {
        	dependencies.add(matcher.group().substring(12));
        }
		return dependencies;
	}
	
	public static String simplify(String val) {
		int indexQuote = val.indexOf('\'');
		int indexDoubleQuote = val.indexOf('\"');
		int startRemoval = -1;
		int endRemoval = -1;

		if (indexQuote>=0 && (indexDoubleQuote<0 || indexQuote<indexDoubleQuote)) {
			endRemoval = val.indexOf('\'', indexQuote+1);
			startRemoval = indexQuote;
			while (endRemoval>0 && val.charAt(endRemoval-1)=='\'') {
				endRemoval = val.indexOf('\'', endRemoval+1);
			}
		}
		if (indexDoubleQuote>=0 && endRemoval<0) {
			endRemoval = val.indexOf('"', indexDoubleQuote+1);
			startRemoval = indexDoubleQuote;
			while (endRemoval>0 && val.charAt(endRemoval-1)=='\'') {
				endRemoval = val.indexOf('"', endRemoval+1);
			}
		}
		if (endRemoval>0) {
			val = val.substring(0, startRemoval)+val.substring(endRemoval+1, val.length());
			if (val.indexOf('\'')>0 || val.indexOf('\"')>0) {
				return simplify(val);
			}
		}
		return val;
	}
	private static void buildDataStore(StructuredFormData structuredDataStore) {
		structuredDataStore.getDataStore().append("const store = Vue.observable({");

		for(Long level : structuredDataStore.getOrderedData().keySet()) {
			for(FormData data : structuredDataStore.getOrderedData().get(level)) {
				structuredDataStore.getDataStore().append(getBasicData(data));
			}
		}
		structuredDataStore.getDataStore().append("}); Vue.prototype.$store = store;");
	}
	private static void buildMethods(StructuredFormData structuredDataStore) {
		
		for(Long level : structuredDataStore.getOrderedData().keySet()) {
			if (level>0) {
				for(FormData data : structuredDataStore.getOrderedData().get(level)) {
					structuredDataStore.getMethods().append(getLoadingMethod(data));
				}
			}
		}
	}
	private static void buildCreated(StructuredFormData structuredDataStore) {
		
		for(Long level : structuredDataStore.getOrderedData().keySet()) {
			if (level>0) {
				for(FormData data : structuredDataStore.getOrderedData().get(level)) {
					String method = getCreatedMethod(data);
					structuredDataStore.getDataCreationMethodMap().put(data.getName(), method);
					//if data depends on URL, we will defer loading to watch
					if (!structuredDataStore.getDependsOnUrl().contains(data.getName())) {
						structuredDataStore.getCreated().append(method);
					}
				}
			}
		}
	}
	private static void buildWatch(StructuredFormData structuredDataStore) {
		
		for(Map.Entry<String, Set<String>> watch : structuredDataStore.getWatches().entrySet()) {
			structuredDataStore.getWatchers().append("'$store.").append(watch.getKey()).append("' : function (val) {");
			
			for(String impact : watch.getValue()) {
				structuredDataStore.getWatchers().append(structuredDataStore.getDataCreationMethodMap().get(impact));
			}
			structuredDataStore.getWatchers().append("},");
		}
	}
	
	private static String getBasicData(FormData data) {
		if (data.getType().equals("String")) {
			return "\""+data.getName()+"\":\""+data.getValue()+"\",";
		}
		if (data.getType().equals("URL Parameter")) {
			 return "\""+data.getName()+"\": new URLSearchParams(window.location.search).get('"+data.getValue()+"'),";
		}
		if (data.getType().equals("JSON")) {
			return "\""+data.getName()+"\":"+data.getValue()+",";
		}
		return "\""+data.getName()+"\":null,";
	}
	
	private static String getLoadingMethod(FormData data) {
		if (data.getType().equals("Javascript")) {
			return "eval"+data.getName()+"() {"+data.getValue()+"},";
		}
		if (data.getType().equals("URL")) {
			 return "eval"+data.getName()+"() {axios.get("+buildUrl(data.getValue())+").then(response => {\r\n"
			 		+ "  this.$store."+data.getName()+" = response.data;"
			 		+ "}).catch(error => {"
			 		+ "  this.$store."+data.getName()+" = error.message;"
			 		+ "})},";
		}
		return "eval"+data.getName()+"() { alert('Method not defined for "+data.getName()+"')},";
	}
	
	private static String getCreatedMethod(FormData data) {
		if (data.getType().equals("Javascript")) {
			return "this.$store."+data.getName()+" = this.eval"+data.getName()+"();";
		}
		return "this.eval"+data.getName()+"();";
	}
	
	private static String buildUrl(String value) {
		return "'"+value.replace("{{", "'+").replace("}}", "+'")+"'";
	}
}
