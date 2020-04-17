/**
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package edu.uci.ics.crawler4j.examples.basic;

import java.io.File;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.http.Header;
import org.apache.http.HttpStatus;
import org.apache.poi.ss.usermodel.Workbook;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.fetcher.PageFetchResult;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar
 */
public class BasicCrawler extends WebCrawler {

	  private static final Pattern FILTERS = Pattern.compile(
		      ".*(\\.(css|js|bmp|gif|jpg|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" +
		      "|rm|smil|wmv|swf|wma|zip|rar|gz|php|iso|ico))$");

		  private static final Pattern imgPatterns = Pattern.compile(".*(\\.(pdf"+"|doc|docx|docm"+"|htm|html))$");

  /**
   * You should implement this function to specify whether the given url
   * should be crawled or not (based on your crawling logic).
   */
  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    // Ignore the url if it has an extension that matches our defined set of image extensions.
   
    if(!FILTERS.matcher(href).matches()&& !href.contains("usc.edu") && !href.contains("marshall"))
    System.out.println("outUSC $$ "+url);
    
    if (!FILTERS.matcher(href).matches() && href.contains("usc.edu") && !href.contains("marshall")){
  	  System.out.println("USC $$ "+url);
  	  //return true;
    }
    
      if (!FILTERS.matcher(href).matches() && href.contains("www.marshall.usc.edu")){
    	// System.out.println("OK $$ "+url);
    	  return true;
      }
    
	return false;
      
   //return !FILTERS.matcher(href).matches(); //&& href.startsWith("http://www.marshall.usc.edu/");
    // Only accept the url if it is in the "www.ics.uci.edu" domain and protocol is "http".
   // return href.startsWith("http://www.marshall.usc.edu/");
  }

  /**
   * This function is called when a page is fetched and ready to be processed
   * by your program.
   */
  @Override
  public void visit(Page page) {
    int docid = page.getWebURL().getDocid();
    String url = page.getWebURL().getURL();
    
    String domain = page.getWebURL().getDomain();
    String path = page.getWebURL().getPath();
    String subDomain = page.getWebURL().getSubDomain();
    String parentUrl = page.getWebURL().getParentUrl();
    String anchor = page.getWebURL().getAnchor();

   // int statusCode =page.getWebURL().getStatus();
    //System.out.println("Visited: " + url);
   // System.out.println("Docid: {}"+ docid);
    //System.out.println(docid+" "+url);
    //System.out.println("Domain: '{}'"+ domain);
    //System.out.println("Sub-domain: '{}'"+ subDomain);
    //System.out.println("Path: '{}'"+path);
    //System.out.println("Parent page: {}"+ parentUrl);
    //System.out.println("Anchor text: {}"+anchor);
    
    if (page.getParseData() instanceof HtmlParseData) {
      HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
      String text = htmlParseData.getText();
      String html = htmlParseData.getHtml();
      Set<WebURL> links = htmlParseData.getOutgoingUrls();

    //  System.out.println("Text length: " + text.length());
      //System.out.println("Html length: " + html.length());
      //System.out.println("Number of outgoing links: " + links.size());
    }

    Header[] responseHeaders = page.getFetchResponseHeaders();
    if (responseHeaders != null) {
    	//System.out.println("Response headers:");
      for (Header header : responseHeaders) {
    	  //System.out.println("\t{}: {}"+header.getName()+ header.getValue());
      }
    }

    logger.debug("=============");
  }

  
  @Override
  protected void handlePageStatusCode(WebURL webUrl, int statusCode, String statusDescription) {


  System.out.println("OK $$ "+webUrl);
   
  }
}
