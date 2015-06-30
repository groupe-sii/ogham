package fr.sii.ogham.core.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicNameValuePair;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import fr.sii.ogham.core.exception.template.BeanException;
import fr.sii.ogham.core.exception.util.HttpException;

public class HttpUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
	private static final HttpClient CLIENT = HttpClientBuilder.create().useSystemProperties().build();
	
	public static Response get(String url, List<Parameter> params) throws HttpException {
		String fullUrl = url;
		String paramsStr = URLEncodedUtils.format(convert(params), "UTF-8");
		fullUrl += (fullUrl.contains("?") ? "&" : "?") + paramsStr;
		// spaces are replaced by '+' but some servers doesn't handle it correctly
		// => convert space to '%20'
		fullUrl = fullUrl.replaceAll("\\+", "%20");
		try {
			LOG.debug("Sending HTTP GET request to {}", fullUrl);
			HttpGet request = new HttpGet(fullUrl);
			HttpResponse response = CLIENT.execute(request);
			int statusCode = response.getStatusLine().getStatusCode();
			LOG.debug("HTTP GET request successfully sent to {}. Status code: {}", fullUrl, statusCode);
			return new Response(statusCode, IOUtils.toString(response.getEntity().getContent()));
		} catch (IOException e) {
			throw new HttpException("Failed to send GET request to "+fullUrl, e);
		}
	}
	
	public static Response get(String url, Parameter... params) throws HttpException {
		return get(url, Arrays.asList(params));
	}
	
	public static Response get(String url, Object... params) throws HttpException {
		try {
			Map<String, Object> map = new HashMap<>();
			for(Object bean : params) {
				if(bean instanceof Parameter) {
					Parameter p = (Parameter) bean;
					map.put(p.getName(), p.getValue());
				} else if(bean instanceof Map) {
					map.putAll((Map<String, Object>) bean);
				} else {
					map.putAll(BeanUtils.convert(bean));
				}
			}
			return get(url, map);
		} catch (BeanException e) {
			throw new HttpException("Failed to convert bean fields into request parameters", e);
		}
	}
	
	public static Response get(String url, Map<String, Object> params) throws HttpException {
		return get(url, convert(params));
	}


	private static List<Parameter> convert(Map<String, Object> map) {
		Set<Entry<String, Object>> entries = map.entrySet();
		List<Parameter> parameters = new ArrayList<>(entries.size());
		for(Entry<String, Object> entry : entries) {
			if(entry.getValue()!=null) {
				parameters.add(new Parameter(entry.getKey(), entry.getValue().toString()));
			}
		}
		return parameters;
	}
	
	private static List<NameValuePair> convert(List<Parameter> params) {
		List<NameValuePair> pairs = new ArrayList<>(params.size());
		for(Parameter param : params) {
			if(param.getValue()!=null) {
				pairs.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		return pairs;
	}
	
	
	public static class Parameter {
		private final String name;
		
		private final String value;

		public Parameter(String name, String value) {
			super();
			this.name = name;
			this.value = value;
		}

		public String getName() {
			return name;
		}

		public String getValue() {
			return value;
		}
	}
	
	
	public static class Response {
		private final int status;
		
		private final String body;

		public Response(int status, String body) {
			super();
			this.status = status;
			this.body = body;
		}

		public int getStatus() {
			return status;
		}

		public String getBody() {
			return body;
		}
	}
	
	
	private HttpUtils() {
		super();
	}
}
