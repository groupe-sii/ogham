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
import fr.sii.ogham.core.util.http.Parameter;
import fr.sii.ogham.core.util.http.Response;

/**
 * Utility class that helps to send HTTP requests.
 * 
 * @author Aurélien Baudet
 *
 */
public class HttpUtils {
	private static final Logger LOG = LoggerFactory.getLogger(HttpUtils.class);
	private static final HttpClient CLIENT = HttpClientBuilder.create().useSystemProperties().build();

	/**
	 * Do a GET request on the provided URL and construct the Query String part
	 * with the provided list of parameters. If the URL already contains
	 * parameters (already contains a '?' character), then the parameters are
	 * added to the existing parameters. The parameters are converted into
	 * <code>application/x-www-form-urlencoded</code>. For example:
	 * <code>field1=value1&field1=value2&field2=value3</code>. The special
	 * characters are encoded. If there is a space, it is encoded into '%20'.
	 * 
	 * @param url
	 *            the base url
	 * @param params
	 *            the list of parameters to append to the query string
	 * @return the response
	 * @throws HttpException
	 *             when the request has failed
	 */
	public static Response get(String url, List<Parameter> params) throws HttpException {
		String fullUrl = url;
		String paramsStr = URLEncodedUtils.format(convert(params), "UTF-8");
		fullUrl += (fullUrl.contains("?") ? "&" : "?") + paramsStr;
		// spaces are replaced by '+' but some servers doesn't handle it
		// correctly
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
			throw new HttpException("Failed to send GET request to " + fullUrl, e);
		}
	}

	/**
	 * Do a GET request on the provided URL and construct the Query String part
	 * with the provided list of parameters. If the URL already contains
	 * parameters (already contains a '?' character), then the parameters are
	 * added to the existing parameters. The parameters are converted into
	 * <code>application/x-www-form-urlencoded</code>. For example:
	 * <code>field1=value1&field1=value2&field2=value3</code>. The special
	 * characters are encoded. If there is a space, it is encoded into '%20'.
	 * 
	 * @param url
	 *            the base url
	 * @param params
	 *            none, one or several parameters to append to the query string
	 * @return the response
	 * @throws HttpException
	 *             when the request has failed
	 */
	public static Response get(String url, Parameter... params) throws HttpException {
		return get(url, Arrays.asList(params));
	}

	/**
	 * <p>
	 * Do a GET request on the provided URL and construct the Query String part
	 * with the provided list of parameters. If the URL already contains
	 * parameters (already contains a '?' character), then the parameters are
	 * added to the existing parameters. The parameters are converted into
	 * <code>application/x-www-form-urlencoded</code>. For example:
	 * <code>field1=value1&field1=value2&field2=value3</code>. The special
	 * characters are encoded. If there is a space, it is encoded into '%20'.
	 * </p>
	 * The parameters can be anything:
	 * <ul>
	 * <li>{@link Parameter}: see {@link #get(String, Parameter...)}</li>
	 * <li>{@link Map}: each entry is used as a parameter (see
	 * {@link #get(String, Map)}). The key of the entry is the name of the
	 * parameter, the value of the entry is the value of the parameter</li>
	 * <li>A bean (any object): each property of the bean is used as parameter
	 * (see {@link BeanUtils}). The name of the property is the name of the
	 * parameter, the value of the property is the value of the parameter</li>
	 * </ul>
	 * 
	 * @param url
	 *            the base url
	 * @param params
	 *            none, one or several parameters to append to the query string
	 * @return the response
	 * @throws HttpException
	 *             when the request has failed
	 */
	@SuppressWarnings("unchecked")
	public static Response get(String url, Object... params) throws HttpException {
		try {
			Map<String, Object> map = new HashMap<>();
			for (Object bean : params) {
				if (bean instanceof Parameter) {
					Parameter p = (Parameter) bean;
					map.put(p.getName(), p.getValue());
				} else if (bean instanceof Map) {
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

	/**
	 * Do a GET request on the provided URL and construct the Query String part
	 * with the provided list of parameters. If the URL already contains
	 * parameters (already contains a '?' character), then the parameters are
	 * added to the existing parameters. The parameters are converted into
	 * <code>application/x-www-form-urlencoded</code>. For example:
	 * <code>field1=value1&field1=value2&field2=value3</code>. The special
	 * characters are encoded. If there is a space, it is encoded into '%20'.
	 * <p>
	 * Each entry of the map is used as a parameter. The key of the entry is the
	 * name of the parameter, the value of the entry is the value of the
	 * parameter
	 * </p>
	 * 
	 * @param url
	 *            the base url
	 * @param params
	 *            none, one or several parameters to append to the query string
	 * @return the response
	 * @throws HttpException
	 *             when the request has failed
	 */
	public static Response get(String url, Map<String, Object> params) throws HttpException {
		return get(url, convert(params));
	}

	/**
	 * Convert the map into a list of parameters
	 * 
	 * @param map
	 *            the map to convert
	 * @return the list of parameters
	 */
	private static List<Parameter> convert(Map<String, Object> map) {
		Set<Entry<String, Object>> entries = map.entrySet();
		List<Parameter> parameters = new ArrayList<>(entries.size());
		for (Entry<String, Object> entry : entries) {
			if (entry.getValue() != null) {
				parameters.add(new Parameter(entry.getKey(), entry.getValue().toString()));
			}
		}
		return parameters;
	}

	/**
	 * Convert a list of parameters to a list of {@link NameValuePair}.
	 * 
	 * @param params
	 *            the parameters abstraction used in the library
	 * @return the parameters used by the real implementation (Apache Commons
	 *         HTTP)
	 */
	private static List<NameValuePair> convert(List<Parameter> params) {
		List<NameValuePair> pairs = new ArrayList<>(params.size());
		for (Parameter param : params) {
			if (param.getValue() != null) {
				pairs.add(new BasicNameValuePair(param.getName(), param.getValue()));
			}
		}
		return pairs;
	}

	private HttpUtils() {
		super();
	}
}
