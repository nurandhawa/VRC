(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var setResponseWithSessionId = function(response) {
        Cookies.set('sessionToken', "");
        window.location.href = "/";
    };

    var onLogout = function() {
        var api = new API();
        api.userLogout(setResponseWithSessionId);
    };

    document.getElementById("logout_nav").onclick = onLogout;
})();
