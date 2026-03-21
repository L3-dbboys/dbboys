package com.dbboys.util;

import java.nio.file.Paths;
import java.util.List;

public class CheckUnderscoreSearch {
  public static void main(String[] args) throws Exception {
    String keyword = args.length > 0 ? args[0] : "LAST_LOG_RESERVED4BACKUP";
    LuceneSearcher searcher = new LuceneSearcher(Paths.get("index"));
    List<LuceneSearcher.SearchResult> results = searcher.search(keyword, 10);
    System.out.println("keyword=" + keyword + ", count=" + results.size());
    for (int i = 0; i < results.size() && i < 5; i++) {
      LuceneSearcher.SearchResult r = results.get(i);
      System.out.println("--- " + (i + 1));
      System.out.println(r.path);
      System.out.println("score=" + r.score);
      System.out.println((r.snippet == null ? "" : r.snippet).replace('\r', ' ').replace('\n', ' '));
    }
  }
}
