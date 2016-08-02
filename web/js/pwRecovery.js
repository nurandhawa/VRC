(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var FORGOT_PASS_HTML = '<button class="btn-link" id="forgotPassword">Forgot your password?</button>';

    var PASS_RECOVERY_FORM_ID = "#recoveryForm";

    // Override the submit button redirect
    $(PASS_RECOVERY_FORM_ID).submit(function () {
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

    var onEmailSubmit = function() {
        recoveryForm.emailRetrieved = true;
        $("#submitEmailButton").prop("disabled", true);
    };

    var onSubmit = function() {
        alert("This functionality is not yet connected. The spinner will spin forever.");
        this.spinnerVisibility = true;
    };

    Vue.validator('email', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });

    var onValid = function() {
        if (this.$emailValidator.touched && !recoveryForm.emailRetrieved) {
            $("#submitEmailButton").prop("disabled", false);
        } else if (this.$securityValidator.touched && recoveryForm.emailRetrieved) {
            $("#submitAnswerButton").prop("disabled", false);
        }
    };

    var onInvalid = function() {
        if (!recoveryForm.emailRetrieved) {
            $("#submitEmailButton").prop("disabled", true);
        } else {
            $("#submitAnswerButton").prop("disabled", true);
        }
    };

    var forgotPassButton;

    forgotPassButton = Vue.extend({
        template: FORGOT_PASS_HTML
    });
    Vue.component("forgot-pass-modal", forgotPassButton);

    var recoveryForm = new Vue({
        el: PASS_RECOVERY_FORM_ID,
        bind: {
            onLoginError: onLoginError
        },
        components: {
            'ClipLoader': VueSpinner.ClipLoader
        },
        data: {
            email: "",
            password: "",
            emailRetrieved: false,
            remember: false,
            color: '#03a9f4',
            spinnerVisibility: false,
            invalidCredentials: false
        },
        methods: {
            onSubmit: onSubmit,
            onEmailSubmit: onEmailSubmit,
            onValid: onValid,
            onInvalid: onInvalid,
            onEmailChange: onEmailChange,
            openModal: function () {
                console.log("Hey!");
                $("#forgPassModal").modal("show");
            }
        }
    });


})();
