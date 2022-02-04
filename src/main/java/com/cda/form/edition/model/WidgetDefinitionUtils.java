package com.cda.form.edition.model;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WidgetDefinitionUtils {

	private static List<WidgetStructure> widgets = null;
	private static List<WidgetStructure> containers = null;

	public static List<WidgetStructure> getWidgets() {
		if (widgets==null) {
			widgets = new ArrayList<>();

			widgets.add(buildWidget("widget", "input-element", "Input", true, "bi bi-input-cursor-text",
					"<div><div class='mb-3' v-if='!props.icon'><label :for='props.id' class='form-label'>{{props.label}}</label>"
							+ "<input :id='props.id' :type='props.type' class='form-control' :placeholder='props.placeholder' v-model='props.value'></input>"
							+ "</div>"
							+ "<label v-if='props.icon' :for='props.id' class='form-label'>{{props.label}}</label>"
							+ "<div v-if='props.icon' class='input-group mb-3'><span class='input-group-text'><i :class='props.icon'/></span>"
							+ "<input :type='props.type' class='form-control' :id='props.id':placeholder='props.placeholder' v-model='props.value'></div></div>",
							buildPropDef("required", PropertyTypeEnum.BOOLEAN, Boolean.TRUE),
							buildPropDef("disabled", PropertyTypeEnum.BOOLEAN, Boolean.FALSE),
							buildPropDef("label", PropertyTypeEnum.TEXT, "label :"),
							buildPropDef("type", PropertyTypeEnum.LIST, "text", null, true,  
									Arrays.asList(new String[]{"text", "number", "email", "password"})),
							buildPropDef("icon", PropertyTypeEnum.TEXT, "bi bi-input-cursor-text"),
							buildPropDef("value", PropertyTypeEnum.BINDING),
							buildPropDef("labelPosition", PropertyTypeEnum.LIST, "top", null, true,  
									Arrays.asList(new String[]{"top", "left"})),
							buildPropDef("placeholder", PropertyTypeEnum.TEXT, "placeholder"),
							buildPropDef("min", PropertyTypeEnum.NUMBER, null, "field.props.type=='number'"),
							buildPropDef("max", PropertyTypeEnum.NUMBER, null, "field.props.type=='number'"),
							buildPropDef("minlength", PropertyTypeEnum.NUMBER, null, "field.props.type!='number'"),
							buildPropDef("maxlength", PropertyTypeEnum.NUMBER, null, "field.props.type!='number'")));

			widgets.add(buildWidget("widget", "checkbox-element", "CheckBox", true, "bi bi-check-square",
					"<div class='form-check mb-1'><input class='form-check-input' type='checkbox'><label class='form-check-label'>{{props.label}}</label></div>",
					buildPropDef("required", PropertyTypeEnum.BOOLEAN, Boolean.TRUE),
					buildPropDef("disabled", PropertyTypeEnum.BOOLEAN, Boolean.FALSE),
					buildPropDef("label", PropertyTypeEnum.TEXT, "label")));

			widgets.add(buildWidget("widget", "button-element", "Button", true, "bi bi-send",
					"<button :class='[props.outlined ? \"btn btn-outline-\"+props.style: \"btn btn-\"+props.style]'><i :class='props.icon' v-if='props.icon'></i> {{ props.label }}</button>",
					buildPropDef("outlined", PropertyTypeEnum.BOOLEAN, Boolean.FALSE),
					buildPropDef("disabled", PropertyTypeEnum.BOOLEAN, Boolean.FALSE),
					buildPropDef("label", PropertyTypeEnum.TEXT, "button"),
					buildPropDef("icon", PropertyTypeEnum.TEXT, "bi bi-send"),
					buildPropDef("style", PropertyTypeEnum.LIST, "primary", null, true,  
							Arrays.asList(new String[]{"primary", "secondary", "info", "success", "danger"}))));		
		}
		return widgets;
	}
	public static List<WidgetStructure> getContainers() {
		if (containers==null) {
			containers = new ArrayList<>();
			containers.add(buildWidget("contentWidget", "panel-element", "Panel", false, "rowwidget",
					"<div class='card'>"+
							"<div class='card-header'>{{props.title}}</div>"+
							"<div class='card-body'>"+
							"<form-content :content='props.content'></form-content>"+
							"</div>"+
							"</div>",
							buildPropDef("title", PropertyTypeEnum.TEXT, "panel")));
			containers.add(buildWidget("contentWidget", "row-element", null, false, null,
					"<div class='row'><form-content :content='props.content'></form-content></div>"));
		}
		return containers;
	}

	public static String getRenderingComponents() {
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
		for (WidgetStructure widget : getContainers()) {
			sb.append(buildComponent(widget.getNature(), widget.getTemplate()));
		}
		for (WidgetStructure widget : getWidgets()) {
			sb.append(buildComponent(widget.getNature(), widget.getTemplate()));
		}		
		return sb.toString();
	}
	private static String buildComponent(String name, String template) {
		template = template.replaceAll("<form-content [^<]+></form-content>", "<slot></slot>");
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
		StringBuilder sb = new StringBuilder("Vue.component('").append(name)
				.append("', {template: \"").append(template.replace("\"", "\\\"")).append("\",props: ['props']");
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


	private static WidgetStructure buildWidget(String type, String nature, String display, boolean sizeable, String icon, String template, PropertyDefinition... propDefs) {
		WidgetStructure widget = new WidgetStructure();
		widget.setType(type);
		widget.setNature(nature);
		widget.setDisplay(display);
		widget.setSizeable(sizeable);
		widget.setIcon(icon);
		widget.setTemplate(template);
		widget.setPropsDef(Arrays.asList(propDefs));
		return widget;
	}
	private static PropertyDefinition buildPropDef(String name, PropertyTypeEnum type) {
		return buildPropDef(name, type, null);
	}
	private static PropertyDefinition buildPropDef(String name, PropertyTypeEnum type, Object defaultValue) {
		return buildPropDef(name, type, defaultValue, null);
	}
	private static PropertyDefinition buildPropDef(String name, PropertyTypeEnum type, Object defaultValue, String condition) {
		return buildPropDef(name, type, defaultValue, condition, false, null);
	}
	private static PropertyDefinition buildPropDef(String name, PropertyTypeEnum type, Object defaultValue, String condition, boolean required) {
		return buildPropDef(name, type, defaultValue, condition, required, null);
	}

	private static PropertyDefinition buildPropDef(String name, PropertyTypeEnum type, Object defaultValue, String condition, boolean required, List<String> values) {
		PropertyDefinition prodDef = new PropertyDefinition();
		prodDef.setName(name);
		prodDef.setType(type);
		prodDef.setDefaultValue(defaultValue);
		prodDef.setCondition(condition);
		prodDef.setValues(values);
		prodDef.setRequired(required);
		return prodDef;
	}

}
