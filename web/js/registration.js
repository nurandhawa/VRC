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
        this.spinnerVisibility = false;
        alert("Registration successful!");
    };

    var onRegError = function (response) {
        this.spinnerVisibility = false;
        var failureMessage = "Registration failed.\n";

        if (response.status == 401) {
            this.invalidCredentials = true;
            $("#inputEmail").parent().addClass("has-error");
            $("#inputEmail").focus();
        }
        var responseBody = JSON.parse(response.responseText);
        failureMessage += responseBody.message;
        alert(failureMessage);
    };

    var onEmailChange = function () {
        this.invalidCredentials = false;
    };

    var onSubmit = function(event) {
        this.spinnerVisibility = true;
        var api = new API();
        if (this.administrator) {
            var invalidPlayerId = -1;
            api.userRegistration(this.email, this.password, this.securityInfo,
                invalidPlayerId, onRegSuccess.bind(this), onRegError.bind(this));
        } else {
            api.userRegistration(this.email, this.password, this.securityInfo,
                this.existingPlayer.id, onRegSuccess.bind(this), onRegError.bind(this));
        }
    };

    Vue.validator('email', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });

    Vue.validator('passwordConfirmation', function (val) {
        return (this._vm.password === val);
    });

    Vue.validator('existingPlayer', function (val) {
        if (this._vm.existingPlayer) {
            return true;
        }
    });

    Vue.validator('minLength', function (val) {
        return /^.{6,}$/.test(val);
    });

    var onValid = function() {
        if (this.$registrationFormValidator.touched) {
            $("#submitButton").prop("disabled", false);
        }
    };

    var onInvalid = function(element) {
        $("#submitButton").prop("disabled", true);
    };

    var api = new API();
    api.getLadder(function (ladderData) {
        var registrationForm = new Vue({
            el: REGISTRATION_FORM_ID,
            components: {
                'ClipLoader': VueSpinner.ClipLoader,
                'v-select': VueSelect.VueSelect
            },
            data: {
                administrator: false,
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
                existingPlayer: "",
                existingPlayers: ladderData.players,
                color: '#03a9f4',
                spinnerVisibility: false,
                invalidCredentials: false
            },
            methods: {
                onSubmit: onSubmit,
                onValid: onValid,
                onInvalid: onInvalid,
                onEmailChange: onEmailChange
            },
            watch: {
                "existingPlayer": function (newVal, oldVal) {
                    this.$validate();
                }
            }
        });
    });

})();
