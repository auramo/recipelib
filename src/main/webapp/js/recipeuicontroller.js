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
        $('.edit-button-form').attr('action', '#pageId=editRecipeView&recipeId=' + recipeId);
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
        initHashChangeStream()
        hideAllViews()
        navigateTo('mainView')
        showMainView()
    }

    function navigateTo(pageId, params) {
        var hash = "pageId=" + pageId// + " value&andsome=othervalue";
        var paramString = _.reduce(params, function(result, val, key) {
            result += "&" + key + "=" + val
            return result;
        }, "");
        hash += paramString
        window.location.hash = hash
    }

    var naviFunctions = {
        'mainView': showMainView,
        'newView': showNewView,
        'editRecipeView': showEditRecipeView,
        'showRecipeView': showRecipeView
    }

    function showRecipeView(params) {
        switchToShowRecipeView(params.recipeId)
    }

    function showEditRecipeView(params) {
        switchToEditRecipeView(params.recipeId)
    }

    function showNewView() {
        switchToEditRecipeView()
    }

    function showMainView() {
        $('.loader').show()
        recipeService.getRecipes(function (result) {
            cachedRecipes = result.recipes
            if (_.isEmpty(result.recipes)) {
                switchToEditRecipeView()
            } else {
                switchToRecipeListView(result.recipes)
            }
        });
    }

    function hashChangeReactor(params) {
        var naviFunction = naviFunctions[params.pageId]
        if (naviFunction) naviFunction(params)
    }

    function initHashChangeStream() {
        //Change hash programmatically with:
        //window.location.hash = "some=value&andsome=othervalue";
        //http://stackoverflow.com/questions/6174821/how-to-trigger-different-functions-on-hash-change-based-on-hash-value-with-jquer

        var hashChanges = Bacon.fromBinder(function(sink) {
            $(window).on('hashchange', function() { sink(getParams())});
            return $.noop
        });
        hashChanges.onValue(hashChangeReactor)

        function getParams() {
            var hashParameters = {}
            var newParameters = getParameters(event.target.location.hash.substring(1))
            $.each(newParameters, function(key, value) {
                if(hashParameters[key] !== value) $(window).trigger(key + "-change")
                hashParameters[key] = value;
            })
            return hashParameters
        }

        function getParameters(paramString) {
            //taken from http://stackoverflow.com/questions/4197591/parsing-url-hash-fragment-identifier-with-javascript/4198132#4198132
            var space = /\+/g
            var keyValRegex = /([^&;=]+)=?([^&;]*)/g
            var decode = function (str) { return decodeURIComponent(str.replace(space, " ")) }
            var keyVal
            var params = {}
            while (keyVal = keyValRegex.exec(paramString))
                params[decode(keyVal[1])] = decode(keyVal[2])
            return params
        }
    }

    function switchToView(currentPageId, nextPageId, recipeId) {
        goToNextView({page: nextPageId, id: recipeId});
    } 

    function switchToRecipeListView(recipes) {
        function recipeListRow(recipe) {
            return '<tr class="recipe-list-row" id="recipe-' +
                recipe.id +
                '"><td class="recipe-list-row-name"><a href="#pageId=showRecipeView&recipeId=' + recipe.id + '">' +
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
            recipeService.createNewRecipe(recipeObject, afterSave)
        }
        else {
            recipeObject.id = recipeId
            recipeService.saveRecipe(recipeObject, afterSave)
        }
        function afterSave(savedId) {
            switchToShowRecipeView(savedId)
            navigateTo('showRecipeView', {recipeId: savedId})
        }
    }

    function deleteRecipe() {
        var recipeId = recipeIdEditField().val()
        if (!_.isEmpty(recipeId)) {
            recipeService.deleteRecipe(recipeId, function() { start(); navigateTo('mainView') })
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
