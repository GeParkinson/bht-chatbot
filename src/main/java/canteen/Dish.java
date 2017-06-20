package canteen;

import java.util.List;

/**
 * Created by sJantzen on 11.06.2017.
 */
public class Dish {

    private String name;
    private String price;
    private TrafficLight trafficLight;
    private List<DishType> dishTypes;
    private List<String> markings;
    private DishCategory dishCategory;

    public Dish(){
    }

    public Dish(final String name, final String price, final TrafficLight trafficLight, final List<DishType> dishTypes, final List<String> markings, final DishCategory dishCategory){
        this.name = name;
        this.price = price;
        this.trafficLight = trafficLight;
        this.dishTypes = dishTypes;
        this.markings = markings;
        this.dishCategory = dishCategory;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public TrafficLight getTrafficLight() {
        return trafficLight;
    }

    public void setTrafficLight(TrafficLight trafficLight) {
        this.trafficLight = trafficLight;
    }

    public List<DishType> getDishTypes() {
        return dishTypes;
    }

    public void setDishTypes(List<DishType> dishTypes) {
        this.dishTypes = dishTypes;
    }

    public List<String> getMarkings() {
        return markings;
    }

    public void setMarkings(List<String> markings) {
        this.markings = markings;
    }

    public DishCategory getDishCategory() {
        return dishCategory;
    }

    public void setDishCategory(DishCategory dishCategory) {
        this.dishCategory = dishCategory;
    }

    public String toString(){
        return name + (markings != null ? "  -  Zusätze: " + String.join(",", markings) : "") +"  -  " + price + "  -  " + (dishCategory != null ? dishCategory.toString() : "") + "  -  " + (dishTypes != null ? dishTypes.toString() : "") + "  -  " + trafficLight;
    }
}
