package fr.sii.notification.core.util;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

public class HtmlUtils {
	private static final Pattern HTML_PATTERN = Pattern.compile("<html", Pattern.CASE_INSENSITIVE);
	private static final String CSS_LINKS_SELECTOR = "link[rel*=\"stylesheet\"], link[type=\"text/css\"], link[href$=\".css\"]";
	private static final String HREF_ATTR = "href";
	private static final String IMG_SELECTOR = "img";
	private static final String SRC_ATTR = "src";
	
	public static boolean isHtml(String content) {
		return HTML_PATTERN.matcher(content).find();
	}
	
	public static List<String> getCssFiles(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements els = doc.select(CSS_LINKS_SELECTOR);
		List<String> cssFiles = new ArrayList<>(els.size());
		for(Element e : els) {
			cssFiles.add(e.attr(HREF_ATTR));
		}
		return cssFiles;
	}
	
	public static List<String> getImages(String htmlContent) {
		Document doc = Jsoup.parse(htmlContent);
		Elements els = doc.select(IMG_SELECTOR);
		List<String> images = new ArrayList<>(els.size());
		for(Element e : els) {
			images.add(e.attr(SRC_ATTR));
		}
		return images;
	}
	
	private HtmlUtils() {
		super();
	}
}
