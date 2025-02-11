package ru.goodsreview.miner.obsolete.utils;

import ru.goodsreview.core.db.controller.ProductController;
import ru.goodsreview.core.db.controller.ReviewController;
import ru.goodsreview.core.model.Product;
import ru.goodsreview.core.model.Review;
import ru.goodsreview.storage.controller.ProductDbController;
import ru.goodsreview.storage.controller.ReviewDbController;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.apache.log4j.Logger;

import javax.sql.DataSource;
import java.util.List;
import java.io.*;

/**
 * Date: 20.11.2011
 * Time: 23:28:27
 * Author:
 *   Sergey Serebryakov
 *   sergey.serebryakoff@gmail.com
 */

// TODO: Think out a good name for this class.
public class ReviewFileWriter {
    private static final String FILENAME = "data/miner/comments.txt";
    private static final String BEFORE_PRODUCT_DELIMITER = "<product>";
    private static final String AFTER_PRODUCT_DELIMITER = "</product>";
    private static final String BEFORE_REVIEW_DELIMITER = "<review>";
    private static final String AFTER_REVIEW_DELIMITER = "</review>";
    private static final String SPACE = "    ";


    private static final Logger log = Logger.getLogger(ReviewFileWriter.class);

    public static void writeReviewsToFile(DataSource dataSource) throws IOException {
        try {
            PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(FILENAME)));

            ProductController productController = new ProductDbController(new SimpleJdbcTemplate(dataSource));
            ReviewController reviewController = new ReviewDbController(new SimpleJdbcTemplate(dataSource));
            List<Product> products = productController.getAllProducts();

            for (Product product : products) {
                /*out.println(BEFORE_PRODUCT_DELIMITER);
                out.println(SPACE + product.getName());
                out.println(AFTER_PRODUCT_DELIMITER); */
                out.println(BEFORE_PRODUCT_DELIMITER + product.getName() + AFTER_PRODUCT_DELIMITER);

                List<Review> reviews = reviewController.getReviewsByProductId(product.getId());
                for (Review review : reviews) {
                    if(review.getContent().length()>3){
                        String str = review.getContent().replaceAll("\\n", "");
                        out.println(SPACE + BEFORE_REVIEW_DELIMITER + str + AFTER_REVIEW_DELIMITER);
                        /*out.println(SPACE + BEFORE_REVIEW_DELIMITER);
                        out.println(SPACE + SPACE + review.getContent());
                        out.println(SPACE + AFTER_REVIEW_DELIMITER);  */
                        out.println();
                    }
                }
            }

            out.close();
        } catch (IOException e) {
            log.error("I/O error when trying to write to file: " + FILENAME, e);
            throw e;
        }
    }
}
