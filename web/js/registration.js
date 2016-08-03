/* jshint -W100 */
(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var REGISTRATION_FORM_ID = "#registrationForm";

    // Override the submit button redirect
    $(REGISTRATION_FORM_ID).submit(function () {
        return false;
    });

    var onRegSuccess = function (response) {
        registrationForm.spinnerVisibility = false;
        alert("Registration successful!");
    };

    var onRegError = function (response) {
        this.spinnerVisibility = false;
        alert("Registration failed.");
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
        api.userRegistration(this.email, this.password, this.securityInfo, onRegSuccess, onRegError.bind(this));
    };

    Vue.validator('email', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });
    Vue.validator('passwordConfirmation', function (val) {
        if (registrationForm){
            return (registrationForm.password === val);
        } else {
            return false;
        }
    });

    var onValid = function() {
        if (this.$registrationFormValidator.touched) {
            $("#submitButton").prop("disabled", false);
        }
    };

    var onInvalid = function(element) {
        $("#submitButton").prop("disabled", true);
    };

    var registrationForm = new Vue({
        el: REGISTRATION_FORM_ID,
        components: {
            'ClipLoader': VueSpinner.ClipLoader
        },
        data: {
            firstName: "",
            lastName: "",
            phoneNumber: "",
            email: "",
            password: "",
            passwordConfirmation: "",
            securityInfo: {
                question: "",
                answer: ""
            },
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
