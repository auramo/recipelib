var recipeUiController = (function() {

    function recipeNameField() {
        return $('input[name=recipe-name]')
    }

    function recipeTagsField() {
        return $('input[name=recipe-tags]')
    }

    function recipeContentField() {
        return $('.nicEdit-main')
    }

    function recipientContentValue() {
        function value() {
            return nicEditors.findEditor('recipe-content').getContent()
        }
        return recipeContentField().asEventStream('keyup').map(value).log().toProperty(value())
    }

    function getRecipeContent() {
        return nicEditors.findEditor('recipe-content').getContent()
    }

    function switchToEditRecipeView(recipeId) {
        if (recipeId) {
            console.log("Switching to edit view with id: " + recipeId)
        }
        $('.new-recipe').show()
        $('.recipe-list').hide()
    }

    function initValidators() {
        function nonEmpty(x) { return x.length > 0 && x !== '<br>' }
        var recipeNameEntered = Bacon.UI.textFieldValue(recipeNameField()).map(nonEmpty)
        var recipeContentEntered = recipientContentValue().map(nonEmpty)
        var buttonEnabled = recipeNameEntered.and(recipeContentEntered)
        buttonEnabled.not().onValue($(".save-button"), "attr", "disabled")
    }

    function switchToRecipeListView(recipes) {
        function recipeListRow(recipe) {
            return '<ul><a href="#" onclick="recipeUiController.switchToEditRecipeView()">' + recipe.name + '</a></ul>';
        }

        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return recipeListRow(recipe); }).reduce(function (r1, r2) { return r1 + r2 })
        }
        $('.new-recipe').hide()
        $('.recipe-list').show()
        $('.recipe-list ul').html(getRecipeListHtml())
    }

    function saveRecipe() {
        var recipeName = recipeNameField().val()
        var recipeTags = recipeTagsField().val()
        var recipeContent = getRecipeContent();
        var recipeObject = { name: recipeName, tags: recipeTags, content: recipeContent }
        recipeService.createNewRecipe(recipeObject, function() { console.log("Successfully submitted") })
    }

    return {
        switchToEditRecipeView: switchToEditRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        saveRecipe: saveRecipe,
        initValidators: initValidators
    }
})();