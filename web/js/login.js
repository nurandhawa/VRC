(function () {
    "use strict";

    $.material.init();

    var LOGIN_FORM_ID = "#loginForm";

    // Override the submit button redirect
    $(LOGIN_FORM_ID).submit(function () {
        return false;
    });

    var onLoggedIn = function(response) {
        Cookies.set('sessionToken', response.sessionToken);
        window.location.href = "/index.html";
    };

    var onSubmit = function(event) {
        var api = new API();
        api.userLogin(this.email, this.password, onLoggedIn);
    };

    var loginForm = new Vue({
        el: LOGIN_FORM_ID,
        data: {
            email: "",
            password: "",
            remember: false
        },
        methods: {
            onSubmit: onSubmit
        }
    });

})();
