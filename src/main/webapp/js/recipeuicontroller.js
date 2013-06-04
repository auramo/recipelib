var recipeUiController = (function() {

    var cachedRecipes = []

    function recipeIdEditField() {
        return $('input[name="recipe-id"]')
    }

    function recipeNameEditField() {
        return $('input[name=recipe-name]')
    }

    function recipeTagsEditField() {
        return $('input[name=recipe-tags]')
    }

    function recipeOriginalAddressEditField() {
        return $('input[name=original-address]')
    }

    function recipeContentEditField() {
        return nicEditors.findEditor('recipe-content')
    }

    function recipientContentValue() {
        function value() {
            return nicEditors.findEditor('recipe-content').getContent()
        }
        return $('.nicEdit-main').asEventStream('keyup').map(value).log().toProperty(value())
    }

    function getRecipeContent() {
        return recipeContentEditField().getContent()
    }

    function splitTags(recipe) {
        return _(recipe.tags).join(" ")
    }

    function fillEditRecipeFields(recipe) {
        recipeIdEditField().val(recipe.id)
        recipeNameEditField().val(recipe.name)
        recipeTagsEditField().val(splitTags(recipe))
        recipeOriginalAddressEditField().val(recipe.originalAddress)
        recipeContentEditField().setContent(recipe.content)
    }

    function fillShowRecipeFields(recipe) {
        $('.recipe-name-show').html(recipe.name)
        $('.recipe-tags-show').html(splitTags(recipe))
        $('.recipe-original-address-show').html(recipe.originalAddress)
        $('.recipe-content-show').html(recipe.content)
    }

    function switchToShowRecipeView(recipeId) {
        recipeService.getRecipe(recipeId, fillShowRecipeFields)
        $('.edit-button').click(function() { switchToEditRecipeView(recipeId)} )
        $('.show-recipe').show()
        $('.recipe-list').hide()
        $('.new-recipe').hide()
    }

    function switchToEditRecipeView(recipeId) {
        if (recipeId) {
            console.log("Switching to edit view with id: " + recipeId)
            recipeService.getRecipe(recipeId, fillEditRecipeFields)
            $(".save-button").removeAttr("disabled")
            $(".delete-button").removeAttr("disabled")
        } else {
            fillEditRecipeFields({name: "", tags: "", originalAddress: "", content: ""})
            $(".save-button").attr("disabled", "disabled")
            $(".delete-button").attr("disabled", "disabled")
        }
        $('.new-recipe').show()
        $('.recipe-list').hide()
        $('.show-recipe').hide()
    }

    function initEvents() {
        function nonEmpty(x) { return x.length > 0 && x !== '<br>' }
        var recipeNameEntered = Bacon.UI.textFieldValue(recipeNameEditField()).map(nonEmpty)
        var recipeContentEntered = recipientContentValue().map(nonEmpty)
        var buttonEnabled = recipeNameEntered.and(recipeContentEntered)//.and(recipeTagsEntered)
        buttonEnabled.not().onValue($(".save-button"), "attr", "disabled")
        Bacon.UI.textFieldValue($('.search-recipes')).debounce(400).onValue(search)
    }

    function search(searchString) {
        searchString = searchString.toLowerCase()
        function matchRecipe(recipe) { return _.contains(recipe.name.toLowerCase(), searchString) || _.contains(recipe.tags, searchString) }
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
            return '<tr class="recipe-list-row" id="recipe-' +
                recipe.id +
                '"><td class="recipe-list-row-name"><a href="#" onclick="recipeUiController.switchToShowRecipeView(\'' +
                recipe.id +
                '\')">' +
                recipe.name +
                '</a></td><td>' +
                _(recipe.tags).join(" ") +
                '</td></tr>';
        }

        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return recipeListRow(recipe); }).reduce(function (r1, r2) { return r1 + r2 })
        }
        $('.new-recipe').hide()
        $('.show-recipe').hide()
        $('.recipe-list').show()
        $('.recipe-list tbody').html(getRecipeListHtml())
    }

    function saveRecipe() {
        var recipeId = recipeIdEditField().val()
        var recipeName = recipeNameEditField().val()
        var recipeTags = recipeTagsEditField().val()
        var originalAddress = recipeOriginalAddressEditField().val()
        var recipeContent = getRecipeContent();
        var recipeObject = { name: recipeName, tags: recipeTags.split(" "), content: recipeContent, originalAddress: originalAddress }
        if (_.isEmpty(recipeId)) {
            recipeService.createNewRecipe(recipeObject, function(newRecipeId) { switchToShowRecipeView(newRecipeId) })
        }
        else {
            recipeObject.id = recipeId
            recipeService.saveRecipe(recipeObject, function(savedRecipeId) { switchToShowRecipeView(savedRecipeId) })
        }
    }

    function deleteRecipe() {
        var recipeId = recipeIdEditField().val()
        if (!_.isEmpty(recipeId)) {
            recipeService.deleteRecipe(recipeId, function() { console.log("Successfully deleted"); start() })
        }
    }

    return {
        switchToEditRecipeView: switchToEditRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        switchToShowRecipeView: switchToShowRecipeView,
        saveRecipe: saveRecipe,
        deleteRecipe: deleteRecipe,
        initEvents: initEvents,
        start: start
    }
})();