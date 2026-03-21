import org.apache.lucene.index.*;
import org.apache.lucene.store.*;
import java.nio.file.*;

public class CheckIdentifierIndex {
  public static void main(String[] args) throws Exception {
    Path p = Paths.get("index");
    try (Directory dir = FSDirectory.open(p); DirectoryReader reader = DirectoryReader.open(dir)) {
      int docsWithField = 0;
      for (LeafReaderContext ctx : reader.leaves()) {
        LeafReader leaf = ctx.reader();
        Terms terms = leaf.terms("identifier_exact");
        if (terms != null) {
          docsWithField += leaf.maxDoc();
          TermsEnum te = terms.iterator();
          int shown = 0;
          while (shown < 20 && te.next() != null) {
            System.out.println(te.term().utf8ToString());
            shown++;
          }
        }
      }
      System.out.println("docsWithIdentifierField=" + docsWithField);
    }
  }
}
