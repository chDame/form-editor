package com.cda.form.compil.utils;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.util.List;
import java.util.Set;

import org.assertj.core.util.Lists;
import org.junit.jupiter.api.Test;

import com.cda.form.compil.model.StructuredFormData;
import com.cda.form.exception.TechnicalException;
import com.cda.form.model.FormData;
public class FormDataUtilsTests {
	@Test
	void simplifyTest() {
		String result = FormDataUtils.simplify("this.$store.text+'blop'+this.$store.toto");
		assertThat(result).isEqualTo("this.$store.text++this.$store.toto");
		result = FormDataUtils.simplify("this.$store.text+'blop'+this.$store.toto+' blop'+");
		assertThat(result).isEqualTo("this.$store.text++this.$store.toto++");
		result = FormDataUtils.simplify("this.$store.text+\"'blop\"+this.$store.toto+' blop'+");
		assertThat(result).isEqualTo("this.$store.text++this.$store.toto++");
		result = FormDataUtils.simplify("this.$store.text+'\'blop'+this.$store.toto+' blop \"coucou\"'+");
		assertThat(result).isEqualTo("this.$store.text++this.$store.toto++");
		result = FormDataUtils.simplify("this.$store.text+'\'blop'+this.$store.toto+' blop \"coucou\"'+'+fouin");
		assertThat(result).isEqualTo("this.$store.text++this.$store.toto++'+fouin");
		result = FormDataUtils.simplify("{\"blop\":this.$store.text,'fou':this.$store.toto}");
		assertThat(result).isEqualTo("{:this.$store.text,:this.$store.toto}");
	}
	
	@Test
	void getDepenciesTest() {
		FormData data = new FormData();
		data.setType("JSON");
		data.setValue("{\"blop\":this.$store.text2,'fou':this.$store.toto}");
		Set<String> dependencies = FormDataUtils.getShortenDependancies(data);
		assertThat(dependencies).containsExactlyInAnyOrder("text2", "toto");
		data.setValue("this.$store.2to@blop+'\'blop'+this.$store.toto+' blop \"coucou\"'+'+fouin");
		dependencies = FormDataUtils.getShortenDependancies(data);
		assertThat(dependencies).containsExactlyInAnyOrder("2to", "toto");
	}
	

	@Test
	void getStructuredDataTest() {
		FormData dataString = new FormData();
		dataString.setValue("test");
		dataString.setName("myStr");
		dataString.setType("String");
		
		FormData dataJson = new FormData();
		dataJson.setValue("{\"blop\":3,'fou':'test'}");
		dataJson.setName("myJson");
		dataJson.setType("JSON");

		FormData dataJavascript = new FormData();
		dataJavascript.setValue("return this.$store.myJson.blop+=5");
		dataJavascript.setName("myJs");
		dataJavascript.setType("Javascript");

		FormData dataUrl = new FormData();
		dataUrl.setValue("/test?toto={{this.$store.myJs}}");
		dataUrl.setName("myUrl");
		dataUrl.setType("URL");
		
		List<FormData> data = Lists.list(dataUrl, dataJson, dataJavascript, dataString);
		StructuredFormData structured = FormDataUtils.getStructuredData(data);
		assertThat(structured.getOrderedData().get(0L)).containsExactlyInAnyOrder(dataJson, dataString);
		assertThat(structured.getOrderedData().get(1L)).containsExactlyInAnyOrder(dataJavascript);
		assertThat(structured.getOrderedData().get(2L)).containsExactlyInAnyOrder(dataUrl);
		
		assertThat(structured.getWatches().get("myJs")).contains("myUrl");
		assertThat(structured.getWatches().get("myJson.blop")).contains("myJs");
	}
	
	@Test()
	void getStructuredDataExceptionTest() {
		
		FormData dataJson = new FormData();
		dataJson.setValue("{\"blop\":3,'fou':'test'}");
		dataJson.setName("myJson");
		dataJson.setType("JSON");

		FormData dataJavascript = new FormData();
		dataJavascript.setValue("return this.$store.myJson.blop+=5");
		dataJavascript.setName("myJs");
		dataJavascript.setType("Javascript");

		FormData dataUrl = new FormData();
		dataUrl.setValue("/test?toto={{this.$store.myUrl2}}");
		dataUrl.setName("myUrl");
		dataUrl.setType("URL");

		FormData dataUrl2 = new FormData();
		dataUrl2.setValue("/test?toto={{this.$store.myUrl}}");
		dataUrl2.setName("myUrl2");
		dataUrl2.setType("URL");
		
		
		List<FormData> data = Lists.list(dataUrl, dataUrl2, dataJson, dataJavascript);

		Exception exception = assertThrows(TechnicalException.class, () -> {
			FormDataUtils.getStructuredData(data);
	    });
	}
}

