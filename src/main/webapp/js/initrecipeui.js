$(document).ready(function() {
    recipes = recipeService.getRecipes();
    if (!_.isEmpty(recipes)) {
        $('.new-recipe').show()
    } else {
        $('.new-recipe').hide()
    }
});