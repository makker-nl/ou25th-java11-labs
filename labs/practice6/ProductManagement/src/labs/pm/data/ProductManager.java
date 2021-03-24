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
package labs.pm.data;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 *
 * @author redhat
 */
public class ProductManager {

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        return new Food(id, name, price, rating, bestBefore);
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        return new Drink(id, name, price, rating);
    }

}
