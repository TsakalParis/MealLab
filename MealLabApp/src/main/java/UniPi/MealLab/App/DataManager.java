package UniPi.MealLab.App;

import com.google.gson.Gson;
import UniPi.MealLab.Model.Recipe;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class DataManager {
    private static final String FILE_NAME = "meallab_data.json";
    private final Gson gson = new Gson();

    // Data Structure for JSON
    public static class UserData {
        public List<Recipe> favorites = new ArrayList<>();
        public List<Recipe> cooked = new ArrayList<>();
    }

    public void save(List<Recipe> favs, List<Recipe> cooked) {
        UserData data = new UserData();
        data.favorites = favs;
        data.cooked = cooked;
        try (Writer writer = new FileWriter(FILE_NAME)) {
            gson.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public UserData load() {
        File file = new File(FILE_NAME);
        if (!file.exists()) return new UserData();

        try (Reader reader = new FileReader(file)) {
            UserData data = gson.fromJson(reader, UserData.class);
            return (data != null) ? data : new UserData();
        } catch (IOException e) {
            return new UserData();
        }
    }
}
