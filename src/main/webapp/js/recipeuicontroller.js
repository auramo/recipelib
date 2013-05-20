var recipeUiController = (function() {

    var cachedRecipes = []

    function recipeIdField() {
        return $('input[name="recipe-id"]')
    }

    function recipeNameField() {
        return $('input[name=recipe-name]')
    }

    function recipeTagsField() {
        return $('input[name=recipe-tags]')
    }

    function recipeOriginalAddressField() {
        return $('input[name=original-address]')
    }

    function recipeContentField() {
        return nicEditors.findEditor('recipe-content')
    }

    function recipientContentValue() {
        function value() {
            return nicEditors.findEditor('recipe-content').getContent()
        }
        return $('.nicEdit-main').asEventStream('keyup').map(value).log().toProperty(value())
    }

    function getRecipeContent() {
        return recipeContentField().getContent()
    }

    function fillEditRecipeFields(recipe) {
        recipeIdField().val(recipe.id)
        recipeNameField().val(recipe.name)
        recipeTagsField().val(recipe.tags)
        recipeOriginalAddressField().val(recipe.originalAddress)
        recipeContentField().setContent(recipe.content)
    }

    function switchToEditRecipeView(recipeId) {
        if (recipeId) {
            console.log("Switching to edit view with id: " + recipeId)
            recipeService.getRecipe(recipeId, fillEditRecipeFields)
        } else {
            fillEditRecipeFields({name: "", tags: "", originalAddress: "", content: ""})
        }
        $('.new-recipe').show()
        $('.recipe-list').hide()
    }

    function initEvents() {
        function nonEmpty(x) { return x.length > 0 && x !== '<br>' }
        var recipeNameEntered = Bacon.UI.textFieldValue(recipeNameField()).map(nonEmpty)
        var recipeContentEntered = recipientContentValue().map(nonEmpty)
        var buttonEnabled = recipeNameEntered.and(recipeContentEntered)
        buttonEnabled.not().onValue($(".save-button"), "attr", "disabled")

        Bacon.UI.textFieldValue($('.search-recipes')).debounce(400).onValue(search)
    }

    function search(searchString) {
        function matchRecipe(recipe) { return _.contains(recipe.name.toLowerCase(), searchString.toLowerCase()) }
        var found = _(cachedRecipes).filter(matchRecipe).map(function(filtered) { return "recipe-" + filtered.id }).value()
        var rows = $('.recipe-list-row')
        rows.filter(function() { return !_.contains(found, this.id) }).hide()
        rows.filter(function() { return _.contains(found, this.id) }).show()
    }

    function start() {
        recipeService.getRecipes(function(result) {
            console.log(result)
            cachedRecipes = result.recipes
            if (_.isEmpty(result.recipes)) {
                switchToEditRecipeView()
            } else {
                switchToRecipeListView(result.recipes)
            }
        });
    }

    function switchToRecipeListView(recipes) {
        function recipeListRow(recipe) {
            return '<li class="recipe-list-row" id="recipe-' +
                recipe.id +
                '"><a href="#" onclick="recipeUiController.switchToEditRecipeView(' +
                recipe.id +
                ')">' +
                recipe.name +
                '</a></li>';
        }

        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return recipeListRow(recipe); }).reduce(function (r1, r2) { return r1 + r2 })
        }
        $('.new-recipe').hide()
        $('.recipe-list').show()
        $('.recipe-list ul').html(getRecipeListHtml())
    }

    function saveRecipe() {
        var recipeId = recipeIdField().val()
        var recipeName = recipeNameField().val()
        var recipeTags = recipeTagsField().val()
        var originalAddress = recipeOriginalAddressField().val()
        var recipeContent = getRecipeContent();
        var recipeObject = { name: recipeName, tags: recipeTags, content: recipeContent, originalAddress: originalAddress }
        if (_.isEmpty(recipeId)) {
            recipeService.createNewRecipe(recipeObject, function() { console.log("Successfully created") })
        }
        else {
            recipeObject.id = recipeId
            recipeService.saveRecipe(recipeObject, function() { console.log("Successfully saved") })
        }
    }

    function deleteRecipe() {
        var recipeId = recipeIdField().val()
        if (!_.isEmpty(recipeId)) {
            recipeService.deleteRecipe(recipeId, function() { console.log("Successfully deleted"); start() })
        }
    }

    return {
        switchToEditRecipeView: switchToEditRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        saveRecipe: saveRecipe,
        deleteRecipe: deleteRecipe,
        initEvents: initEvents,
        start: start
    }
})();