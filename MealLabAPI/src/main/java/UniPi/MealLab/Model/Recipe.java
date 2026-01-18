package UniPi.MealLab.Model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class Recipe {
    public String idMeal;
    public String strMeal;
    public String strCategory;
    public String strArea;
    public String strInstructions;
    public String strMealThumb;

    // Ingredients
    public String strIngredient1, strIngredient2, strIngredient3, strIngredient4, strIngredient5,
                  strIngredient6, strIngredient7, strIngredient8, strIngredient9, strIngredient10,
                  strIngredient11, strIngredient12, strIngredient13, strIngredient14, strIngredient15,
                  strIngredient16, strIngredient17, strIngredient18, strIngredient19, strIngredient20;

    // Measures
    public String strMeasure1, strMeasure2, strMeasure3, strMeasure4, strMeasure5,
                  strMeasure6, strMeasure7, strMeasure8, strMeasure9, strMeasure10,
                  strMeasure11, strMeasure12, strMeasure13, strMeasure14, strMeasure15,
                  strMeasure16, strMeasure17, strMeasure18, strMeasure19, strMeasure20;

    // Getters
    public String getIdMeal() { return idMeal; }
    public String getStrMeal() { return strMeal; }
    public String getStrCategory() { return strCategory; }
    public String getStrArea() { return strArea; }
    public String getStrInstructions() { return strInstructions; }
    public String getStrMealThumb() { return strMealThumb; }

    public String getFullIngredients() {
        StringBuilder sb = new StringBuilder();
        append(sb, strIngredient1, strMeasure1); append(sb, strIngredient2, strMeasure2);
        append(sb, strIngredient3, strMeasure3); append(sb, strIngredient4, strMeasure4);
        append(sb, strIngredient5, strMeasure5); append(sb, strIngredient6, strMeasure6);
        append(sb, strIngredient7, strMeasure7); append(sb, strIngredient8, strMeasure8);
        append(sb, strIngredient9, strMeasure9); append(sb, strIngredient10, strMeasure10);
        append(sb, strIngredient11, strMeasure11); append(sb, strIngredient12, strMeasure12);
        append(sb, strIngredient13, strMeasure13); append(sb, strIngredient14, strMeasure14);
        append(sb, strIngredient15, strMeasure15); append(sb, strIngredient16, strMeasure16);
        append(sb, strIngredient17, strMeasure17); append(sb, strIngredient18, strMeasure18);
        append(sb, strIngredient19, strMeasure19); append(sb, strIngredient20, strMeasure20);
        return sb.toString();
    }

    private void append(StringBuilder sb, String ing, String ms) {
        if (ing != null && !ing.trim().isEmpty()) {
            sb.append("- ").append(ing);
            if (ms != null && !ms.trim().isEmpty()) {
                sb.append(": ").append(ms);
            }
            sb.append("\n");
        }
    }
    
    @Override
    public String toString() { return strMeal; }
}
