var recipeUiController = (function() {

    function switchToNewRecipeView() {
        $('.new-recipe').show()
        $('.recipe-list').hide()
    }

    function switchToRecipeListView(recipes) {
        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return "<ul>" + recipe.name + "</ul>"; }).reduce(function (r1, r2) { return r1 + r2 })
        }
        $('.new-recipe').hide()
        $('.recipe-list').show()
        $('.recipe-list ul').html(getRecipeListHtml())
    }

    return {
        switchToNewRecipeView: switchToNewRecipeView,
        switchToRecipeListView: switchToRecipeListView
    }
})();