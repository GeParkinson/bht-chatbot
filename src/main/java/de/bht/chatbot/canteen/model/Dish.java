package de.bht.chatbot.canteen.model;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by sJantzen on 11.06.2017.
 */
public class Dish {

    private static final DateTimeFormatter dtf = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    private String name;
    private LocalDate date;
    private String price;
    private BigDecimal priceStudent;
    private BigDecimal priceEmployee;
    private BigDecimal priceGuest;
    private TrafficLight trafficLight;
    private List<DishType> dishTypes;
    private List<String> markings;
    private DishCategory dishCategory;

    /**
     * Default constructor.
     */
    public Dish(){
        dishTypes = new ArrayList<>();
        markings = new ArrayList<>();
    }

    /**
     * Full constructor.
     * @param name
     * @param date
     * @param price
     * @param priceStudent
     * @param priceEmployee
     * @param priceGuest
     * @param trafficLight
     * @param dishTypes
     * @param markings
     * @param dishCategory
     */
    public Dish(final String name, final LocalDate date, final String price, final BigDecimal priceStudent, final BigDecimal priceEmployee,
                final BigDecimal priceGuest, final TrafficLight trafficLight, final List<DishType> dishTypes,
                final List<String> markings, final DishCategory dishCategory){
        this.name = name;
        this.date = date;
        this.price = price;
        this.priceStudent = priceStudent;
        this.priceEmployee = priceEmployee;
        this.priceGuest = priceGuest;
        this.trafficLight = trafficLight;
        this.dishTypes = dishTypes;
        this.markings = markings;
        this.dishCategory = dishCategory;
    }

    public final String getName() {
        return name;
    }

    public final void setName(final String name) {
        this.name = name;
    }

    public final String getPrice() {
        return price;
    }

    public final void setPrice(final String price) {
        this.price = price;
    }

    public final TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public final void setTrafficLight(final TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public final List<DishType> getDishTypes() {
        return dishTypes;
    }

    public final void setDishTypes(final List<DishType> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public final List<String> getMarkings() {
        return markings;
    }

    public final void setMarkings(final List<String> markings) {
        this.markings = markings;
    }

    public final DishCategory getDishCategory() {
        return dishCategory;
    }

    public final void setDishCategory(final DishCategory dishCategory) {
        this.dishCategory = dishCategory;
    }

    public final LocalDate getDate() {
        return date;
    }

    public final void setDate(final LocalDate date) {
        this.date = date;
    }

    public final BigDecimal getPriceStudent() {
        return priceStudent;
    }

    public final void setPriceStudent(final BigDecimal priceStudent) {
        this.priceStudent = priceStudent;
    }

    public final BigDecimal getPriceEmployee() {
        return priceEmployee;
    }

    public final void setPriceEmployee(final BigDecimal priceEmployee) {
        this.priceEmployee = priceEmployee;
    }

    public final BigDecimal getPriceGuest() {
        return priceGuest;
    }

    public final void setPriceGuest(final BigDecimal priceGuest) {
        this.priceGuest = priceGuest;
    }

    public final String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("--------------------------").append("\n");
        sb.append(date.format(dtf) + " - " + dishCategory + ":").append("\n");
        sb.append(name).append("\n");
        sb.append(price).append("\n");
        //sb.append("Studenten-Preis: " + priceStudent + " EUR").append("\n");
        //sb.append("Mitarbeiter-Preis: " + priceEmployee + " EUR").append("\n");
        //sb.append("Gäste-Preis: " + priceGuest + " EUR").append("\n");
        sb.append("Ampel: " + trafficLight.getText()).append("\n");
        if (dishTypes != null && dishTypes.size() > 0) {
            sb.append("Siegel: " + dishTypes.toString()).append("\n");
        }
        if (markings != null && markings.size() > 0) {
            sb.append("Zusätze: " + String.join(", ", markings));
        }
        sb.append("\n");
        return sb.toString();
    }
}
