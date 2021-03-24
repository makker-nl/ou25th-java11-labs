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
import java.util.Comparator;
import java.util.function.Predicate;
import labs.pm.data.Product;
import labs.pm.data.ProductManager;
import labs.pm.data.Rating;

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

        pm.printProductReport(101);
        pm.printProductReport(103);
        int pId = 164;
        pm.createProduct(pId, "Kombucha", BigDecimal.valueOf(1.99), Rating.NOT_RATED);
        pm.reviewProduct(pId, Rating.TWO_STAR, "Looks like tea but is it?");
        pm.reviewProduct(pId, Rating.FOUR_STAR, "Fine tea");
        pm.reviewProduct(pId, Rating.FOUR_STAR, "This is not tea");
        pm.reviewProduct(pId, Rating.FIVE_STAR, "Perfect!");
        
//        pm.dumpData();
//        pm.restoreData();        
        
        pm.printProductReport(pId);

        pm.printProductReport(101);
        pm.printProductReport(105);
        
        Comparator<Product> ratingSorter = (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal();
        Comparator<Product> priceSorter = (p1, p2) -> p2.getPrice().compareTo(p1.getPrice());

        Predicate<Product> priceFilter = (p) -> p.getPrice().floatValue() < 2;
        System.out.println("Products with price less than 2: ");
        pm.printProducts(priceFilter, ratingSorter);
        //pm.printProducts(p -> p.getPrice().floatValue() < 2, (p1, p2) -> p2.getRating().ordinal() - p1.getRating().ordinal());
        System.out.println("Discounts: ");
        pm.getDiscounts().forEach((rating, discount) -> System.out.println(rating + "\t" + discount));
    }

}
