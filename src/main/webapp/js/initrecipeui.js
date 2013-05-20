$(document).ready(function() {
    nicEditors.allTextAreas()
    $('.new-recipe').hide()
    $('.recipe-list').hide()
    recipeUiController.initEvents()
    recipeUiController.start()
});