$(document).ready(function() {
    recipeService.getRecipes(function(result) {
        console.log(result)
        if (_.isEmpty(result.recipes)) {
            recipeUiController.switchToNewRecipeView()
        } else {
            recipeUiController.switchToRecipeListView(result.recipes)
        }
    });
});