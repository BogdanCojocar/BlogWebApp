package org.home.webservice;

import java.io.IOException;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import org.springframework.stereotype.Component;

import freemarker.template.Configuration;
import freemarker.template.Template;
import freemarker.template.TemplateException;

@Component
public class BlogWebServiceFrontend {

	public static final String BLOG_TEMPLATE = "blog_template.ftl";
	public static final String ENTRY_TEMPLATE = "entry_template.ftl";
	public static final String ERROR_TEMPLATE = "error_template.ftl";
	public static final String LOGIN_TEMPLATE = "login.ftl";
	public static final String NEWPOST_TEMPLATE = "newpost_template.ftl";
	public static final String POST_NOT_FOUND_TEMPLATE = "post_not_found.ftl";
	public static final String SIGNUP_TEMPLATE = "signup.ftl";
	public static final String TEST_TEMPLATE = "test.ftl";
	public static final String WELCOME_TEMPLATE = "welcome.ftl";
	
	private static final String TEMPLATE_PATH = "/templates";
	private static Configuration configuration = new Configuration();
	private static final Logger LOG = Logger
			.getLogger(BlogWebServiceFrontend.class.getName());

	public BlogWebServiceFrontend() {
		configuration.setClassForTemplateLoading(BlogWebServiceFrontend.class,
				TEMPLATE_PATH);
	}

	public String addTemplate(String templateName,
			Map<String, Object> templateDataMap) {
		StringWriter writer = new StringWriter();
		try {
			Template template = configuration.getTemplate(templateName);
			template.process(templateDataMap, writer);
		} catch (TemplateException e) {
			LOG.info("Exception while processing the template " + e);
		} catch (IOException e) {
			LOG.info("Exception while reading the template file " + e);
		}

		return writer.toString();
	}

	public String testTemplate() {
		Map<String, Object> testMap = new HashMap<String, Object>();
		testMap.put("name", "World");
		return addTemplate(TEST_TEMPLATE, testMap);
	}
}
