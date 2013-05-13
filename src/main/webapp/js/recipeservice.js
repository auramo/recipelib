var recipeService = (function() {
    return {
        getRecipes: function(callback) {
            function fetchRecipeCallback(data) {
                callback(JSON.parse(data));
            }
            ajaxGet("" , fetchRecipeCallback, fetchRecipeFailureCallback)
        }
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
    function fetchRecipeFailureCallback(jqXHR) {
        errorService.showError("Kauppalistan hakeminen epäonnistui", jqXHR.responseText);
    }
})();
