(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var onLoggedIn = function(response) {
        Cookies.set('sessionToken', response.sessionToken);
        window.location.href = "/index.html";
    };

    var onLogout = function() {
        var api = new API();
        api.userLogout(onLoggedIn);
    };

    document.getElementById("logout_nav").onclick = onLogout();
})();

