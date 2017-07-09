package de.bht.chatbot.canteen.model;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * This class represents all data of dishes for the current and the next
 * week of the canteen of the beuth university.
 * Created by sJantzen on 11.06.2017.
 */
public class CanteenData {

    private Logger logger = LoggerFactory.getLogger(CanteenData.class);

    private String locationName;
    private String openingHoursKitchen;
    private String openingHours;
    private List<Dish> dishes;

    /**
     * Default constructor.
     */
    private CanteenData(){
    }

    /**
     * Full constructor.
     * @param locationName
     * @param openingHoursKitchen
     * @param openingHours
     * @param dishes
     */
    public CanteenData(final String locationName, final String openingHoursKitchen, final String openingHours, final List<Dish> dishes){
        this.locationName = locationName;
        this.openingHoursKitchen = openingHoursKitchen;
        this.openingHours = openingHours;
        this.dishes = dishes;
    }

    public final String getLocationName() {
        return locationName;
    }

    public final void setLocationName(final String locationName) {
        this.locationName = locationName;
    }

    public final String getOpeningHoursKitchen() {
        return openingHoursKitchen;
    }

    public final void setOpeningHoursKitchen(final String openingHoursKitchen) {
        this.openingHoursKitchen = openingHoursKitchen;
    }

    public final String getOpeningHours() {
        return openingHours;
    }

    public final void setOpeningHours(final String openingHours) {
        this.openingHours = openingHours;
    }

    public final List<Dish> getDishes() {
        return dishes;
    }

    public final void setDishes(final List<Dish> dishes) {
        this.dishes = dishes;
    }

    /**
     * Returns all dishes, matching the given parameters.
     * @param pDate if you wish to get the dishes of the given date
     * @param pHealthy gets all dishes having the given traffic light color
     * @param pDishType gets dishes, containing all given dishTypes
     * @param pMarkings gets dishes, containing all given markings
     * @return
     */
    public final List<Dish> getDishesFiltered(final String pDate, final String pHealthy, final String pDishType,
                                              final String pMarkings) {

        final LocalDate date = ("tomorrow".equals(pDate) ? LocalDate.now().plusDays(1) : LocalDate.now());

        final TrafficLight trafficLight = (pHealthy != null && !"".equals(pHealthy) ? TrafficLight.getTrafficLight(pHealthy): null);

        final DishType dishType = (pDishType != null && !"".equals(pDishType) ? DishType.getDishTypeByName(pDishType) : null);

        //final List<String> markings = (pMarkings != null && !"".equals(pMarkings) ? Arrays.asList(pMarkings.split(",")) : null);

            /*
             * TODO man könnte noch Suchmodi hinzufügen, also so etwas wie:
             * Ein Gericht muss ALLE types enthalten
             * Ein Gericht muss mindestens EINEN enthalten
             * Ein Gericht darf KEINEN von beiden enthalten
              */

        return dishes.stream().filter(dish -> (date.equals(dish.getDate())))
                .filter(dish -> (trafficLight == null || trafficLight.equals(dish.getTrafficLight())))
                .filter(dish -> (dishType == null || dish.getDishTypes().contains(dishType)))
                .collect(Collectors.toList());
    }

    /**
     * Returns all dishes of the given date.
     * @param date
     * @return
     */
    public final List<Dish> getDishesByDate(final String date) {
        return getDishesFiltered(date, null, null, null);
    }

    /**
     * Returns a list of dishes with the given dishTypes.
     * @param dishTypes
     * @return
     */
    public final List<Dish> getDishesByDishType(final String dishTypes) {
        return getDishesFiltered(null, null, dishTypes, null);
    }

    /**
     * Returns a list of dishes matching the given trafficLight.
     * @param trafficLight
     * @return
     */
    public final List<Dish> getDishesByTrafficLight(final String trafficLight) {
        return getDishesFiltered(null, trafficLight, null, null);
    }

    /**
     * Returns a list of dishes with the given markings.
     * @param markings
     * @return
     */
    public final List<Dish> getDishesByMarkings(final String markings) {
        return getDishesFiltered(null, null, null, markings);
    }

    public final String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Location: " + locationName).append("\n");
        sb.append("Opening hours kitchen: " + openingHoursKitchen).append("\n");
        sb.append("Opening hours: " + openingHours).append("\n");
        sb.append("Dishes:").append("\n");
        for(Dish dish : dishes){
            sb.append(dish.toString()).append("\n");
        }
        return sb.toString();
    }
}
