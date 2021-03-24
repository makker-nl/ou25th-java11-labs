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

import static java.lang.System.out;
import java.math.BigDecimal;
import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 *
 * @author redhat
 */
public class ProductManager {

    public static final int ARRAY_INCREMENT = 5;
    public static final String LAN_TAG_US = Locale.US.toLanguageTag();
    public static final String LAN_TAG_UK = Locale.UK.toLanguageTag();
    public static final String LAN_TAG_ESUS = "es-US";
    public static final String LAN_TAG_FR = Locale.FRANCE.toLanguageTag();
    ;
    public static final String LAN_TAG_FRCA = "fr-CA";
    public static final String LAN_TAG_RU = "ru-RU";
    public static final String LAN_TAG_CN = Locale.CHINA.toLanguageTag();
    ;
    public static final String LAN_TAG_NL = "nl-NL";
//    private Product product;
//    private Review[] reviews = new Review[ARRAY_INCREMENT];
    private Map<Product, List<Review>> products = new HashMap<>();
    private ResourceFormatter formatter;
    private static Map<String, ResourceFormatter> formatters = Map.of(
            LAN_TAG_UK, new ResourceFormatter(Locale.UK),
            LAN_TAG_US, new ResourceFormatter(Locale.US),
            LAN_TAG_ESUS, new ResourceFormatter(new Locale(LAN_TAG_ESUS)),
            LAN_TAG_FR, new ResourceFormatter(Locale.FRANCE),
            LAN_TAG_FRCA, new ResourceFormatter(new Locale("fr", "CA")),
            LAN_TAG_RU, new ResourceFormatter(new Locale("ru", "RU")),
            LAN_TAG_CN, new ResourceFormatter(Locale.CHINA),
            LAN_TAG_NL, new ResourceFormatter(new Locale("nl", "NL"))
    );

    public ProductManager(Locale locale) {
        this(locale.toLanguageTag());
    }

    public ProductManager(String languageTag) {
        changeLocale(languageTag);
    }

    public void changeLocale(String languageTag) {
        formatter
                = formatters.getOrDefault(languageTag, formatters.get(LAN_TAG_UK));
    }

    public static Set<String> getSupportedLocales() {
        return formatters.keySet();
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating, LocalDate bestBefore) {
        Product product = new Food(id, name, price, rating, bestBefore);
        products.putIfAbsent(product, new ArrayList<Review>());
        return product;
    }

    public Product createProduct(int id, String name, BigDecimal price, Rating rating) {
        Product product = new Drink(id, name, price, rating);
        products.putIfAbsent(product, new ArrayList<Review>());
        return product;
    }

    public Product reviewProduct(Product product, Rating rating, String comments) {
        List<Review> reviews = products.get(product);
        products.remove(product, reviews);
        reviews.add(new Review(rating, comments));
        int sum = 0;

        for (Review review : reviews) {
            sum += review.getRating().ordinal();
        }
        product = product.applyRating(Rateable.convert(Math.round((float) sum / reviews.size())));
        products.put(product, reviews);
        return product;
    }

    public Product reviewProduct(int id, Rating rating, String comments) {
        Product product = findProduct(id);
        return reviewProduct(product, rating, comments);
    }

    public void printProductReport(Product product) {
        List<Review> reviews = products.get(product);
        Collections.sort(reviews);
        StringBuilder txt = new StringBuilder();
        txt.append(formatter.formatProduct(product));
        txt.append('\n');
        for (Review review : reviews) {
            if (review == null) {
                break;
            }
            txt.append(formatter.formatReview(review)
            );
            txt.append('\n');
        }
        if (reviews.isEmpty()) {
            txt.append(formatter.getText("no.reviews"));
            txt.append('\n');
        }
        out.println(txt);
    }

    public void printProducts(Comparator<Product> sorter) {
        List<Product> productList = new ArrayList<>(products.keySet());
        productList.sort(sorter);
        StringBuilder txt = new StringBuilder();
        for (Product product : productList) {
            txt.append(formatter.formatProduct(product));
            txt.append('\n');
        }
        out.println(txt);
    }

    public void printProductReport(int id) {
        Product product = findProduct(id);
        printProductReport(product);
    }

    public Product findProduct(int id) {
        Product result = null;
        for (Product product : products.keySet()) {
            if (product.getId() == id) {
                result = product;
                break;
            }
        }
        return result;
    }

    private static class ResourceFormatter {

        private Locale locale;
        private ResourceBundle resources;
        private DateTimeFormatter dateFormat;
        private NumberFormat moneyFormat;

        private ResourceFormatter(Locale locale) {
            this.locale = locale;
            resources = ResourceBundle.getBundle("labs.pm.data.resources", locale);
            dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
            moneyFormat = NumberFormat.getCurrencyInstance(locale);
        }

        private String formatProduct(Product product) {
            return MessageFormat.format(getText("product"),
                    product.getName(),
                    moneyFormat.format(product.getPrice()),
                    product.getRating().getStars(),
                    dateFormat.format(product.getBestBefore()));
        }

        private String formatReview(Review review) {
            return MessageFormat.format(getText("review"),
                    review.getRating().getStars(),
                    review.getComments());
        }

        private String getText(String key) {
            return resources.getString(key);
        }
    }
}
