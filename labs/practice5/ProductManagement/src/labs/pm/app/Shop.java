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
import labs.pm.data.Product;
import static java.lang.System.out;
import static labs.pm.data.Rating.*;

/**
 *
 * {@code Shop} class represents an application that manages Products
 *
 * @version 4.0
 * @author redhat
 */
public class Shop {

    public static void plProduct(Product p) {
        out.println(p.getId() + " " + p.getName() + " " + p.getPrice() + " " + p.getDiscount()
                + " " + p.getRating().getStars());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Product p1 = new Product(101, "Tea", BigDecimal.valueOf(1.99));
        plProduct(p1);
        Product p2 = new Product(102, "Coffee", BigDecimal.valueOf(1.99), FOUR_STAR);
        plProduct(p2);
        Product p3 = new Product(103, "Cake", BigDecimal.valueOf(3.99), FIVE_STAR);
        plProduct(p3);
        Product p4 = new Product();
        plProduct(p4);
        Product p5 = p3.applyRating(THREE_STAR);
        plProduct(p5);
    }

}
