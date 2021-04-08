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
package labs.client;

import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import static labs.client.ResourceFormatter.LAN_TAG_UK;
import labs.file.service.ProductFileManager;
import labs.pm.data.Product;
import labs.pm.data.Rating;
import labs.pm.data.Review;
import labs.pm.service.ProductManager;
import labs.pm.service.ProductManagerException;

/**
 *
 * {@code Shop} class represents an application that manages Products
 *
 * @version 4.0
 * @author redhat
 */
public class Shop {

    private static Logger logger = Logger.getLogger(Shop.class.getName());

    private static void pl(String text) {
        System.out.println(text);
    }

    public static void main(String[] args) {
        try {
            ResourceFormatter formatter = ResourceFormatter.getResourceFormatter(LAN_TAG_UK);
            ProductManager pm = new ProductFileManager();
            int pId = 164;
            pm.createProduct(pId, "Kombucha", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
            pm.reviewProduct(pId, Rating.TWO_STAR, "Looks like tea but is it?");
            pm.reviewProduct(pId, Rating.FOUR_STAR, "Fine tea");
            pm.reviewProduct(pId, Rating.FOUR_STAR, "This is not tea");
            pm.reviewProduct(pId, Rating.FIVE_STAR, "Perfect!");
            pm.findProducts(p -> p.getPrice().doubleValue() < 2).stream().forEach(product -> pl(formatter.formatProduct(product)));
            pId = 101;
            Product product = pm.findProduct(pId);
            List<Review> reviews = pm.findReviews(pId);
            pl(formatter.formatProduct(product));
            reviews.forEach(review -> pl(formatter.formatReview(review)));
            printFile(formatter.formatProductReport(product, reviews), Path.of(formatter.formatData("report", product.getId())));
        } catch (ProductManagerException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }

    private static void printFile(String content, Path file) {

        try (PrintWriter out = new PrintWriter(new OutputStreamWriter(Files.newOutputStream(file, StandardOpenOption.CREATE), StandardCharsets.UTF_8))) {
            out.append(content);

        } catch (IOException ex) {
            logger.log(Level.WARNING, ex.getMessage(), ex);
        }
    }
}
