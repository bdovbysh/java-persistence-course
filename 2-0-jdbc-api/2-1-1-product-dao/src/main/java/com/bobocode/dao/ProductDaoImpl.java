package com.bobocode.dao;

import com.bobocode.exception.DaoOperationException;
import com.bobocode.model.Product;
import com.bobocode.util.ExerciseNotCompletedException;
import lombok.SneakyThrows;

import javax.sql.DataSource;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class ProductDaoImpl implements ProductDao {

    public static final String DELETE_FROM_PRODUCTS_BY_ID = "DELETE FROM PRODUCTS WHERE ID = ?";
    private static String UPDATE_PRODUCT_BY_ID_QUERY = "UPDATE PRODUCTS SET " +
            "name = ?, " +
            "producer = ?, " +
            "price = ?, " +
            "expiration_date = ? " +
            "WHERE ID = ?";

    private static String INSERT_NEW_PRODUCT = "INSERT INTO PRODUCTS(name, producer, price, expiration_date)" +
            " values (?, ?, ?, ?) ";


    private static String FIND_PRODUCT_BY_ID = "SELECT id, name, producer, price, expiration_date, creation_time from products where id = ?";
    private static String FIND_ALL_PRODUCTS = "SELECT id, name, producer, price, expiration_date, creation_time from products";

    private DataSource dataSource;

    public ProductDaoImpl(DataSource dataSource) {
        this.dataSource = dataSource;
    }

    @Override
    public void save(Product product) {

        Objects.requireNonNull(product);

        try (Connection connection = dataSource.getConnection()) {
            PreparedStatement preparedStatement = connection.prepareStatement(INSERT_NEW_PRODUCT, PreparedStatement.RETURN_GENERATED_KEYS);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getProducer());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setDate(4, Date.valueOf(product.getExpirationDate()));

            int updatedRows = preparedStatement.executeUpdate();

            if (updatedRows >= 1) {
                ResultSet resultSet = preparedStatement.getGeneratedKeys();
                resultSet.next();
                Long id = resultSet.getLong(1);
                product.setId(id);
            }
            else {
                throw new DaoOperationException("Cannot obtain generated ID from the database.");
            }

            connection.commit();
        }
        catch(SQLException sqlException) {
            throw new DaoOperationException(String.format("Error saving product: %s", product), sqlException);
        }
    }

    @Override
    public List<Product> findAll() {

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(FIND_ALL_PRODUCTS);
            ResultSet resultSet = preparedStatement.executeQuery();

            List<Product> products = new ArrayList<>();
            while (resultSet.next()) {
                products.add(parseResultSetRow(resultSet));
            }

            return products;
        }
        catch(SQLException sqlException) {
            throw new DaoOperationException("Error while finding all products", sqlException);
        }
    }

    @SneakyThrows
    @Override
    public Product findOne(Long productId) {
        validateProductId(productId);

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(FIND_PRODUCT_BY_ID);
            preparedStatement.setLong(1, productId);

            ResultSet resultSet = preparedStatement.executeQuery();

            if (!resultSet.next()) {
                throw new DaoOperationException(String.format("Product with id = %s does not exist", productId));
            }

            return parseResultSetRow(resultSet);

        }
        catch(SQLException sqlException) {
            throw new DaoOperationException("Error while finding product by ID ", sqlException);
        }

    }

    private Product parseResultSetRow(ResultSet resultSet) throws SQLException {
        Product product = new Product();
        product.setId(resultSet.getLong("id"));
        product.setName(resultSet.getString("name"));
        product.setProducer(resultSet.getString("producer"));
        product.setPrice(resultSet.getBigDecimal("price"));
        product.setExpirationDate(resultSet.getDate("expiration_date").toLocalDate());
        product.setCreationTime(resultSet.getTimestamp("creation_time").toLocalDateTime());
        return product;
    }

    @Override
    public void update(Product product) {

        validate(product);

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(UPDATE_PRODUCT_BY_ID_QUERY);
            preparedStatement.setString(1, product.getName());
            preparedStatement.setString(2, product.getProducer());
            preparedStatement.setBigDecimal(3, product.getPrice());
            preparedStatement.setDate(4, Date.valueOf(product.getExpirationDate()));
            preparedStatement.setLong(5, product.getId());

            preparedStatement.execute();
            connection.commit();

        }
        catch(SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }

    }

    @Override
    public void remove(Product product) {

        validate(product);

        try (Connection connection = dataSource.getConnection()) {

            PreparedStatement preparedStatement = connection.prepareStatement(DELETE_FROM_PRODUCTS_BY_ID);
            preparedStatement.setLong(1, product.getId());

            preparedStatement.execute();
            connection.commit();

        }
        catch(SQLException sqlException) {
            throw new RuntimeException(sqlException);
        }
    }

    private void validate(Product product) {
        Objects.requireNonNull(product);

        validateProductId(product.getId());
    }

    private void validateProductId(Long productId) {
        if (Objects.isNull(productId)) {
            throw new DaoOperationException("Product id cannot be null");
        }
        else if (productId < 1L) {
            throw new DaoOperationException(String.format("Product with id = %s does not exist", productId));
        }
    }

}
