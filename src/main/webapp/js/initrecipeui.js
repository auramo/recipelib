$(document).ready(function() {
    nicEditors.allTextAreas()
    $('.new-recipe').hide()
    $('.recipe-list').hide()
    recipeUiController.initValidators()
    recipeService.getRecipes(function(result) {
        console.log(result)
        if (_.isEmpty(result.recipes)) {
            recipeUiController.switchToNewRecipeView()
        } else {
            recipeUiController.switchToRecipeListView(result.recipes)
        }
    });
});