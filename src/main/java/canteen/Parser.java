package canteen;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sJantzen on 11.06.2017.
 * Parser class, to parse a given url, leading to the canteen page of the Beuth-Hochschule.
 */
public class Parser {

    /**
     * Creates new CanteenData by parsing a given canteen url.
     * @param url
     * @return
     */
    public static CanteenData parse(final String url){

        CanteenData data = new CanteenData();

        List<Dish> dishes = new ArrayList<>();

        try{
            Document doc = Jsoup.connect(url).get();

            Elements tables = doc.select("div.mensa_day_speise");

            /*
             * Set location.
             */
            Elements locationContainer = doc.select("#content h1");
            for (Element h1 : locationContainer){
                data.setLocationName(h1.ownText().replace("Speiseplan ", ""));
            }

            /*
             * Set date.
             */
            data.setStrDate(doc.getElementById("content").getElementsByTag("div").get(0).getElementsByTag("p").get(0).text());
            // TODO Date ermitteln
            // TODO Möglichkeit einführen, dass man den Speiseplan für den angefragten Tag raussucht. Müsste hoch zum Seitenaufruf.

            /*
             * Set opening hours.
             */
            // TODO es gibt keine informationen dazu auf der Homepage, deshalb einfach fest setzen.
            data.setOpeningHours("Küchen-Öffnungszeiten täglich von 11:00 bis 14:30 Uhr.");

            /*
             * Loop all dishes.
             */
            for(Element element : tables){

                DishCategory category = null;

                /*
                 * Set dish category.
                 */
                if (element.classNames().contains("starters")) {
                    category = DishCategory.STARTER;
                }else if (element.classNames().contains("salads")) {
                    category = DishCategory.SALAD;
                }else if (element.classNames().contains("soups")) {
                    category = DishCategory.SOUP;
                }else if (element.classNames().contains("special")) {
                    category = DishCategory.SPECIAL;
                }else if (element.classNames().contains("food")) {
                    category = DishCategory.FOOD;
                }else if (element.classNames().contains("side_dishes")) {
                    category = DishCategory.SIDE_DISH;
                }else if (element.classNames().contains("desserts")) {
                    category = DishCategory.DESSERT;
                }

                /*
                 * Gets all dishes of a category.
                 */
                Elements dishRows = element.select("tr.mensa_day_speise_row");
                for (Element row : dishRows){

                    Dish dish = new Dish();

                    /*
                     * Dish name.
                     */
                    Elements nameContainer = row.select("td.mensa_day_speise_name");
                    for (Element td : nameContainer){
                        dish.setName(td.ownText());
                    }

                    /*
                     * Set category.
                     */
                    dish.setDishCategory(category);

                    /*
                     * Set dish types.
                     */
                    Elements typeContainer = row.select("a[href$=siegel]");
                    List<DishType> dishTypes = new ArrayList<>();
                    for (Element img : typeContainer){
                        switch (img.attr("href").split("_")[0].replace("#","")){
                            case "vegetarisch":
                                dishTypes.add(DishType.VEGETARIAN);
                                break;
                            case "vegan":
                                dishTypes.add(DishType.VEGAN);
                                break;
                            case "bio":
                                dishTypes.add(DishType.BIO);
                                break;
                            case "klimabaum":
                                dishTypes.add(DishType.CLIMATE_NEUTRAL);
                                break;
                            default:
                                System.out.println("Type: " + img.attr("href").split("_")[0].replace("#",""));
                        }
                    }
                    dish.setDishTypes(dishTypes);

                    /*
                     * Set traffic light for this dish.
                     */
                    Elements trafficLightContainer = row.select("a[href^=#ampel]");
                    for (Element img : trafficLightContainer){
                        switch (img.attr("href").split("_")[1]){
                            case "gruen":
                                dish.setTrafficLight(TrafficLight.GREEN);
                                break;
                            case "orange":
                                dish.setTrafficLight(TrafficLight.ORANGE);
                                break;
                            case "rot":
                                dish.setTrafficLight(TrafficLight.RED);
                                break;
                            default:
                                System.out.println("TrafficLight: " + img.attr("href").split("_")[1]);
                        }
                    }

                    /*
                     * Gets all markings for a dish.
                     */
                    Elements markings = row.select("a.zusatz");
                    List<String> dishMarkings = new ArrayList<>();
                    for (Element marking : markings){
                        dishMarkings.add(marking.attr("title"));
                    }

                    dish.setMarkings(dishMarkings);

                    /*
                     * Get the dish price.
                     */
                    Elements priceContainer = row.select("td.mensa_day_speise_preis");
                    for (Element td : priceContainer){
                        dish.setPrice(td.ownText());
                    }

                    dishes.add(dish);
                }
            }
        } catch(IOException e){
            System.out.println("ERROR: " + e.getStackTrace());
        }

        data.setDishes(dishes);

        /*
         * Output
         */
        System.out.println(data.toString());

        return data;
    }
}
