package UniPi.MealLab.App;

import org.junit.jupiter.api.Test;
import UniPi.MealLab.Model.Recipe;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import static org.junit.jupiter.api.Assertions.*;

class DataManagerTest {

    @Test
    void testSaveAndLoad() {
        Recipe r = new Recipe();
        r.idMeal = "999";
        r.strMeal = "Test Burger";
        
        List<Recipe> favs = new ArrayList<>();
        favs.add(r);
        
        List<Recipe> cooked = new ArrayList<>();

        DataManager manager = new DataManager();
        manager.save(favs, cooked);
        
        DataManager.UserData loaded = manager.load();
        
        assertNotNull(loaded);
        assertEquals(1, loaded.favorites.size());
        assertEquals("Test Burger", loaded.favorites.get(0).strMeal);
        
        new File("meallab_data.json").delete();
    }
}
