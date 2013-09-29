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

    function getRecipeContent() {
        return recipeContentEditField().getContent()
    }

    function splitTags(recipe) {
        return _(recipe.tags).join(" ")
    }

    function fillShowRecipeFields(recipe) {
        $('.recipe-name-show').html(recipe.name)
        $('.recipe-tags-show').html(splitTags(recipe))
        $('.recipe-original-address-show').attr("href", recipe.originalAddress)
        $('.recipe-content-show').html(recipe.content)
        $('.loader').hide()
        $('.show-recipe').show()
    }

    function switchToShowRecipeView(recipeId) {
        recipeService.getRecipe(recipeId, fillShowRecipeFields)
        $('.edit-button').unbind('click')
        $('.edit-button').click(function() { switchToView('show_recipe', 'edit_recipe', recipeId); } )
        $('.recipe-list').hide()
        $('.new-recipe').hide()
        $('.loader').show()
    }

    function fillEditRecipeFields(recipe) {
        $('.loader').hide()
        $('.new-recipe').show()
        recipeIdEditField().val(recipe.id)
        recipeNameEditField().val(recipe.name)
        recipeTagsEditField().val(splitTags(recipe))
        recipeOriginalAddressEditField().val(recipe.originalAddress)
        recipeContentEditField().setContent(recipe.content)
    }

    function switchToEditRecipeView(recipeId) {
        hideAllViews()
        $('.loader').show()
        if (recipeId) {
            recipeService.getRecipe(recipeId, fillEditRecipeFields)
            $(".save-button").removeAttr("disabled")
            $(".delete-button").removeAttr("disabled")
        } else {
            fillEditRecipeFields({name: "", tags: "", originalAddress: "", content: ""})
            $(".save-button").attr("disabled", "disabled")
            $(".delete-button").attr("disabled", "disabled")
        }
    }

    function initEvents() {
        function nonEmpty(x) { return x.length > 0 && x !== '<br>' }
        var recipeNameEntered = Bacon.UI.textFieldValue(recipeNameEditField()).map(nonEmpty)
        var buttonEnabled = recipeNameEntered
        buttonEnabled.not().onValue($(".save-button"), "attr", "disabled")
        Bacon.UI.textFieldValue($('.search-recipes')).debounce(400).onValue(search)

        window.onpopstate = function(event) {
            if (event.state !== null) {
                goToNextView(event.state);
            }
        };
    }

    function goToNextView(state) {
        var switchTable =
            {
                list_recipes: function() { switchToRecipeListView(cachedRecipes); },
                edit_recipe: function() { if (state.id) switchToEditRecipeView(state.id); },
                show_recipe: function() { if (state.id) switchToShowRecipeView(state.id); }
            }
        var switchFunc = switchTable[state.page];
        if (switchFunc) switchFunc();
    }

    function search(searchString) {
        searchString = searchString.toLowerCase()
        function matchRecipe(recipe) { return _.contains(recipe.name.toLowerCase(), searchString) || _.contains(recipe.tags, searchString) }
        var found = _(cachedRecipes).filter(matchRecipe).map(function(filtered) { return "recipe-" + filtered.id }).value()
        var rows = $('.recipe-list-row')
        rows.filter(function() { return !_.contains(found, this.id) }).hide()
        rows.filter(function() { return _.contains(found, this.id) }).show()
    }

    function hideAllViews() {
        $('.loader').hide()
        $('.new-recipe').hide()
        $('.recipe-list').hide()
        $('.show-recipe').hide()
    }

    function start() {
        hideAllViews()
        $('.loader').show()
        recipeService.getRecipes(function(result) {
            cachedRecipes = result.recipes
            if (_.isEmpty(result.recipes)) {
                switchToEditRecipeView()
            } else {
                switchToRecipeListView(result.recipes)
            }
        });
    }

    function switchToView(currentPageId, nextPageId, recipeId) {
        history.pushState({page: currentPageId}, null, null);
        goToNextView({page: nextPageId, id: recipeId});
    } 

    function switchToRecipeListView(recipes) {
        function recipeListRow(recipe) {
            return '<tr class="recipe-list-row" id="recipe-' +
                recipe.id +
                '"><td class="recipe-list-row-name"><a href="#" onclick="recipeUiController.switchToView(\'list_recipes\', \'show_recipe\', \'' +
                recipe.id +
                '\'); return false;">' +
                recipe.name +
                '</a></td><td>' +
                _(recipe.tags).join(" ") +
                '</td></tr>';
        }

        function getRecipeListHtml() {
            return _(recipes).map(function(recipe) { return recipeListRow(recipe); }).reduce(function (r1, r2) { return r1 + r2 })
        }
        $('.loader').hide()
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
            recipeService.deleteRecipe(recipeId, function() { start() })
        }
    }

    return {
        switchToEditRecipeView: switchToEditRecipeView,
        switchToRecipeListView: switchToRecipeListView,
        switchToShowRecipeView: switchToShowRecipeView,
	    switchToView: switchToView,
        saveRecipe: saveRecipe,
        deleteRecipe: deleteRecipe,
        initEvents: initEvents,
        start: start
    }
})();
