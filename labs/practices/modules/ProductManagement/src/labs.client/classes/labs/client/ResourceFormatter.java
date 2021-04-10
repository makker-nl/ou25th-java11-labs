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

import java.text.MessageFormat;
import java.text.NumberFormat;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import labs.pm.data.Product;
import labs.pm.data.Review;

/**
 *
 * @author redhat
 */
public class ResourceFormatter {

    public static final String LAN_TAG_US = Locale.US.toLanguageTag();
    public static final String LAN_TAG_UK = Locale.UK.toLanguageTag();
    public static final String LAN_TAG_ESUS = "es-US";
    public static final String LAN_TAG_FR = Locale.FRANCE.toLanguageTag();
    public static final String LAN_TAG_FRCA = "fr-CA";
    public static final String LAN_TAG_RU = "ru-RU";
    public static final String LAN_TAG_CN = Locale.CHINA.toLanguageTag();
    public static final String LAN_TAG_NL = "nl-NL";

    private static final String RES_BUNDLE_LOC = "labs.client.resources";
    private final Locale locale;
    private final ResourceBundle resources;
    private final DateTimeFormatter dateFormat;
    private final NumberFormat moneyFormat;

    private static final Map<String, ResourceFormatter> formatters = Map.of(
            LAN_TAG_UK, new ResourceFormatter(Locale.UK),
            LAN_TAG_US, new ResourceFormatter(Locale.US),
            LAN_TAG_ESUS, new ResourceFormatter(new Locale(LAN_TAG_ESUS)),
            LAN_TAG_FR, new ResourceFormatter(Locale.FRANCE),
            LAN_TAG_FRCA, new ResourceFormatter(new Locale("fr", "CA")),
            LAN_TAG_RU, new ResourceFormatter(new Locale("ru", "RU")),
            LAN_TAG_CN, new ResourceFormatter(Locale.CHINA),
            LAN_TAG_NL, new ResourceFormatter(new Locale("nl", "NL"))
    );

    public static ResourceFormatter getResourceFormatter(String languageTag) {
        return formatters.getOrDefault(languageTag, formatters.get(LAN_TAG_UK));
    }

    private ResourceFormatter(Locale locale) {
        this.locale = locale;
        resources = ResourceBundle.getBundle(RES_BUNDLE_LOC, locale);
        dateFormat = DateTimeFormatter.ofLocalizedDate(FormatStyle.SHORT).localizedBy(locale);
        moneyFormat = NumberFormat.getCurrencyInstance(locale);
    }

    public String formatProduct(Product product) {
        return MessageFormat.format(getText("product"),
                product.getName(),
                moneyFormat.format(product.getPrice()),
                product.getRating().getStars(),
                dateFormat.format(product.getBestBefore()));
    }

    public String formatProductReport(Product product, List<Review> reviews) {
        Collections.sort(reviews);
        StringBuilder out = new StringBuilder();
        out.append(formatProduct(product)).append(System.lineSeparator());
        if (reviews.isEmpty()) {
            out.append(getText("no.reviews"));
        } else {
            out.append(reviews.stream()
                    .map(review -> formatReview(review) + System.lineSeparator())
                    .collect(Collectors.joining()));
        }
        return out.toString();
    }

    public String formatData(String key, int productId) {
        return MessageFormat.format(getText(key), productId);
    }

    public String formatReview(Review review) {
        return MessageFormat.format(getText("review"),
                review.getRating().getStars(),
                review.getComments());
    }

    private String getText(String key) {
        return resources.getString(key);
    }

}
