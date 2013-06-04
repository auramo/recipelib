var recipeService = (function() {
    return {
        getRecipes: function(callback) {
            function fetchRecipeCallback(data) {
                console.log("before parsing")
                var parsed = JSON.parse(data)
                console.log("parsed")
                console.log(parsed)
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
            console.log("savingthis:")
            console.log(jsonRecipeObject)
            ajaxSendData("PUT", recipe.id, jsonRecipeObject, parseIdAndCall(successCallback), postRecipeFailureCallback)
        },
        deleteRecipe: function(recipeId, successCallback) {
            console.log("deleting this:")
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
                console.log("success")
                console.log("data")
                console.log(data)
                successCallback(data)
            },
            error:function(jqXHR, status, msg){
                console.log("Eror occurred. Status:")
                console.log(jqXHR.status)
                console.log(status)
                console.log("Response contents")
                console.log(jqXHR.responseText)
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
                console.log("success")
                console.log("data")
                console.log(data)
                successCallback(data)
            },
            error: function(jqXHR, status, msg){
                console.log("Eror occurred. Status:")
                console.log(jqXHR.status)
                console.log(status)
                console.log("Response contents")
                console.log(jqXHR.responseText)
                if (jqXHR.status == 401) document.location.reload();
                else failureCallback(jqXHR, status, msg);
            }
        });
    }

    function parseIdAndCall(idCallback) {
        return function(response) {
            console.log("response")
            console.log(response)
            //var msg = JSON.parse(response)
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
