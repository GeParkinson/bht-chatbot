package de.bht.beuthbot.canteen;

import de.bht.beuthbot.canteen.model.*;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sJantzen on 22.06.2017.
 * Parser class, to parse a given url, leading to the de.bht.chatbot.canteen page of the Beuth-Hochschule.
 */
public class Parser {

    private static Logger logger = LoggerFactory.getLogger(Parser.class);

    private static final DateTimeFormatter DATEFORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");
    private static final String CANTEEN_CURRENT_WEEK_URL = "http://www.studentenwerk-berlin.de/print/mensen/speiseplan/beuth/woche.html";
    private static final String CANTEEN_NEXT_WEEK_URL = "http://www.studentenwerk-berlin.de/print/mensen/speiseplan/beuth/naechste_woche.html";

    /**
     * Creates new CanteenData with the dishes of the current and the next week
     * by parsing a given de.bht.chatbot.canteen website of Beuth HS.
     * @return
     */
    public CanteenData parse(){

        logger.info("Parser invoked.");

        List<Dish> dishes = new ArrayList<>();

        dishes.addAll(getDishes(CANTEEN_CURRENT_WEEK_URL));     // parses data for the current week
        dishes.addAll(getDishes(CANTEEN_NEXT_WEEK_URL));        // parses data for the next week

        CanteenData data = new CanteenData("Mensa Beuth HS", "Küchen-Öffnungszeiten täglich von 11:00 bis 14:30 Uhr",
                "Nach Küchenschluss, bis 18 Uhr, kann die Mensa zum Lernen und Arbeiten genutzt werden.", dishes);

        return data;
    }

    /**
     * Creates a list of dishes by parsing the given url.
     * @param url
     * @return
     */
    private List<Dish> getDishes(final String url) {
        List<Dish> rv = new ArrayList<>();

        try{
            Document doc = Jsoup.connect(url).get();

            Elements tables = doc.select("table.mensa_week_table");

            for(Element table : tables){
                // Check if correct table
                if(table.getElementsByTag("th").size() > 0 && table.getElementsByTag("th").get(0).text().contains("Speiseart")) {
                    LocalDate[] dates = new LocalDate[5];

                    // Get all dates
                    Elements dateElements = table.select("th.mensa_week_head_col");
                    int index = 0;
                    for(Element dateElement : dateElements) {
                        dates[index++] = LocalDate.parse(dateElement.text().split(", ")[1], DATEFORMATTER);
                    }

                    // loop rows
                    Elements dataRows = table.getElementsByTag("tbody").get(0).getElementsByTag("tr");
                    for(Element row : dataRows) {

                        String rowHeader = row.getElementsByClass("mensa_week_speise_tag_title").get(0).text();

                        // loop all columns of a row (category)
                        for(int i = 0; i < row.getElementsByTag("td").size(); i++) {

                            TrafficLight trafficLight = null;

                            List<DishType> dishTypes = new ArrayList<>();

                            // loop all dishes of a column
                            for(Element element : row.getElementsByTag("td").get(i).getAllElements()) {

                                if (element.tagName().equals("p")) {

                                    Dish dish = new Dish();

                                    // ----------
                                    // get date
                                    dish.setDate(dates[i]);

                                    // ----------
                                    // get dishCategory
                                    dish.setDishCategory(DishCategory.getDishCategory(rowHeader));

                                    // ----------
                                    // get name
                                    dish.setName(element.select("strong").get(0).text() + " " + element.ownText());

                                    // ----------
                                    // get markings
                                    List<String> markings = new ArrayList<>();
                                    for(Element aTag : element.select("a.zusatz")) {
                                        markings.add(aTag.attr("title"));
                                    }
                                    dish.setMarkings(markings);

                                    // ----------
                                    // get prices
                                    String strPrice = element.select("span.mensa_preise").get(0).text();
                                    dish.setPrice(strPrice);

                                    // check if 3 different prices are available or just one price for all
                                    if (strPrice.contains("/")) {
                                        // 3 different prices
                                        String[] prices = strPrice.replace("EUR", "").trim().split(" / ");
                                        dish.setPriceStudent(new BigDecimal(prices[0]));
                                        dish.setPriceEmployee(new BigDecimal(prices[1]));
                                        dish.setPriceGuest(new BigDecimal(prices[2]));
                                    }
                                    else {
                                        // only 1 price
                                        BigDecimal price = new BigDecimal(strPrice.replace("EUR", "").trim());
                                        dish.setPriceStudent(price);
                                        dish.setPriceEmployee(price);
                                        dish.setPriceGuest(price);
                                    }

                                    // ----------
                                    // get dishTypes
                                    dish.setDishTypes(dishTypes);

                                    // ----------
                                    // set traffic light
                                    dish.setTrafficLight(trafficLight);

                                    // ----------
                                    // set dish types
                                    List<DishType> currentDishTypes = new ArrayList<>();
                                    currentDishTypes.addAll(dishTypes);
                                    dish.setDishTypes(currentDishTypes);

                                    rv.add(dish);

                                    // RESET trafficLight, dishTypes
                                    trafficLight = null;
                                    dishTypes.clear();
                                }
                                else if (element.tagName().equals("a") && element.attr("href").startsWith("#ampel")) {
                                    // get traffic light
                                    switch (element.attr("href")) {
                                        case "#ampel_gruen":
                                            trafficLight = TrafficLight.GREEN;
                                            break;
                                        case "#ampel_orange":
                                            trafficLight = TrafficLight.ORANGE;
                                            break;
                                        case "#ampel_rot":
                                            trafficLight = TrafficLight.RED;
                                            break;
                                    }
                                }
                                else if (element.tagName().equals("a") && (element.attr("href").endsWith("_siegel")
                                        || element.attr("href").equals("#msc"))) {
                                    // get dishType
                                    switch (element.attr("href")) {
                                        case "#vegan_siegel":
                                            dishTypes.add(DishType.VEGAN);
                                            break;
                                        case "#vegetarisch_siegel":
                                            dishTypes.add(DishType.VEGETARIAN);
                                            break;
                                        case "#klimabaum_siegel":
                                            dishTypes.add(DishType.CLIMATE_NEUTRAL);
                                            break;
                                        case "#bio_siegel":
                                            dishTypes.add(DishType.BIO);
                                            break;
                                        case "#msc":
                                            dishTypes.add(DishType.MSC);
                                            break;
                                    }
                                }
                            }
                        }
                    }
                }
            }
        } catch(IOException e){
            logger.error("ERROR while parsing site: " + url, e);
        }
        return rv;
    }
}
