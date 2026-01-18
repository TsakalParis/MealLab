package UniPi.MealLab.API;

import com.fasterxml.jackson.databind.ObjectMapper;
import UniPi.MealLab.Model.MealResponse;
import UniPi.MealLab.Model.Recipe;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

/**
 * Service class responsible for communicating with the external MealDB API.
 * Handles HTTP requests and JSON parsing.
 */
public class MealService {

    private static final String BASE_URL = "https://www.themealdb.com/api/json/v1/1/";
    private final HttpClient client;
    private final ObjectMapper mapper;

    public MealService() {
        this.client = HttpClient.newHttpClient();
        this.mapper = new ObjectMapper();
    }

    /**
     * Helper method to execute a GET request to the API.
     * @param suffix The endpoint suffix (e.g., "search.php?s=pasta")
     * @return List of Recipe objects
     * @throws Exception if network error or parsing fails
     */
    private List<Recipe> makeRequest(String suffix) throws Exception {
        String url = BASE_URL + suffix;
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .GET()
                .build();
        
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() != 200) {
            throw new RuntimeException("API Error: HTTP " + response.statusCode());
        }

        MealResponse result = mapper.readValue(response.body(), MealResponse.class);
        return result.getMeals();
    }

    /**
     * Searches for recipes by ingredient or name.
     * @param query The search term (e.g., "chicken")
     * @return List of matching recipes
     */
    public List<Recipe> searchRecipes(String query) throws Exception {
        // Encode query to handle spaces
        String encodedQuery = query.replace(" ", "%20");
        return makeRequest("search.php?s=" + encodedQuery);
    }
    
    /**
     * Retrieves full details for a specific recipe by ID.
     * @param id The recipe ID
     * @return The Recipe object or null if not found
     */
    public Recipe getRecipeById(String id) throws Exception {
        List<Recipe> recipes = makeRequest("lookup.php?i=" + id);
        if (recipes != null && !recipes.isEmpty()) {
            return recipes.get(0);
        }
        return null;
    }

    // Search by Name
    public List<Recipe> searchByName(String name) throws Exception {
        return makeRequest("search.php?s=" + name.replace(" ", "%20"));
    }

    // Search by Ingredient
    public List<Recipe> searchByIngredient(String ingredient) throws Exception {
        return makeRequest("filter.php?i=" + ingredient.replace(" ", "%20"));
    }

    /**
     * Fetches a completely random recipe.
     * @return A random Recipe object
     */
    public Recipe getRandomRecipe() throws Exception {
        List<Recipe> recipes = makeRequest("random.php");
        if (recipes != null && !recipes.isEmpty()) {
            return recipes.get(0);
        }
        return null;
    }
}
