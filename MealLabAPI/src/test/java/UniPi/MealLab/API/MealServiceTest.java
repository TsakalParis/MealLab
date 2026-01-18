package UniPi.MealLab.API;

import org.junit.jupiter.api.Test;
import UniPi.MealLab.Model.Recipe;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

public class MealServiceTest {

    @Test
    public void testSearchWorks() {
        try {
            MealService service = new MealService();
            List<Recipe> recipes = service.searchRecipes("chicken");
            
            assertNotNull(recipes, "List should not be null");
            assertFalse(recipes.isEmpty(), "List should not be empty");
            
        } catch (Exception e) {
            fail("Test failed: " + e.getMessage());
        }
    }
    
    @Test
    public void testRandomRecipe() {
        try {
            MealService service = new MealService();
            Recipe r = service.getRandomRecipe();
            assertNotNull(r, "Random recipe should not be null");
        } catch (Exception e) {
            fail("Random failed: " + e.getMessage());
        }
    }
}
