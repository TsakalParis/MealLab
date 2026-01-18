package UniPi.MealLab.Model;
import java.util.List;
public class MealResponse {
public List<Recipe> meals;
public List<Recipe> getMeals() {
return meals;
}
public void setMeals(List<Recipe> meals) {
this.meals = meals;
}
}