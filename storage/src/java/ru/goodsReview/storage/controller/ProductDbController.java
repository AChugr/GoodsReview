package ru.goodsReview.storage.controller;

import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.simple.SimpleJdbcTemplate;
import ru.goodsReview.core.model.Product;
import ru.goodsReview.storage.mapper.ProductMapper;

import java.sql.Types;
import java.util.List;
import java.util.ArrayList;

/**
 * User: Artemij
 * Date: 17.10.11
 * Time: 19:04
 */
public class ProductDbController {
    private SimpleJdbcTemplate simpleJdbcTemplate;
    private ProductMapper productMapper;

    public ProductDbController(SimpleJdbcTemplate simpleJdbcTemplate) {
        this.simpleJdbcTemplate = simpleJdbcTemplate;
        this.productMapper = new ProductMapper();
    }

    public long addProduct(Product product) {
        try {
            simpleJdbcTemplate.getJdbcOperations().update("INSERT INTO product (category_id, name, description, popularity) VALUES(?,?,?,?)",
                    new Object[]{product.getCategoryId(), product.getName(), product.getDescription(), product.getPopularity()},
                    new int[]{Types.INTEGER, Types.VARCHAR, Types.VARCHAR, Types.INTEGER});
            long lastId = simpleJdbcTemplate.getJdbcOperations().queryForLong("SELECT LAST_INSERT_ID()");
            return lastId;
        } catch (DataAccessException e) {
            // We don't have permissions to update the table.
            // TODO(serebryakov): Log the error.
            e.printStackTrace();
        }
        return -1;
    }

    public List<Long> addProductList(List<Product> productList) {
        List<Long> ids = new ArrayList<Long>();
        for (Product product : productList) {
            ids.add(addProduct(product));
        }
        return ids;
    }

    public List<Product> getAllProducts() {
        List<Product> products =
                simpleJdbcTemplate.getJdbcOperations().query("SELECT * FROM product", productMapper);
        return products;
    }

    /**
     * Returns specified number of most popular products.
     * @param n Number of products needed.
     * @return Specified number of most popular products.
     */
    public List<Product> getPopularProducts(int n) {
        List<Product> products =
                simpleJdbcTemplate.getJdbcOperations().query("SELECT * FROM product ORDER BY popularity DESC LIMIT ?",
                        new Object[]{n},
                        new int[]{Types.INTEGER},
                        productMapper);
        return products;
    }

    public Product getProductById(long product_id) {
        List<Product> products =
                simpleJdbcTemplate.getJdbcOperations().query("SELECT * FROM product WHERE id = ?",
                        new Object[]{product_id},
                        new int[]{Types.INTEGER},
                        productMapper);
        return products.get(0);
    }

    public Product getProductByName(String product_name) {
        List<Product> products =
                simpleJdbcTemplate.getJdbcOperations().query("SELECT * FROM product WHERE name = ?",
                        new Object[]{product_name},
                        new int[]{Types.VARCHAR},
                        productMapper);
        return products.get(0);
    }

    public List<Product> getProductsByCategory(long category_id) {
        List<Product> products =
                simpleJdbcTemplate.getJdbcOperations().query("SELECT * FROM product WHERE category_id = ?",
                        new Object[]{category_id},
                        new int[]{Types.INTEGER},
                        productMapper);
        return products;
    }
}