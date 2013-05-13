var errorService = (function() {
    //TODO make a dom element which is shown when error occurs and contains this msg
    return {showError: function(errorMessage, details) { alert(errorMessage + " " + details)}};
})();

