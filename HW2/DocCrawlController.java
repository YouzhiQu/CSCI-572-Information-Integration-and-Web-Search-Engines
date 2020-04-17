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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uci.ics.crawler4j.crawler.CrawlConfig;
import edu.uci.ics.crawler4j.crawler.CrawlController;
import edu.uci.ics.crawler4j.fetcher.PageFetcher;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtConfig;
import edu.uci.ics.crawler4j.robotstxt.RobotstxtServer;

/**
 * @author Yasser Ganjisaffar
 */
public class ImageCrawlController {
  private static final Logger logger = LoggerFactory.getLogger(ImageCrawlController.class);

  public static void main(String[] args) throws Exception {
	 

    String rootFolder = "/Users/apple/Desktop/USC/CSCI 572";
    int numberOfCrawlers =7;
    String storageFolder = "/Users/apple/Desktop/USC/CSCI 572/marshallV5";

    CrawlConfig config = new CrawlConfig();
    config.setMaxDepthOfCrawling(5);
    config.setCrawlStorageFolder(rootFolder);

    /*
     * Since images,docs and pdfs are binary content, we need to set this parameter to
     * true to make sure they are included in the crawl.
     */
    config.setIncludeBinaryContentInCrawling(true);
    config.setMaxPagesToFetch(5000);
    //System.out.println(config.getMaxDownloadSize());
  config.setMaxDownloadSize(10000000);
  //System.out.println(config.getMaxDownloadSize());
 
    String[] crawlDomains = {"http://www.marshall.usc.edu/"};

    PageFetcher pageFetcher = new PageFetcher(config);
    RobotstxtConfig robotstxtConfig = new RobotstxtConfig();
    RobotstxtServer robotstxtServer = new RobotstxtServer(robotstxtConfig, pageFetcher);
    CrawlController controller = new CrawlController(config, pageFetcher, robotstxtServer);
    for (String domain : crawlDomains) {
      controller.addSeed(domain);
    }

    DocCrawler.configure(crawlDomains, storageFolder);

    controller.start(DocCrawler.class, numberOfCrawlers);
  }
}
