package ru.goodsReview.miner.listener;

/**
 * User: Alexander Marchuk
 * aamarchuk@gmail.com
 * Date: 10/26/11
 * Time: 6:27 AM
 */


import org.apache.log4j.Logger;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import org.webharvest.runtime.Scraper;
import org.webharvest.runtime.ScraperRuntimeListener;
import org.webharvest.runtime.processors.BaseProcessor;
import ru.goodsReview.core.model.Review;
import ru.goodsReview.storage.controller.ProductDbController;
import ru.goodsReview.storage.controller.ReviewDbController;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;


public class CitilinkNotebooksScraperRuntimeListener implements ScraperRuntimeListener {

    private int i = 0;
    private static double GOOD_FEAUTURE_POS = 5.0;
    private static double BAD_FEAUTURE_POS = -5.0;
    private static final Logger log = Logger.getLogger(CitilinkNotebooksScraperRuntimeListener.class);

    protected SimpleJdbcTemplate jdbcTemplate;
    protected ReviewDbController reviewDbController;
    protected ProductDbController productDbController;

    public CitilinkNotebooksScraperRuntimeListener(SimpleJdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        reviewDbController = new ReviewDbController(jdbcTemplate);
    }

    public void onExecutionStart(Scraper scraper) {

    }

    public void onExecutionPaused(Scraper scraper) {
    }

    public void onExecutionContinued(Scraper scraper) {
    }

    public void onNewProcessorExecution(Scraper scraper, BaseProcessor baseProcessor) {
    }

    public void onExecutionEnd(Scraper scraper) {


    }

    public void onProcessorExecutionFinished(Scraper scraper, BaseProcessor baseProcessor, Map map) {

        if ("body".equalsIgnoreCase(
                scraper.getRunningProcessor().getElementDef().getShortElementName()) && (scraper.getRunningLevel() == 6)) {

            String nameProd = scraper.getContext().get("ProductName").toString();
            String prodPrice = scraper.getContext().get("Price").toString();
            String reviewTime = scraper.getContext().get("ReviewTime").toString();
            String starRate = scraper.getContext().get("StarRate").toString();
            String description = scraper.getContext().get("Description").toString();
            String goodFeatures = scraper.getContext().get("GoodFeatures").toString();
            String badFeatures = scraper.getContext().get("BadFeatures").toString();
            String comments = scraper.getContext().get("Comments").toString();
            String voteYes = scraper.getContext().get("VoteYes").toString();
            String voteNo = scraper.getContext().get("VoteNo").toString();

            Date date = new Date();
            //todo getProduct(Product product)
            Review goodFeauture = new Review(1, goodFeatures, "anonim", date, "", 1, "source", GOOD_FEAUTURE_POS, 0.0, 0,0);
            Review badFeauture = new Review(1, badFeatures, "anonim", date, "", 1, "source", BAD_FEAUTURE_POS, 0.0, 0,0);
            Review comment = new Review(1, comments, "anonim", date, "", 1, "source", 0.0, 0.0, 0,0);
            List<Review> reviewList = new ArrayList<Review>();
            reviewList.add(goodFeauture);
            reviewList.add(badFeauture);
            reviewList.add(comment);
            reviewDbController.addReviewList(reviewList);
            /*Review rev = new Review(1, goodFeatures + "\n" + badFeatures + "\n" + comments, "anonim",
                    new Date(), description, 1, "source_url", 0.0, 0.0, 0, 0);
            rev.setAuthor("anonim");
            rev.setDescription(description);
            rev.setVotesNo(Integer.parseInt(voteNo));
            rev.setVotesYes(Integer.parseInt(voteYes));
            rev.setDate(date);
            rev.setImportance(0);
            rev.setPositivity(0);
            //todo sourceUrl= citilink.ru   imho
            rev.setSourceUrl("citilink.ru/catalog/computers_and_notebooks/notebooks/");

            reviewDbController.addReview(rev);
            log.info(" New review addded: ID=" + rev.getId());      */
        }
    }

    public void onExecutionError(Scraper scraper, Exception e) {
        if (e != null) {
            log.error("CitilinkNotebooksScraperRuntimeListener error");
        }
    }
}
