package com.cda.form.edition.model;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.io.IOUtils;
import org.json.JSONArray;
import org.json.JSONObject;

import com.cda.form.utils.ResourceUtils;
import com.fasterxml.jackson.databind.ObjectMapper;

public class WidgetDefinitionUtils {

	private static List<WidgetStructure> widgets = null;
	private static List<WidgetStructure> containers = null;
	private static ObjectMapper mapper = new ObjectMapper();
	private static boolean initiazed = false;
	
	private static synchronized void init() throws IOException {
		if (!initiazed) {
			widgets = new ArrayList<>();
			containers = new ArrayList<>();
			List<String> ressourceWidgets = ResourceUtils.getWidgets();
			for(String widgetFolder : ressourceWidgets) {
				String widgetFile = "/widgets/"+widgetFolder+"/widget.json";
				String propertiesFile = "/widgets/"+widgetFolder+"/properties.json";
				String templateFile = "/widgets/"+widgetFolder+"/template.txt";
				String methodFile = "/widgets/"+widgetFolder+"/methods.txt";
			
				String widgetStr = IOUtils.resourceToString(widgetFile, StandardCharsets.UTF_8);
				String properties = IOUtils.resourceToString(propertiesFile, StandardCharsets.UTF_8);
				String template = IOUtils.resourceToString(templateFile, StandardCharsets.UTF_8);
				template = template.replaceAll("[\\n\\r\\t]", "");
				JSONObject widgetJson = new JSONObject(widgetStr);
				widgetJson.put("template", template);
				widgetJson.put("propsDef", new JSONArray(properties));
				try {
					widgetJson.put("methods", IOUtils.resourceToString(methodFile, StandardCharsets.UTF_8));
				} catch (IOException e) {
					//methods don't exists
				}
				
				WidgetStructure widget = mapper.readValue(widgetJson.toString(), WidgetStructure.class);
				if (widget.getType().equals("contentWidget")) {
					containers.add(widget);
				} else {
					widgets.add(widget);
				}
			}
			initiazed = true;
		}
	}
	public static List<WidgetStructure> getWidgets() throws IOException {
		init();
		return widgets;
	}
	public static List<WidgetStructure> getContainers() throws IOException {
		init();
		return containers;
	}

	public static String getRenderingComponents() throws IOException {
		StringBuilder sb = new StringBuilder("Vue.prototype.setPath=function(prop, val) {"
				+ "	let path = prop.split('.');"
				+ "	let obj = store;"
				+ "	while(path.length>1) {"
				+ "		obj = obj[path[0]];"
				+ "		path.splice(0,1);"
				+ "	}"
				+ "	Vue.set(obj, path[0], val);"
				+ "};"
				+ "Vue.prototype.getPath=function(prop) {"
				+ "	let path = prop.split('.');"
				+ "	let obj = store;"
				+ "	while(path.length>1) {"
				+ "		obj = obj[path[0]];"
				+ "		path.splice(0,1);"
				+ "	}"
				+ "	return obj[path[0]];"
				+ "};");
		sb.append("let ModalService = {"
				+ "  modal: {},"
				+ "  currentModal:null,"
				+ "  open: function (modalName) {"
				+ "			if (!this.modal[modalName]) { this.modal[modalName] = new bootstrap.Modal(document.getElementById(modalName), {});}"
				+ "			this.modal[modalName].show(); this.currentModal = modalName;"
				+ "  },"
				+ "	 close: function() {"
				+ "    this.modal[this.currentModal].hide();"
				+ "  }"
				+ "};"
				+ "var modalService = Object.create(ModalService);");
		for (WidgetStructure widget : getContainers()) {
			sb.append(buildComponent(widget));
		}
		for (WidgetStructure widget : getWidgets()) {
			sb.append(buildComponent(widget));
		}		
		return sb.toString();
	}
	private static String buildComponent(WidgetStructure widget) {
		String template = widget.getTemplate().replaceAll("<form-content [^<]+></form-content>", "<slot></slot>");
		Set<String> bindings = new HashSet<>();
		if (template.contains("v-model=")) {
			Pattern pattern = Pattern.compile("v\\-model='[^']+'|v\\-model=\"[^\"]+\"");
	        Matcher matcher = pattern.matcher(template);

	        while(matcher.find()) {
	        	String vmodel = matcher.group();
	        	String binding = vmodel.substring(9, vmodel.length()-1);
	        	bindings.add(binding);
	        	template = template.replace(vmodel, "v-model='"+binding.toLowerCase().replace(".", "")+"Accessor'");
	        }
		}
		StringBuilder sb = new StringBuilder("Vue.component('").append(widget.getNature())
				.append("', {template: \"").append(template.replace("\"", "\\\"")).append("\",props: ['props']");
		
		if (widget.getMethods()!=null) {
			sb.append(",methods: ").append(widget.getMethods());
		}
		if (bindings.size()>0) {
			sb.append(",computed: {");
			for(String binding : bindings) {
				sb.append(binding.toLowerCase().replace(".", "")).append("Accessor:{")
					.append("get() {return this.getPath(this.").append(binding).append(");},")
					.append("set(val) {this.setPath(this.").append(binding).append(",val);}}");
					
			}
			sb.append("}");
		}
		sb.append("});");
		return sb.toString();
	}

}
