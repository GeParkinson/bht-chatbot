package canteen;

import java.util.List;

import org.apache.commons.cli.*;

/**
 * Created by sJantzen on 11.06.2017.
 * This method calls a parser with the given url, leading to a canteen page.
 */
public class ParserRunner {

    public static void main(String[] args) {

        Options options = new Options();
        Option aUrl = new Option("u", "url", true, "canteen url to parse");
        Option aDate = new Option("d","date", true, "requested date, for menu or opening hours");

        aUrl.setRequired(true);
        aDate.setRequired(false);

        options.addOption(aUrl);
        options.addOption(aDate);

        CommandLineParser cmdParser = new DefaultParser();
        CommandLine cmd = null;
        try{
            cmd = cmdParser.parse(options, args);
        } catch (ParseException e){
            // TODO logging
        }

        String url;
        String date;

        if(cmd != null){
            url = cmd.getOptionValue("url");
        }else{
            url = "http://www.studentenwerk-berlin.de/mensen/speiseplan/beuth/";
        }

        CanteenData data = Parser.parse(url);

        //test(data);

        output(data);
    }

    /**
     * JUnit test cases for the given canteen data. The test values has to be updated for every new day.
     * @param data
     */
    /*
    public static void test(final CanteenData data){
        List<Dish> vegetarianDishes = data.getDishesByType(DishType.VEGETARIAN);
        Assert.assertNotNull(vegetarianDishes);
        Assert.assertTrue(vegetarianDishes.size() == 9);

        List<Dish> veganDishes = data.getDishesByType(DishType.VEGAN);
        Assert.assertNotNull(veganDishes);
        Assert.assertTrue(veganDishes.size() == 9);

        List<Dish> bioDishes = data.getDishesByType(DishType.BIO);
        Assert.assertNotNull(bioDishes);
        Assert.assertTrue(bioDishes.size() == 3);

        List<Dish> climateDishes = data.getDishesByType(DishType.CLIMATE_NEUTRAL);
        Assert.assertNotNull(climateDishes);
        Assert.assertTrue(climateDishes.size() == 1);

        List<Dish> starterDishes = data.getDishesByCategory(DishCategory.STARTER);
        Assert.assertNotNull(starterDishes);
        Assert.assertTrue(starterDishes.size() == 1);

        List<Dish> saladDishes = data.getDishesByCategory(DishCategory.SALAD);
        Assert.assertNotNull(saladDishes);
        Assert.assertTrue(saladDishes.size() == 3);

        List<Dish> sideDishDishes = data.getDishesByCategory(DishCategory.SIDE_DISH);
        Assert.assertNotNull(sideDishDishes);
        Assert.assertTrue(sideDishDishes.size() == 4);

        List<Dish> foodDishes = data.getDishesByCategory(DishCategory.FOOD);
        Assert.assertNotNull(foodDishes);
        Assert.assertTrue(foodDishes.size() == 6);

        List<Dish> specialDishes = data.getDishesByCategory(DishCategory.SPECIAL);
        Assert.assertNotNull(specialDishes);
        Assert.assertTrue(specialDishes.size() == 2);

        List<Dish> dessertDishes = data.getDishesByCategory(DishCategory.DESSERT);
        Assert.assertNotNull(dessertDishes);
        Assert.assertTrue(dessertDishes.size() == 5);

        List<Dish> vegetarianFoodDishes = data.getDishesByCategoryAndType(DishCategory.FOOD, DishType.VEGETARIAN);
        Assert.assertNotNull(vegetarianFoodDishes);
        Assert.assertTrue(vegetarianFoodDishes.size() == 3);
    }
    */

    /**
     * Just some outputs to check the parsed values.
     * @param data
     */
    public static void output(final CanteenData data){
        List<Dish> vegetarianDishes = data.getDishesByType(DishType.VEGETARIAN);
        System.out.println("VEGETARIAN DISHES");
        for(Dish dish : vegetarianDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("VEGAN DISHES");
        List<Dish> veganDishes = data.getDishesByType(DishType.VEGAN);
        for(Dish dish : veganDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("BIO DISHES");
        List<Dish> bioDishes = data.getDishesByType(DishType.BIO);
        for(Dish dish : bioDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("CLIMATE DISHES");
        List<Dish> climateDishes = data.getDishesByType(DishType.CLIMATE_NEUTRAL);
        for(Dish dish : climateDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("STARTERS");
        List<Dish> starterDishes = data.getDishesByCategory(DishCategory.STARTER);
        for(Dish dish : starterDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("SALADS");
        List<Dish> saladDishes = data.getDishesByCategory(DishCategory.SALAD);
        for(Dish dish : saladDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("SIDE DISHES");
        List<Dish> sideDishDishes = data.getDishesByCategory(DishCategory.SIDE_DISH);
        for(Dish dish : sideDishDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("FOODS");
        List<Dish> foodDishes = data.getDishesByCategory(DishCategory.FOOD);
        for(Dish dish : foodDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("SPECIAL DISHES");
        List<Dish> specialDishes = data.getDishesByCategory(DishCategory.SPECIAL);
        for(Dish dish : specialDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("DESSERTS");
        List<Dish> dessertDishes = data.getDishesByCategory(DishCategory.DESSERT);
        for(Dish dish : dessertDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");

        System.out.println("VEGETARIAN FOODS");
        List<Dish> vegetarianFoodDishes = data.getDishesByCategoryAndType(DishCategory.FOOD, DishType.VEGETARIAN);
        for(Dish dish : vegetarianFoodDishes){
            System.out.println(dish.toString());
        }
        System.out.println("---------------------------------------");
    }
}
