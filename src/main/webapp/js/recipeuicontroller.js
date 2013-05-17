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

    function saveRecipe() {
        console.log("saverecipe")

        var recipeName = $('input[name=recipe-name]').val()
        var recipeTags = $('input[name=recipe-tags]').val()

        var recipeContent = nicEditors.findEditor('recipe-content').getContent();
        var recipeObject = { name: recipeName, tags: recipeTags, content: recipeContent }
        recipeService.createNewRecipe(recipeObject, function() { console.log("Successfully submitted") })
    }

    return {
        switchToNewRecipeView: switchToNewRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        saveRecipe: saveRecipe

    }
})();