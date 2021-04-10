/*
 * Copyright (C) 2021 redhat
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package labs.file.service;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.MessageFormat;
import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.Predicate;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import labs.pm.data.Drink;
import labs.pm.data.Food;
import labs.pm.data.Product;
import labs.pm.data.Rateable;
import labs.pm.data.Rating;
import labs.pm.data.Review;
import labs.pm.service.ProductManager;
import labs.pm.service.ProductManagerException;

/**
 *
 * @author redhat
 */
public class ProductFileManager implements ProductManager {

    public static final int ARRAY_INCREMENT = 5;

    private Map<Product, List<Review>> products = new HashMap<>();
    private final ReentrantReadWriteLock lock = new ReentrantReadWriteLock();
    private final Lock writeLock = lock.writeLock();
    private final Lock readLock = lock.readLock();

    private final ResourceBundle config = ResourceBundle.getBundle("labs.file.service.config");
    private final MessageFormat reviewFormat = new MessageFormat(config.getString("review.data.format"));
    private final MessageFormat productFormat = new MessageFormat(config.getString("product.data.format"));
    private final Path reportsFolder = Path.of(config.getString("reports.folder"));
    private final Path dataFolder = Path.of(config.getString("data.folder"));
    private final Path tempFolder = Path.of(config.getString("temp.folder"));

    private static final Logger logger = Logger.getLogger(ProductManager.class.getName());

    public ProductFileManager() {
        loadAllData();
    }

    @Override
    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        return putProduct(product);
    }

    @Override
    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        return putProduct(product);
    }

    @Override
    public Product reviewProduct(int id, Rating rating, String comments) {
        Product product = null;
        try {
            writeLock.lock();
            product = findProduct(id);
            product = reviewProduct(product, rating, comments);
        } catch (ProductManagerException ex) {
            logger.log(Level.INFO, ex.getMessage());
            return null;
        } finally {
            writeLock.unlock();
        }
        return product;
    }

    private Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));
        product = product.applyRating(
                Rateable.convert(
                        (int) Math.round(
                                reviews.stream()
                                        .mapToInt(r -> r.getRating().ordinal())
                                        .average()
                                        .orElse(0))));
        products.put(product, reviews);
        return product;
    }

    @Override
    public Product findProduct(int id) throws ProductManagerException {
        try {
            readLock.lock();
            return products.keySet()
                    .stream()
                    .filter(p -> p.getId() == id)
                    .findFirst()
                    .orElseThrow(() -> new ProductManagerException("Product with id " + id + " not found"));//.get();//.orElseGet(() -> null);
        } finally {
            readLock.unlock();
        }
    }

    @Override
    public List<Product> findProducts(Predicate<Product> filter) throws ProductManagerException {
        return products.keySet()
                .stream()
                .filter(filter)
                .collect(Collectors.toList());
    }

    @Override
    public List<Review> findReviews(int id) throws ProductManagerException {
        return products.get(findProduct(id));
    }

    @Override
    public Map<Rating, BigDecimal> getDiscounts() throws ProductManagerException {
        try {
            readLock.lock();
            return products.keySet()
                    .stream()
                    .collect(
                            Collectors.groupingBy(
                                    product -> product.getRating(),
                                    Collectors.collectingAndThen(
                                            Collectors.summingDouble(product -> product.getDiscount().doubleValue()),
                                            discount -> BigDecimal.valueOf(discount)
                                    )
                            )
                    );
        } finally {
            readLock.unlock();
        }
    }

    private Product putProduct(Product product) {
        try {
            writeLock.lock();
            products.putIfAbsent(product, new ArrayList<>());
        } catch (Exception ex) {
            logger.log(Level.INFO, "Error adding product " + ex.getMessage());
            // Discard the product on exception when adding product.
            return null;
        } finally {
            writeLock.unlock();
        }
        return product;
    }

    private void dumpData() {
        try {
            // You could also use Files.createDirectories, that also creates all parent-folders that does not exist yet.
            // Then the check on existence would not be necessary.
            if (Files.notExists(tempFolder)) {
                Files.createDirectory(tempFolder);
            }
            Path tempFile = tempFolder.resolve(MessageFormat.format(config.getString("temp.file"), Instant.now()));
            try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(tempFile, StandardOpenOption.CREATE))) {
                System.out.println("Dump products to " + tempFile.getFileName());
                out.writeObject(products);
//                products = new HashMap<>();
            }

        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error dumping data " + ex.getMessage(), ex);

        }
    }

    @SuppressWarnings("unchecked")
    private void restoreData() {
        try {
            Path tempFile = Files.list(tempFolder)
                    .filter(path -> path.getFileName().toString().endsWith(".tmp")).findFirst().orElseThrow();
            try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(tempFile, StandardOpenOption.DELETE_ON_CLOSE))) {
                System.out.println("Read projects from " + tempFile.getFileName());
                products = (HashMap) in.readObject();
            }
        } catch (Exception ex) {
            logger.log(Level.SEVERE, "Error reading data " + ex.getMessage(), ex);

        }

    }

    private void loadAllData() {
        try {
            products = Files.list(dataFolder)
                    .filter(file -> file.getFileName().toString().startsWith("product"))
                    .map(file -> loadProduct(file))
                    .filter(product -> product != null)
                    .collect(Collectors.toMap(product -> product,
                            product -> loadReviews(product))
                    );
        } catch (IOException ex) {
            logger.log(Level.SEVERE, "Error loading data " + ex.getMessage(), ex);

        }
    }

    private Product loadProduct(Path file) {
        Product product = null;
        if (Files.exists(file)) {
            try {
                //  Alternative for StandardCharsets.UTF_8: Charset.forName("UTF-8"). However, again this relies on a hardcoded string.
                product = parseProduct(
                        Files.lines(dataFolder.resolve(file), StandardCharsets.UTF_8).findFirst().orElseThrow());
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Error loading product " + ex.getMessage());
            }

        }
        return product;
    }

    private List<Review> loadReviews(Product product) {
        List<Review> reviews = null;
        Path file = dataFolder.resolve(MessageFormat.format(config.getString("reviews.data.file"), product.getId()));
        if (Files.notExists(file)) {
            reviews = new ArrayList<>();
        } else {
            try {
                //  Alternative for StandardCharsets.UTF_8: Charset.forName("UTF-8"). However, again this relies on a hardcoded string.
                reviews = Files.lines(file, StandardCharsets.UTF_8)
                        .map(line -> parseReview(line))
                        .filter(review -> review != null)
                        .collect(Collectors.toList());
            } catch (IOException ex) {
                logger.log(Level.WARNING, "Error loading reviews " + ex.getMessage());
            }

        }
        return reviews;

    }

    private Review parseReview(String text) {
        Review review = null;
        try {
            Object[] values = reviewFormat.parse(text);
            review = new Review(Rateable.convert(Integer.parseInt((String) values[0])),
                    (String) values[1]);
        } catch (ParseException | NumberFormatException ex) {
            logger.log(Level.WARNING, "Error parsing review " + text + " " + ex.getMessage());
        }
        return review;
    }

    private Product parseProduct(String text) {
        Product product = null;
        try {
            Object[] values = productFormat.parse(text);
            String type = (String) values[0];
            int id = Integer.parseInt((String) values[1]);
            String name = (String) values[2];
            BigDecimal price = BigDecimal.valueOf(Double.parseDouble((String) values[3]));
            Rating rating = Rateable.convert(Integer.parseInt((String) values[4]));
            switch (type) {
                case "D":
                    product = new Drink(id, name, price, rating);
                    break;
                case "F":
                    LocalDate bestBefore = LocalDate.parse((String) values[5]);
                    product = new Food(id, name, price, rating, bestBefore);
            }
        } catch (ParseException | NumberFormatException | DateTimeParseException ex) {
            logger.log(Level.WARNING, "Error parsing product " + text + " " + ex.getMessage());
        }
        return product;
    }

}
