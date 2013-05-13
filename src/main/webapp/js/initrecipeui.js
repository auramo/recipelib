$(document).ready(function() {
    recipeService.getRecipes(function(result) {
        console.log(result)
        if (_.isEmpty(result.recipes)) {
            $('.new-recipe').show()
            $('.recipe-list').hide()
        } else {
            $('.new-recipe').hide()
            $('.recipe-list').show()
            $('.recipe-list ul').html(getRecipeListHtml(result.recipes))
        }
    });
    function getRecipeListHtml(recipes) {
      return _(recipes).map(function(recipe) { return "<ul>" + recipe.name + "</ul>"; }).reduce(function (r1, r2) { return r1 + r2 })
    }
});