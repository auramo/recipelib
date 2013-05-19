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

    function switchToNewRecipeView(fade) {
        //$('.new-recipe').show()
        if (fade) {
            $('.new-recipe').fadeIn()
            $('.recipe-list').fadeOut()
        } else {
            $('.new-recipe').show()
            $('.recipe-list').hide()
        }
        //initValidators()
    }

    function initValidators() {
        function nonEmpty(x) { return x.length > 0 && x !== '<br>' }
        recipeNameEntered = Bacon.UI.textFieldValue(recipeNameField()).map(nonEmpty)
        recipeContentEntered = recipientContentValue().map(nonEmpty)
        buttonEnabled = recipeNameEntered.and(recipeContentEntered)
        buttonEnabled.not().onValue($(".save-button"), "attr", "disabled")
    }

    function switchToRecipeListView(recipes, fade) {
        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return "<ul>" + recipe.name + "</ul>"; }).reduce(function (r1, r2) { return r1 + r2 })
        }
        if (fade) {
            $('.new-recipe').fadeOut()
            $('.recipe-list').fadeIn()
        } else {
            $('.new-recipe').hide()
            $('.recipe-list').show()
        }
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
        switchToNewRecipeView: switchToNewRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        saveRecipe: saveRecipe,
        initValidators: initValidators
    }
})();