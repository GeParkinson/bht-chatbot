package de.bht.beuthbot.canteen;

import de.bht.beuthbot.canteen.model.*;
import net.jodah.expiringmap.ExpiringMap;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDate;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * This class parses the current webpage of the beuth canteen.
 * Created by sJantzen on 03.07.2017.
 */
public class Parser {

    private Logger logger = LoggerFactory.getLogger(Parser.class);

    private static final Pattern DATE_PATTERN = Pattern.compile(".*([0-9]{4}-[0-9]{2}-[0-9]{2}).*");
    private static final Pattern ICON_PATTERN = Pattern.compile(".*/([0-9]{1,2})\\.png");

    private static final String CANTEENS_URL = "https://www.stw.berlin/xhr/speiseplan-und-standortdaten.html";
    private static final String DAYS_DISHES_URL = "https://www.stw.berlin/xhr/speiseplan-wochentag.html";
    private static final String PARAM_RESOURCE_ID_NAME = "resources_id";
    private static final String PARAM_RESOURCE_ID_VALUE = "527";
    private static final String PARAM_DATE_NAME = "date";

    private static final String OPENING_HOURS = "Mo. - Fr. | 09:00 - 14:30";

    private static final String CURRENT_DATA_KEY = "KEY";
    private static Map<String, CanteenData> currentCanteenData;

    static {
        currentCanteenData = ExpiringMap.builder().expiration(1, TimeUnit.DAYS)
                .build();
    }

    /**
     * Creates new CanteenData with all dishes of the current and next week by parsing
     * the canteen url of the beuth university.
     * @return
     */
    public CanteenData parse() {

        if(currentCanteenData.containsKey(CURRENT_DATA_KEY)){
            logger.debug("Canteen Data already exists in Map.");
            return currentCanteenData.get(CURRENT_DATA_KEY);
        }

        String canteenName = "";

        List<Dish> dishes = new ArrayList<>();

        try {
            Document doc = Jsoup.connect(CANTEENS_URL).data(PARAM_RESOURCE_ID_NAME, PARAM_RESOURCE_ID_VALUE).userAgent("Mozilla").post();

            Elements canteenNameEle = doc.select("#listboxEinrichtungen option[value=" + PARAM_RESOURCE_ID_VALUE + "]");
            if(canteenNameEle != null && canteenNameEle.size() > 0) {
                canteenName = canteenNameEle.first().text();
            }

            /*
             * This is a list with dates of the current and next week.
             * First we get all dates and than we will iterate over these dates,
             * to get all dishes of the current and next week.
             */
            List<String> dates = new ArrayList<>();

            // Get all dates of the current week.
            dates.addAll(getDates(doc));

            /*
             * Loop all days.
             */
            Map<String, String> params = new HashMap<>();
            params.put(PARAM_RESOURCE_ID_NAME, PARAM_RESOURCE_ID_VALUE);
            for (String day : dates) {

                params.put(PARAM_DATE_NAME, day);

                try {
                    doc = Jsoup.connect(DAYS_DISHES_URL).data(params).userAgent("Mozilla").post();
                    Thread.sleep(1000);
                } catch (Exception e) {
                    logger.debug("Exception while connecting to site: " + DAYS_DISHES_URL, e);
                    continue;
                }

                /*
                 * Loop all dish categories of the current page.
                 */
                Elements dishCategories = doc.select("div.splGroupWrapper");

                for (Element dishCategory : dishCategories) {

                    /*
                     * Get name of the current dishCategory.
                     */
                    Elements strCategories = dishCategory.select("div.splGroup");
                    DishCategory category = null;
                    if(strCategories != null && strCategories.size() > 0 ) {
                        String strCategory = dishCategory.select("div.splGroup").first().text();
                        category = DishCategory.getDishCategory(strCategory);
                    }

                    /*
                     * Loop all dishes of the current category.
                     */
                    Elements categoryDishes = dishCategory.select("div.splMeal");

                    for (Element categoryDish : categoryDishes) {

                        Dish dish = new Dish();

                        dish.setDate(LocalDate.parse(day));
                        dish.setDishCategory(category);

                        /*
                         * Get markings for dish.
                         */
                        if(categoryDish.attr("lang") != null) {
                            dish.getMarkings().addAll(Arrays.asList(categoryDish.attr("lang").trim().split(", ")));
                        }

                        /*
                         * Get price string.
                         */
                        Element strPrice = categoryDish.select("div.text-right").first();
                        dish.setPrice(strPrice.text());

                        // check if 3 different prices are available or just one price for all
                        /*
                        if(!"".equals(dish.getPrice())) {
                            if (dish.getPrice().contains("/")) {
                                // 3 different prices
                                String[] prices = dish.getPrice().replace("€", "").replace("\\?", "").replace(",",".").trim().split("/");
                                dish.setPriceStudent(new BigDecimal(prices[0]));
                                dish.setPriceEmployee(new BigDecimal(prices[1]));
                                dish.setPriceGuest(new BigDecimal(prices[2]));
                            }
                            else {
                                // only 1 price
                                logger.info("1: " + dish.getPrice());
                                logger.info("1: " + dish.getPrice().replace("€", ""));
                                logger.info("1: " + dish.getPrice().replace("€", "").replace("\\?", ""));
                                logger.info("1: " + dish.getPrice().replace("€", "").replace("\\?", "").replace(",","."));
                                logger.info("1: " + dish.getPrice().replace("€", "").replace("\\?", "").replace(",",".").trim());
                                BigDecimal price = new BigDecimal(dish.getPrice().replace("€", "").replace("\\?", "").replace(",",".").trim());
                                dish.setPriceStudent(price);
                                dish.setPriceEmployee(price);
                                dish.setPriceGuest(price);
                            }
                        }
                        */

                        /*
                         * Get name of dish.
                         */
                        Element name = categoryDish.select("span.bold").first();
                        dish.setName(name.text());

                        /*
                         * Loop icons for traffic lights and dish types.
                         */
                        Elements icons = categoryDish.select("img.splIcon");

                        for (Element icon : icons) {

                            /*
                             * Check if and set traffic light.
                             */
                            if(icon.attr("src").contains("ampel")) {
                                if(icon.attr("src").contains("ampel_gelb")) {
                                    dish.setTrafficLight(TrafficLight.YELLOW);
                                } else if (icon.attr("src").contains("ampel_gruen")){
                                    dish.setTrafficLight(TrafficLight.GREEN);
                                } else if (icon.attr("src").contains("ampel_rot")) {
                                    dish.setTrafficLight(TrafficLight.RED);
                                }
                            } else {
                                /*
                                 * Check for dishTypes and set them.
                                 */
                                Matcher m = ICON_PATTERN.matcher(icon.attr("src"));
                                while ( m.find() ) {
                                    if(DishType.getDishTypeByNumber(m.group(1)) != null) {
                                        dish.getDishTypes().add(DishType.getDishTypeByNumber(m.group(1)));
                                    }
                                }
                            }
                        }
                        dishes.add(dish);
                    }
                }
            }
        } catch (IOException e) {
            logger.info("Got Error while parsing site: " + CANTEENS_URL, e);
        }

        currentCanteenData.put(CURRENT_DATA_KEY, new CanteenData(canteenName, OPENING_HOURS, "", dishes));

        return currentCanteenData.get(CURRENT_DATA_KEY);
    }

    /**
     * This method returns a string list of dates for all possible days with dishes
     * for the current shown week.
     * @param doc
     * @return
     */
    private static List<String> getDates(Document doc) {
        List<String> dates = new ArrayList<>();
        Elements canteenLinks = doc.select("li[id^=spltag]");
        for(Element canteenLink : canteenLinks) {
            Matcher m = DATE_PATTERN.matcher(canteenLink.attr("onClick"));
            while ( m.find() ) {
                dates.add(m.group(1));
                // Get date for the same day of the next week.
                LocalDate ld = LocalDate.parse(m.group(1));
                ld = ld.plusDays(7);
                dates.add(ld.toString());
            }
        }
        return dates;
    }

}
