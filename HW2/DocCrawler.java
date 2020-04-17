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

package edu.uci.ics.crawler4j.examples.doccrawler;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.pdfbox.PDFReader;

import com.google.common.io.Files;

import edu.uci.ics.crawler4j.crawler.Page;
import edu.uci.ics.crawler4j.crawler.WebCrawler;
import edu.uci.ics.crawler4j.parser.BinaryParseData;
import edu.uci.ics.crawler4j.parser.HtmlParseData;
import edu.uci.ics.crawler4j.url.WebURL;

/**
 * @author Yasser Ganjisaffar
 */

/*
 * This class shows how you can crawl images on the web and store them in a
 * folder. This is just for demonstration purposes and doesn't scale for large
 * number of images. For crawling millions of images you would need to store
 * downloaded images in a hierarchy of folders
 */
public class ImageCrawler extends WebCrawler {

	private static final Pattern FILTERS = Pattern.compile(
		      ".*(\\.(css|js|bmp|gif|jpg|jpe?g|png|tiff?|mid|mp2|mp3|mp4|wav|avi|mov|mpeg|ram|m4v" +
		      "|rm|smil|wmv|swf|wma|zip|rar|gz|php|iso|ico))$");

	  private static final Pattern docPatterns = Pattern.compile(".*(\\.(pdf"+"|doc|docx))$");

  private static File storageFolder;
  private static String[] crawlDomains;

  public static void configure(String[] domain, String storageFolderName) {
    crawlDomains = domain;

    storageFolder = new File(storageFolderName);
    if (!storageFolder.exists()) {
      storageFolder.mkdirs();
    }
  }

  @Override
  public boolean shouldVisit(Page referringPage, WebURL url) {
    String href = url.getURL().toLowerCase();
    if (FILTERS.matcher(href).matches()) {
      return false;
    }

   if (docPatterns.matcher(href).matches() ) {
      return true;
    }

    for (String domain : crawlDomains) {
    	
      if (href.startsWith(domain)) {
        return true;
      }
    }
    return false;
  }
  
  @Override
  public void visit(Page page) {
    String url = page.getWebURL().getURL();
    String url_split[]=url.split("/");
    //System.out.println(temp[2]);
    // We are only interested in processing images which are bigger than 10k
   if (!(url_split[2].contains("marshall") && (docPatterns.matcher(url).matches()||(page.getParseData() instanceof HtmlParseData)))) {
      return;
    }
  
    int links_size=0;
    Set<WebURL> links = null;
    if (page.getParseData() instanceof HtmlParseData) {
        HtmlParseData htmlParseData = (HtmlParseData) page.getParseData();
        String text = htmlParseData.getText();
        String html = htmlParseData.getHtml();
       links = htmlParseData.getOutgoingUrls();

    //   System.out.println("Text length: " + text.length());
      //  System.out.println("Html length: " + html.length());
        //System.out.println("Number of outgoing links: " + links.size());
        links_size=links.size();
     // get a unique name for storing this image
        //String extension = url.substring(url.lastIndexOf('.'));
        String hashedName = UUID.randomUUID() + ".html";

        // store image   
        String filename = storageFolder.getAbsolutePath() + "/" + hashedName;
        File fname=new File(storageFolder.getAbsolutePath() + "/" + hashedName);
        
     
        try {
          Files.write(page.getContentData(), fname);
          System.out.println("URL: "+ url+" SIZE: "+fname.length()+"   OutgoingLinks: " + 
          links_size+"   ContentType: "+page.getContentType()+" NAME OF FILE: "+ fname);

          FileWriter fileWritter = new FileWriter("marshallV5.csv",true);
          BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
          bufferWritter.write(url+","+fname.length()+","+page.getContentType()+","+fname+","+links_size+",");
          Iterator iterator = links.iterator(); 
          
          // check values
          while (iterator.hasNext()){
          //System.out.println("Value: "+iterator.next() + " ");  
          bufferWritter.write(iterator.next()+",");
          }
          bufferWritter.write("\n");
          

          bufferWritter.close();
        } catch (IOException iox) {
          logger.error("Failed to write file: " + filename, iox);
        }
      }
   
    if (page.getParseData() instanceof BinaryParseData) {
    	BinaryParseData binaryParseData = (BinaryParseData) page.getParseData();
        //String text =((HtmlParseData) binaryParseData).getText();
        //String html = binaryParseData.getOutgoingUrls();
         links = binaryParseData.getOutgoingUrls();

    //   System.out.println("Text length: " + text.length());
      //  System.out.println("Html length: " + html.length());
        //System.out.println("Number of outgoing links: " + links.size());
        links_size=links.size();
     // get a unique name for storing this image
        String extension = url.substring(url.lastIndexOf('.'));
        String hashedName = UUID.randomUUID() + extension;

        // store image   
        String filename = storageFolder.getAbsolutePath() + "/" + hashedName;
        File fname=new File(storageFolder.getAbsolutePath() + "/" + hashedName);
        
     
        try {
          Files.write(page.getContentData(), fname);
          System.out.println("URL: "+ url+" SIZE: "+fname.length()+"   OutgoingLinks: " + 
          links_size+"   ContentType: "+page.getContentType()+" NAME OF FILE: "+ fname);

          FileWriter fileWritter = new FileWriter("marshallV5.csv",true);
          BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
          bufferWritter.write(url+","+fname.length()+","+page.getContentType()+","+fname+","+links_size+",");
          Iterator iterator = links.iterator(); 
          
          // check values
          while (iterator.hasNext()){
          //System.out.println("Value: "+iterator.next() + " ");  
          bufferWritter.write(iterator.next()+",");
          }
          bufferWritter.write("\n");
          bufferWritter.close();
        } catch (IOException iox) {
          logger.error("Failed to write file: " + filename, iox);
        }
      }
    
    
    
  }
}
