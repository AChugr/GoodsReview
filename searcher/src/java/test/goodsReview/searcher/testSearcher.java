package goodsReview.searcher;

import org.apache.lucene.analysis.ru.RussianAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.SimpleFSDirectory;
import org.apache.lucene.util.Version;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import ru.goodsReview.indexer.Indexer;

import javax.sql.DataSource;
import java.io.File;

public class testSearcher {
    static final String DBDirectory = "database";
    public static void main(String[] args) throws Exception {
        final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("storage/src/scripts/beans.xml");
        DataSource dataSource = null;
        dataSource = (DataSource) context.getBean("dataSource");
        Indexer indexer = new Indexer(new SimpleJdbcTemplate(dataSource));
        indexer.doProductsIndex(DBDirectory);
        IndexSearcher indexSearcher = new IndexSearcher(new SimpleFSDirectory(new File(DBDirectory)));
        QueryParser queryParser = new QueryParser(Version.LUCENE_34, "",new RussianAnalyzer(Version.LUCENE_34));

        Query query = queryParser.parse("name:iPhone 4"); //"good" example
        TopDocs topDocs = indexSearcher.search(query,1);
        if (topDocs.getMaxScore() > 0) {
            Document document = indexSearcher.doc(topDocs.scoreDocs[0].doc); //H-H-H-H-H-H-HOLY SHIT!!!!1
            System.out.println(document.get("id")+" "+document.get("name")+" "+document.get("popularity")+" "+document.get("description"));
        } else {
            System.out.println("Sorry, no results!");
        }

        Query query1 = queryParser.parse("name:iMNotiPhone5, lol"); //"bad" example
        TopDocs topDocs1 = indexSearcher.search(query1,1); //Imma freakin' copypaster, yeah!
        if (topDocs1.getMaxScore() > 0) {
            Document document = indexSearcher.doc(topDocs1.scoreDocs[0].doc);
            System.out.println(document.get("id")+" "+document.get("name")+" "+document.get("popularity")+" "+document.get("description"));
        } else {
            System.out.println("Sorry, no results!");
        }
    }
}