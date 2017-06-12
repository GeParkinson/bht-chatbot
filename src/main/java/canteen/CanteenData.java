package canteen;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by sJantzen on 11.06.2017.
 */
public class CanteenData {

    private String locationName;
    private String strDate;
    private Date date;
    private String openingHours;
    private List<Dish> dishes;

    public CanteenData(){
    }

    public CanteenData(final String locationName, final String strDate, final Date date, final String openingHours, final List<Dish> dishes){
        this.locationName = locationName;
        this.strDate = strDate;
        this.date = date;
        this.openingHours = openingHours;
        this.dishes = dishes;
    }

    public String getLocationName() {
        return locationName;
    }

    public void setLocationName(String locationName) {
        this.locationName = locationName;
    }

    public String getStrDate() {
        return strDate;
    }

    public void setStrDate(String strDate) {
        this.strDate = strDate;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getOpeningHours() {
        return openingHours;
    }

    public void setOpeningHours(String openingHours) {
        this.openingHours = openingHours;
    }

    public List<Dish> getDishes() {
        return dishes;
    }

    public void setDishes(List<Dish> dishes) {
        this.dishes = dishes;
    }

    public List<Dish> getDishesByType(final DishType dishType){
        List<Dish> dishesByType = new ArrayList<>();
        if(dishes != null){
            for(Dish dish : dishes){
                if(dish.getDishTypes() != null && dish.getDishTypes().contains(dishType)){
                    dishesByType.add(dish);
                }
            }
        }
        return dishesByType;
    }

    public List<Dish> getDishesByCategory(final DishCategory dishCategory){
        List<Dish> dishesByCategory = new ArrayList<>();
        if(dishes != null){
            for(Dish dish : dishes){
                if(dish.getDishCategory() != null && dish.getDishCategory().equals(dishCategory)){
                    dishesByCategory.add(dish);
                }
            }
        }
        return dishesByCategory;
    }

    public List<Dish> getDishesByCategoryAndType(final DishCategory dishCategory, final DishType dishType){
        List<Dish> dishesByCategoryAndType = new ArrayList<>();
        if(dishes != null){
            for(Dish dish : dishes){
                if(dish.getDishCategory() != null && dish.getDishCategory().equals(dishCategory)){
                    if(dish.getDishTypes() != null && dish.getDishTypes().contains(dishType)){
                        dishesByCategoryAndType.add(dish);
                    }
                }
            }
        }
        return dishesByCategoryAndType;
    }

    public String toString(){
        StringBuilder sb = new StringBuilder();
        sb.append("Location: " + locationName).append("\n");
        sb.append("Date: " + strDate).append("\n");
        sb.append("Opening hours: " + openingHours).append("\n");
        sb.append("Dishes:").append("\n");
        for(Dish dish : dishes){
            sb.append(dish.toString()).append("\n");
        }
        return sb.toString();
    }
}
