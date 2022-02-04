package com.cda.form.compil.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.cda.form.compil.model.StructuredFormData;
import com.cda.form.edition.model.WidgetDefinitionUtils;
import com.cda.form.exception.TechnicalException;
import com.cda.form.model.ContentWidget;
import com.cda.form.model.Form;
import com.cda.form.model.FormData;
import com.cda.form.model.PropFn;
import com.cda.form.model.Widget;

public class FormUtils {

	private FormUtils() {
		
	}
	
	public static String buildFullApplication(Form form) {
		String header = buildHeader(form);
		String template = buildVueAppTemplate(form);
		String script =buildVueAppScript(form);
		return "<html>"+header+"<body>"+template+script+"</body></html>";
	}
	
	public static String buildHeader(Form form) {
		return "<head><script src=\"/js/vue-2.6.14.min.js\"></script>"
				//+ "<script src=\"/js/components.js\"></script>"
				+ "</script><script src=\"/js/axios-0.24.0.min.js\"></script>"
				+ "<meta charset=\"utf-8\">"
				+ "<meta name=\"viewport\" content=\"width=device-width, initial-scale=1\">"
				+ "<link href=\"/bootstrap/css/bootstrap.min.css\" rel=\"stylesheet\">"
				+ "<link href=\"/bootstrap/icons/bootstrap-icons.css\" rel=\"stylesheet\">"
				+ "</head>";
	}
	
	public static String buildVueAppTemplate(Form form) {
		StringBuilder vueStr = new StringBuilder("<div id=\"").append(form.getName()).append("\" class=\"container-fluid\">");
		for(ContentWidget w : form.getContent()) {
			vueStr.append(buildContentWidget(w));
		}
		vueStr.append("</div>");
		return vueStr.toString();
	}
	
	public static String buildVueAppScript(Form form) {
		StructuredFormData formData = FormDataUtils.getStructuredData(form.getData());

		StringBuilder vueScript = new StringBuilder("<script>").append(formData.getDataStore().toString())
				.append(WidgetDefinitionUtils.getRenderingComponents())
				.append("let app = new Vue({el: '#").append(form.getName())
				.append("', data() {return {}}, methods:{")
				.append(formData.getMethods().toString()).append("}, created() {")
				.append(formData.getCreated().toString())
				.append("}, watch: {").append(formData.getWatchers().toString())
				.append("}});</script>");
		return vueScript.toString();
	}
	
	public static String buildContentWidget(ContentWidget w) {
		String props = buildProps(w);

		StringBuilder widgetStr = new StringBuilder("<div").append(buildWidgetParentDivClass(w)).append("><").append(w.getNature()).append(" :props='").append(props).append("'>");
		for(Widget content : w.getContent()) {
			if (content instanceof ContentWidget) {
				widgetStr.append(buildContentWidget((ContentWidget) content));
			} else {
				widgetStr.append(buildWidget(content));
			}
		}
		widgetStr.append("</").append(w.getNature()).append("></div>");
		return widgetStr.toString();
	}
	
	public static String buildWidget(Widget w) {
		String props = buildProps(w);
		return "<div"+buildWidgetParentDivClass(w)+"><"+w.getNature()+" :props='"+props+"'>"+
			"</"+w.getNature()+"></div>";
	}
	
	public static String buildWidgetParentDivClass(Widget w) {
		if(w.getProps().containsKey("lg") && w.getProps().containsKey("md")) {
			String classname = (String) w.getProps().get("class");
			if (classname==null) {
				classname="";
			}
			return " class=\""+classname+" col-lg-"+w.getProps().get("lg")+" col-md-"+w.getProps().get("md")+
					" col-sm-"+w.getProps().get("sm")+" col-"+w.getProps().get("xs")+
					"\"";
		}
		return "";
	}
	
	public static String buildProps(Widget w) {
		StringBuilder propStr = new StringBuilder("{");
		for (Map.Entry<String, Object> propEntry : w.getProps().entrySet()) {
			propStr.append('"').append(propEntry.getKey()).append("\" : ");
			PropFn fn = w.getPropsFn().get(propEntry.getKey());
			if (fn!=null && fn.isActive()) {
				propStr.append(fn.getValue());
			} else {
				if(propEntry.getValue() instanceof String)  {
					propStr.append('"').append(propEntry.getValue()).append('"');
				} else {
					propStr.append(propEntry.getValue());
				}
			}
			propStr.append(",");
		}
		if (w.getBinding()!=null) {
			for (Map.Entry<String, String> propEntry : w.getBinding().entrySet()) {
				propStr.append('"').append(propEntry.getKey()).append("\" : \"")
				.append(propEntry.getValue()).append("\",");
			}
		}
		propStr.append("}");
		return propStr.toString().replace(",}", "}");
	}
}
