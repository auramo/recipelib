var recipeService = (function() {
    return {
        getRecipes: function(callback) {
            function fetchRecipeCallback(data) {
                var parsed = JSON.parse(data)
                callback(parsed);
            }
            ajaxGet("" , fetchRecipeCallback, fetchRecipeFailureCallback)
        },
        getRecipe: function(id, callback) {
            function fetchRecipeDetailsCallback(data) {
                var parsed = JSON.parse(data)
                callback(parsed);
            }
            ajaxGet(id , fetchRecipeDetailsCallback, fetchRecipeFailureCallback)
        },
        createNewRecipe: function(recipe, successCallback) {
            var jsonRecipeObject = JSON.stringify(recipe)
            ajaxSendData("POST", "", jsonRecipeObject, parseIdAndCall(successCallback), postRecipeFailureCallback)
        },
        saveRecipe: function(recipe, successCallback) {
            var jsonRecipeObject = JSON.stringify(recipe)
            ajaxSendData("PUT", recipe.id, jsonRecipeObject, parseIdAndCall(successCallback), postRecipeFailureCallback)
        },
        deleteRecipe: function(recipeId, successCallback) {
            ajaxSendData("DELETE", recipeId, "", successCallback, postRecipeFailureCallback)
        }
    }

    function ajaxSendData(method, path, data, successCallback, failureCallback) {
        $.ajax({
            url: "/recipeapi/" + path,
            type: method,
            data: data,
            contentType: 'application/json',
            dataType: "json",
            success: function(data){
                successCallback(data)
            },
            error:function(jqXHR, status, msg){
                if (jqXHR.status == 401) document.location.reload();
                else failureCallback(jqXHR, status, msg);
            }
        });
    }

    function ajaxGet(path, successCallback, failureCallback) {
        $.ajax({
            url: "/recipeapi/" + path,
            type: "get",
            success: function(data){
                successCallback(data)
            },
            error: function(jqXHR, status, msg){
                if (jqXHR.status == 401) document.location.reload();
                else failureCallback(jqXHR, status, msg);
            }
        });
    }

    function parseIdAndCall(idCallback) {
        return function(response) {
            idCallback(response.id)
        }
    }

    function postRecipeFailureCallback(jqXHR, status) {
        errorService.showError("Kauppalistan lähettäminen epäonnistui", jqXHR.status + " " + status);
    }

    function fetchRecipeFailureCallback(jqXHR, status) {
        errorService.showError("Kauppalistan hakeminen epäonnistui", jqXHR.status + " " + status);
    }
})();
