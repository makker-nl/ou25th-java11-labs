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

/**
 *
 * {@code Shop} class represents an application that manages Products
 * @version 4.0
 * @author redhat
 */
public class Shop {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        Product p1 = new Product();
        p1.setId(101);
        p1.setName("Tea");
        p1.setPrice(BigDecimal.valueOf(1.99));
        out.println(p1.getId() + " " + p1.getName() + " " + p1.getPrice() + " " +p1.getDiscount());
    }

}
