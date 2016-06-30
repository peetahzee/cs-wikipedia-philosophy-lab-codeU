/**
 * 
 */
package com.flatironschool.javacs;

import static org.junit.Assert.*;

import java.io.IOException;
import java.util.List;

import org.junit.Test;
import org.junit.After;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/**
 * @author downey
 *
 */
public class WikiPhilosophyTest {

	@Test
	public void testProcessParagraphStandardLink() {
		Document doc = Jsoup.parse("<p>This is a link to a <a href=\"/wiki/test\">test</a>. " + 
		  	"This is an <a href=\"/wiki/test2\">another link</a>.</p>");
		Element el = doc.getElementsByTag("p").get(0);
		List<String> newUrls = WikiPhilosophy.getLinksFromParagraph(el);
		assertEquals(newUrls.size(), 2);
		assertTrue(newUrls.contains("/wiki/test"));
		assertTrue(newUrls.contains("/wiki/test2"));
	}

	@Test
	public void testProcessParagraphExternalLink() {
		Document doc = Jsoup.parse("<p>This is a link to a <a href=\"/wiki/test\">test</a>. " +
			  "This is a link to <a href=\"https://google.com\">Google</a>.</p>");
		Element el = doc.getElementsByTag("p").get(0);
		List<String> newUrls = WikiPhilosophy.getLinksFromParagraph(el);
		assertEquals(newUrls.size(), 1);
		assertTrue(newUrls.contains("/wiki/test"));
	}

	@Test
	public void testProcessParagraphItalicizedLink() {
		Document doc = Jsoup.parse("<p>This is an <em>italicized link to a <a href=\"/wiki/test\">test</a></em>. " +
			  "This is a <b><u><i>bold underlined italicized link to <a href=\"/wiki/test2\">Google</a></i></u></b>.</p>");
		Element el = doc.getElementsByTag("p").get(0);
		List<String> newUrls = WikiPhilosophy.getLinksFromParagraph(el);
		assertEquals(newUrls.size(), 0);
	}

	@Test
	public void testProcessParagraphParenLink() {
		Document doc = Jsoup.parse("<p>This is a link to a <a href=\"/wiki/test\">test</a>. " + 
		  	"This is a (by-the-way <a href=\"/wiki/test2\">another link</a>).</p>");
		Element el = doc.getElementsByTag("p").get(0);
		List<String> newUrls = WikiPhilosophy.getLinksFromParagraph(el);
		assertEquals(newUrls.size(), 1);
		assertTrue(newUrls.contains("/wiki/test"));
	}
}
