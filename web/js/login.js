(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var LOGIN_FORM_ID = "#loginForm";

    // Override the submit button redirect
    $(LOGIN_FORM_ID).submit(function () {
        return false;
    });

    var onLoggedIn = function(response) {
        Cookies.set('sessionToken', response.sessionToken);
        Cookies.set('userRole', response.userRole);
        window.location.href = "/";
    };

    var onLoginError = function (response) {
        this.spinnerVisibility = false;
        if (response.status == 401) {
            this.invalidCredentials = true;
            $("#inputEmail").parent().addClass("has-error");
            $("#inputEmail").focus();
        }
    };

    var onEmailChange = function () {
        this.invalidCredentials = false;
    };

    var onSubmit = function(event) {
        this.spinnerVisibility = true;
        var api = new API();
        api.userLogin(this.email, this.password, this.remember, onLoggedIn, onLoginError.bind(this));
    };

    Vue.validator('email', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });

    var onValid = function() {
        if (this.$loginFormValidator.touched) {
            $("#submitButton").prop("disabled", false);
        }
    };

    var onInvalid = function(element) {
        $("#submitButton").prop("disabled", true);
    };

    var loginForm = new Vue({
        el: LOGIN_FORM_ID,
        bind: {
            onLoginError: onLoginError
        },
        components: {
            'ClipLoader': VueSpinner.ClipLoader
        },
        data: {
            email: "",
            password: "",
            remember: false,
            color: '#03a9f4',
            spinnerVisibility: false,
            invalidCredentials: false
        },
        methods: {
            onSubmit: onSubmit,
            onValid: onValid,
            onInvalid: onInvalid,
            onEmailChange: onEmailChange
        }
    });

})();
