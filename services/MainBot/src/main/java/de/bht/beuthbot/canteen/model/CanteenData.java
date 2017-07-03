package de.bht.beuthbot.canteen.model;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by sJantzen on 11.06.2017.
 */
public class CanteenData {

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
     * @param date
     * @param trafficLight
     * @param dishTypes
     * @param dishCategories
     * @param markings
     * @return
     */
    public final List<Dish> getDishesFiltered(final LocalDate date, final TrafficLight trafficLight, final List<DishType> dishTypes,
                                              final List<DishCategory> dishCategories, final List<String> markings) {

            /*
             * TODO man könnte noch Suchmodi hinzufügen, also so etwas wie:
             * Ein Gericht muss ALLE types enthalten
             * Ein Gericht muss mindestens EINEN enthalten
             * Ein Gericht darf KEINEN von beiden enthalten
              */

            return dishes.stream().filter(dish -> (date == null || date.equals(dish.getDate())))
            .filter(dish -> (trafficLight == null || trafficLight.equals(dish.getTrafficLight())))
            .filter(dish -> (dishTypes != null && !dishTypes.isEmpty() ? dish.getDishTypes().containsAll(dishTypes) : true))
            .filter(dish -> (dishCategories != null && !dishTypes.isEmpty() ? dishCategories.contains(dish.getDishCategory()) : true))
            .filter(dish -> (markings != null && !markings.isEmpty() ? dish.getMarkings().containsAll(markings) : true))
            .collect(Collectors.toList());
    }

    /**
     * Returns all dishes of the given date.
     * @param date
     * @return
     */
    public final List<Dish> getDishesByDate(final LocalDate date) {
        return getDishesFiltered(date, null, null, null, null);
    }

    /**
     * Returns a list of dishes with the given dishTypes.
     * @param dishTypes
     * @return
     */
    public final List<Dish> getDishesByDishType(final List<DishType> dishTypes) {
        return getDishesFiltered(null, null, dishTypes, null, null);
    }

    /**
     * Returns a list of dishes having a dishCategory in the given dishCategory-list.
     * @param dishCategories
     * @return
     */
    public final List<Dish> getDishesByDishCategory(final List<DishCategory> dishCategories) {
        return getDishesFiltered(null, null, null, dishCategories, null);
    }

    /**
     * Returns a list of dishes matching the given trafficLight.
     * @param trafficLight
     * @return
     */
    public final List<Dish> getDishesByTrafficLight(final TrafficLight trafficLight) {
        return getDishesFiltered(null, trafficLight, null, null, null);
    }

    /**
     * Returns a list of dishes with the given markings.
     * @param markings
     * @return
     */
    public final List<Dish> getDishesByMarkings(final List<String> markings) {
        return getDishesFiltered(null, null, null, null, markings);
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
