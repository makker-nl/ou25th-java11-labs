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
package labs.pm.app;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.Map;
import java.util.function.Predicate;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import static labs.pm.data.Rating.*;

/**
 *
 * {@code Shop} class represents an application that manages Products
 *
 * @version 4.0
 * @author redhat
 */
public class Shop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
//        ProductManager pm = new ProductManager(Locale.US);
//        ProductManager pm = new ProductManager(Locale.UK);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_NL);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_RU);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_US);
        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_UK);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_FR);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_FRCA);
//        ProductManager pm = new ProductManager(ProductManager.LAN_TAG_CN);

        int p1Id = 101;
        pm.createProduct(p1Id, "Tea", BigDecimal.valueOf(1.99), NOT_RATED);
//        pm.printProductReport(p1Id);
        pm.reviewProduct(p1Id, FOUR_STAR, "Nice hot cup of tea");
        pm.reviewProduct(p1Id, TWO_STAR, "Rather weak tea");
        pm.reviewProduct(p1Id, FOUR_STAR, "Fine tea");
        pm.reviewProduct(p1Id, FOUR_STAR, "Good tea");
        pm.reviewProduct(p1Id, FIVE_STAR, "Perfect tea");
        pm.reviewProduct(p1Id, THREE_STAR, "Just add some lemon");
//        pm.printProductReport(p1Id);

        int p2Id = 102;
        pm.createProduct(p2Id, "Coffee", BigDecimal.valueOf(1.99), NOT_RATED);
        pm.reviewProduct(p2Id, THREE_STAR, "Coffee was ok");
        pm.reviewProduct(p2Id, ONE_STAR, "Where is the milk?!");
        pm.reviewProduct(p2Id, FIVE_STAR, "It's perfect with ten spoons of sugar!");
//        pm.printProductReport(p2Id);

        int p3Id = 103;
        pm.createProduct(p3Id, "Cake", BigDecimal.valueOf(3.99), NOT_RATED, LocalDate.now().plusDays(2));
        pm.reviewProduct(p3Id, FIVE_STAR, "Very nice cake");
        pm.reviewProduct(p3Id, FOUR_STAR, "It's good, but I've expected more chocolate");
        pm.reviewProduct(p3Id, FIVE_STAR, "This cake is perfect!");
//        pm.printProductReport(p3Id);

        int p4Id = 104;
        pm.createProduct(p4Id, "Cookie", BigDecimal.valueOf(2.99), NOT_RATED, LocalDate.now());
        pm.reviewProduct(p4Id, THREE_STAR, "Just another cookie");
        pm.reviewProduct(p4Id, THREE_STAR, "Ok");
//        pm.printProductReport(p4Id);

        int p5Id = 105;
        pm.createProduct(p5Id, "Hot Chocolate", BigDecimal.valueOf(2.50), NOT_RATED);
        pm.reviewProduct(p5Id, FOUR_STAR, "Tasty");
        pm.reviewProduct(p5Id, FOUR_STAR, "Not bast at all");
//        pm.printProductReport(p5Id);

        int p6Id = 106;
        pm.createProduct(p6Id, "Chocolate", BigDecimal.valueOf(2.50), NOT_RATED, LocalDate.now().plusDays(3));
        pm.reviewProduct(p6Id, TWO_STAR, "Too seet");
        pm.reviewProduct(p6Id, THREE_STAR, "Better than cookie");
        pm.reviewProduct(p6Id, TWO_STAR, "Too bitter");
        pm.reviewProduct(p6Id, ONE_STAR, "I don't get it!");
        pm.printProductReport(p6Id);
//        pm.printProducts((p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
//        pm.printProducts((p1, p2) -> p1.getRating().ordinal() - p2.getRating().ordinal());
        Comparator<Product> ratingSorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
        Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());
//        pm.printProducts(ratingSorter);
//        pm.printProducts((p1, p2) -> p2.getPrice().compareTo(p1.getPrice()));
//        pm.printProducts(priceSorter);
//        pm.printProducts(ratingSorter.thenComparing(priceSorter));
//        pm.printProducts(ratingSorter.thenComparing(priceSorter).reversed());
        Predicate<Product> priceFilter = (p) -> p.getPrice().floatValue() < 2;
        pm.printProducts(priceFilter, ratingSorter);
        pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + "\t" + discount));
    }

}
