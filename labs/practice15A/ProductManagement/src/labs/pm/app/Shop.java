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

import java.util.List;
import java.util.Set;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

    private final static int MIN_PRODUCT_ID = 101;
    private final static int NUM_PRODUCTS = 5;
    private final static int NUM_CLIENTS = 5;
    private final static int MAX_THREADS = 3;
    private final static Logger logger = Logger.getLogger(Shop.class.getName());

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        AtomicInteger clientCount = new AtomicInteger();
        ProductManager pm = ProductManager.getInstance();//new ProductManager(); 
        Callable<String> client = () -> {
            String clientId = "Client " + clientCount.incrementAndGet();
            String threadName = Thread.currentThread().getName();
            // StudentGuide mentions 64 possible product id's. Our folder has only 6.
            int productId = ThreadLocalRandom.current().nextInt(NUM_PRODUCTS) + MIN_PRODUCT_ID;
            Set<String> supportedLocales = ProductManager.getSupportedLocales();
            String languageTag = supportedLocales.stream().skip(ThreadLocalRandom.current().nextInt(supportedLocales.size())).findFirst().get();
            StringBuilder log = new StringBuilder();
            log.append(clientId).append(" ").append(threadName).append("\n-\tstart of log\t-\n");
            log.append(
                    pm.getDiscounts(languageTag)
                            .entrySet()
                            .stream()
                            .map(entry -> entry.getKey() + "\t" + entry.getValue())
                            .collect(Collectors.joining("\n")));
            Product product = pm.reviewProduct(productId, Rating.FOUR_STAR, "Yet another review from " + clientId);
            log.append((product != null) ? "\nProduct " + productId + " reviewed\n" : "\nProduct " + productId + " not reviewed\n");
            pm.printProductReport(productId, languageTag, clientId);
            log.append(clientId).append(" generated report for ").append(productId).append(" product");
            log.append("\n-\tend of log\t-\n");
            return log.toString();
        };
        List<Callable<String>> clients = Stream.generate(() -> client).limit(NUM_CLIENTS).collect(Collectors.toList());
        ExecutorService executorService = Executors.newFixedThreadPool(MAX_THREADS);
        try {
            List<Future<String>> results = executorService.invokeAll(clients);
            executorService.shutdown();
            results.stream().forEach(result -> { try {
                System.out.println(result.get());
                } catch (InterruptedException | ExecutionException ex) {
                    logger.log(Level.SEVERE, "Error retrieving client log", ex);
                }  
            });
        } catch (InterruptedException ex) {
            logger.log(Level.SEVERE, "Error invoking clients", ex);
        }
    }

}
