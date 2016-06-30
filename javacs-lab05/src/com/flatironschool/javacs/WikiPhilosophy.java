package com.flatironschool.javacs;

import java.io.IOException;
import java.lang.Math;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Deque;
import java.util.Hashtable;

import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;

import org.jsoup.select.Elements;

public class WikiPhilosophy {
  final static String BASE_URL = "https://en.wikipedia.org";
  final static String BEGIN_URL = "/wiki/Java_(programming_language)";
  final static String END_URL = "/wiki/Philosophy";
  final static WikiFetcher wf = new WikiFetcher();

  
  public static void main(String[] args) throws IOException {
    // Used as a Queue for a BFS, a Stack for a DFS.
    Deque<String> urlsToVisit = new ArrayDeque<String>();
    // Keep track of which URLs we have visited, so we don't get ourselves stuck in a loop.
    List<String> visitedUrls = new ArrayList<String>();
    // Keep track of how we got to each page, so that we can find our trace back to the BEGIN_URL.
    Hashtable<String, String> referrers = new Hashtable<String, String>();
    boolean pathFound = false;

    // Begin with the BEGIN_URL
    urlsToVisit.push(BEGIN_URL);

    while(!urlsToVisit.isEmpty() && !pathFound) {
      String currentUrl = urlsToVisit.pop();
      visitedUrls.add(currentUrl);

      Elements paragraphs = wf.fetchWikipedia(BASE_URL + currentUrl);
      List<String> pageUrls = new ArrayList<String>();

      for (Element p : paragraphs) {
        pageUrls.addAll(getLinksFromParagraph(p));
      }

      // Reverse the order of all page urls so that when we're done pushing all the URLS
      // to the stack, the first URL in the page will be the first URL in the current page.
      Collections.reverse(pageUrls);

      // Add all the URLs to the list of URLs to visit.
      for (String newUrl : pageUrls) {
        if(!visitedUrls.contains(newUrl)) {
          urlsToVisit.push(newUrl);
          // Record how we ended up at newUrl.
          referrers.put(newUrl, currentUrl);

          // Check if one of the links in this page is the END_URL; which means we'll be done!
          if (newUrl.equals(END_URL)) {
            pathFound = true;
          }
        }
      }
    }

    if (pathFound) {
      System.out.println("=================");
      System.out.println("Path found!");
      System.out.println("=================");

      // Back trace how we ended up at END_URL.
      String backtraceUrl = END_URL;
      System.out.println(backtraceUrl);
      while(backtraceUrl != BEGIN_URL) {
        String referrerUrl = referrers.get(backtraceUrl);
        System.out.println(referrerUrl);
        backtraceUrl = referrerUrl;
      }
    } else {
      System.out.println("=================");
      System.out.println("No path found :(");
      System.out.println("=================");
    }
  }

  static List<String> getLinksFromParagraph(Element paragraph) {
    Iterable<Node> iter = new WikiNodeIterable(paragraph);
    List<String> newUrls = new ArrayList<String>();
    int numOfParens = 0;

    for (Node node: iter) {
      if (node instanceof Element) {
        Element el = (Element) node;
        if (isWikiLink(el) && !isItalicized(node) && numOfParens == 0) {
          newUrls.add(el.attr("href"));
        }
      } else if (node instanceof TextNode) {
        numOfParens += countChar(node.toString(), '(');
        numOfParens = Math.max(numOfParens - countChar(node.toString(), ')'), 0);
      }
    }
    return newUrls;
  }

  static boolean isWikiLink(Element el) {
    return el.tagName() == "a" && el.attr("href").indexOf("/wiki/") == 0;
  }

  static boolean isItalicized(Node node) {
    while(node.parentNode() != null) {
      Node parent = node.parentNode();
      if (parent instanceof Element) {
        Element parentEl = (Element) parent;
        if (parentEl.tagName() == "i" || parentEl.tagName() == "em") {
          return true;
        }
      }
      node = parent;
    }
    return false;
  }

  static int countChar(String s, char targetChar) {
    int count = 0;
    for (char c : s.toCharArray()) {
      if (c == targetChar) { count++; }
    }
    return count;
  }
}
