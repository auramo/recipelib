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
        var content = nicEditors.findEditor('recipe-content').getContent();
        var objectWithHtmlContent = {otherField: "Hi there", htmlContent: content}
        stringified = JSON.stringify(objectWithHtmlContent)
        console.log(stringified)
        //recipeService.createNewRecipe()
    }

    return {
        switchToNewRecipeView: switchToNewRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        saveRecipe: saveRecipe

    }
})();