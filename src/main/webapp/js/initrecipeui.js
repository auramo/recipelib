$(document).ready(function() {
    recipeService.getRecipes(function(result) {
        if (_.isEmpty(result.recipes)) {
            console.log(result)
            console.log("is empty")
            $('.new-recipe').show()
            $('.recipe-list').hide()
        } else {
            console.log("not empty")
            $('.new-recipe').hide()
            $('.recipe-list').show()
        }
    });
});