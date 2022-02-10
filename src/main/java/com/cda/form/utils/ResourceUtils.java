package com.cda.form.utils;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;

public class ResourceUtils {

	public static List<String> getWidgets() throws IOException {
		List<String> result = new ArrayList<>();
		ClassLoader cl = ResourceUtils.class.getClassLoader(); 
		ResourcePatternResolver resolver = new PathMatchingResourcePatternResolver(cl);
		Resource[] resources = resolver.getResources("classpath*:/widgets/*/widget.json");
		for (Resource resource: resources){
			result.add(resource.getFile().getParentFile().getName());
		}
		return result;
	}
	
	public static boolean exists(String file) {
		ClassLoader cl = ResourceUtils.class.getClassLoader(); 
		return cl.getResource(file)!=null;
	}
}
