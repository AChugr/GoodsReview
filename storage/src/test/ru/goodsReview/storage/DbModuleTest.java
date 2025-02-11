package ru.goodsreview.storage;

import junit.framework.Assert;
import org.apache.log4j.Logger;
import org.junit.Test;
import org.springframework.context.support.FileSystemXmlApplicationContext;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import ru.goodsreview.core.db.exception.StorageException;
import ru.goodsreview.core.model.*;
import ru.goodsreview.storage.controller.*;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.Random;

/*
    Date: 26.10.11
    Time: 00:52
    Author:
        Artemij Chugreev
        artemij.chugreev@gmail.com
*/
public class DbModuleTest {
    private static final Logger log = Logger.getLogger(DbModuleTest.class);

    @Test
    public void testSMB() {
        Assert.assertEquals(4, 2 + 2);
    }

    public static void main(String[] args) throws SQLException, StorageException {
        final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
                "storage/src/scripts/beans.xml");
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        SimpleJdbcTemplate simpleJdbcTemplate = new SimpleJdbcTemplate(dataSource);

        Timestamp time = new Timestamp(System.currentTimeMillis());
        ThesisUniqueDbController thesisUniqueDbController = new ThesisUniqueDbController(simpleJdbcTemplate);
        {
            ThesisUnique thesisUnique = new ThesisUnique("good", 2, time, 1.0, 1.0);
            thesisUniqueDbController.addThesisUnique(thesisUnique);
        }
        {
            ThesisUnique thesisUnique = new ThesisUnique("bad", 5, time, 1.0, 1.0);
            thesisUniqueDbController.addThesisUnique(thesisUnique);
        }
        {
            ThesisUnique thesisUnique = new ThesisUnique("uuuu", 20, time, 1.0, 1.0);
            thesisUniqueDbController.addThesisUnique(thesisUnique);
        }


    }

    public static void controllerUsingExample() throws StorageException {
        final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext(
                "storage/src/scripts/beans.xml");
        DataSource dataSource = (DataSource) context.getBean("dataSource");
        /* NOTE(serebryakov): I recommend to run this test on the clean database. */
        CategoryDbController categoryController = new CategoryDbController(new SimpleJdbcTemplate(dataSource));
        Category category0 = new Category(-1, "Mobile phones", "PHONES", 1);
        long category0id = categoryController.addCategory(category0);
        Category category1 = categoryController.getCategoryById(category0id);
        Assert.assertEquals(category0id, category1.getId());

        String productName = "Motorola RND" + (new Random().nextInt(1000000000));
        //ProductDbController productController = new ProductDbController();
        Product product0 = new Product(-1 /* any ID */, category0id, productName, "хороший товар", 10);
//        long product0id = productController.addProduct(product0);
//        Product product1 = productController.getProductByName(productName);
//        log.debug("Product name: " + product1.getName());
//        log.debug("Product desc: " + product1.getDescription());
//        log.debug("[id when insert: " + product0id + ", id when select: " + product1.getId() + "]");

        // NOTE(serebryakov): Who wrote these two lines? Please clarify your point or remove.
        /*Product productFrom = productController.getProductById(10);
        System.out.println("PRODUCT == " + productFrom.getDescription());*/

/*        SourceDbController sourceController = new SourceDbController(new SimpleJdbcTemplate(dataSource));
        Source source0 = new Source(-1, "Reliable source", "OK some url");
        long source0id = sourceController.addSource(source0);
        Source source1 = sourceController.getSourceById(source0id);
        Assert.assertEquals(source0id, source1.getId());
        log.debug("Source name: " + source1.getName());

        ReviewDbController reviewController = new ReviewDbController(new SimpleJdbcTemplate(dataSource));
        Review review0 = new Review(-1, product0id, "OK review content", "Sergey",
                                    System.currentTimeMillis(),
                                    "OK description",
                                    source0id, "http://url", 0.0, 0.0, 0, 0);
        long review0id = reviewController.addReview(review0);
        Review review1 = reviewController.getReviewById(review0id);
        Assert.assertEquals(review0id, review1.getId());
        log.debug("Review content: " + review1.getContent());
        log.debug("Review date: " + review1.getTime() + ", time: " + review1.getTime());
        review1.setContent("HAHAHA");
        reviewController.updateReview(review1);
        Review review2 = reviewController.getReviewById(review0id);
        Assert.assertEquals(review1.getContent(), review2.getContent());

        ThesisDbController thesisController = new ThesisDbController(new SimpleJdbcTemplate(dataSource));
        //todo change this constructor. Sorry.
        Thesis thesis0 = new Thesis(1, 1, "OK thesis content", 0, 0.0, 0.0);
        long thesis0id = thesisController.addThesis(thesis0);
        Thesis thesis1 = thesisController.getThesisById(thesis0id);
        log.debug("Thesis by ID = " + thesis0id);
        log.debug("Content: " + thesis1.getContent());
        log.debug("Theses for review ID = " + review0id);
        for (Thesis thesis : thesisController.getThesesByReviewId(review0id)) {
            log.debug("Content: " + thesis.getContent());
        }
        log.debug("Theses for product ID = " + product0id);
        for (Thesis thesis : thesisController.getThesesByProductId(product0id)) {
            log.debug("Content: " + thesis.getContent());
        }
    }

    /* public static void main(String[] args) throws SQLException {
       final FileSystemXmlApplicationContext context = new FileSystemXmlApplicationContext("storage/src/scripts/beans.xml");
       DataSource dataSource = null;
       dataSource = (DataSource) context.getBean("dataSource");
       ProductDbController baseController = new  ProductDbController(new SimpleJdbcTemplate(dataSource));
       List<Product> productsList = new ArrayList<Product>();
       productsList.add(new Product(1, "iphone 4", "mobile_phone_by_apple", 10));
       productsList.add(new Product(1, "htc desire", "mobile phone by HTC", 6));
       productsList.add(new Product(1, "nokia 3310", "cool mobile phone by Nokia", 100));
       productsList.add(new Product(1, "motorola", "bad phone", 100));

       baseController.addProductList(productsList);
 }   */

    }
}