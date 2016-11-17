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

    var onDelSuccess = function (response) {
        this.spinnerVisibility = false;
        alert("Account successfully deleted!");
    };

    var onDelError = function (response) {
        this.spinnerVisibility = false;
        var failureMessage = "Failed to delete the account.\n";
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
        if (this._vm.administrator || this._vm.existingPlayer) {
            return true;
        }
    });

    Vue.validator('deletePlayer', function (val) {
        if (this._vm.deletePlayer) {
            return true;
        }
    });

    Vue.validator('editEmail', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });

    Vue.validator('editPasswordConfirmation', function (val) {
        return (this._vm.editPassword === val);
    });

    Vue.validator('editMinLength', function (val) {
        return /^.{6,}$/.test(val);
    });

    Vue.validator('minLength', function (val) {
        return /^.{6,}$/.test(val);
    });

    Vue.validator('alpha', function (val) {
        return /^[A-z]+$/.test(val);
    });

    // Same as alpha, but empty string also returns true
    Vue.validator('alphaEmpty', function (val) {
        return /^[A-z]+$|^$/.test(val);
    });

    var onValid = function() {
        if (this.$registrationFormValidator.touched) {
            $("#submitButton").prop("disabled", false);
        }
    };

    var onInvalid = function(element) {
        $("#submitButton").prop("disabled", true);
    };

    var showCreateAccountDiv = function() {
        document.getElementById("createAccountDiv").style.display = "block";
        document.getElementById("removeAccountDiv").style.display = "none";
        document.getElementById("editPlayerDiv").style.display = "none";
    };

    var showRemoveAccountDiv = function() {
        document.getElementById("removeAccountDiv").style.display = "block";
        document.getElementById("createAccountDiv").style.display = "none";
        document.getElementById("editPlayerDiv").style.display = "none";
    };

    var editPlayerInfo = function() {
        document.getElementById("removeAccountDiv").style.display = "none";
        document.getElementById("createAccountDiv").style.display = "none";
        document.getElementById("editPlayerDiv").style.display = "block";
    };

    var onDelete = function () {
        this.spinnerVisibility = true;
        var api = new API();
        api.deleteUser(this.deletePlayer.id, onDelSuccess.bind(this), onDelError.bind(this));
    };

    var onEditPlayer = function () {
        console.log("HELLO");
        console.log(this.firstName + this.lastName + this.email + this.password);
        var api = new API();
    };

    var onChange = function () {
        console.log("HELLO");
    };

    document.getElementById("editPlayer").onChange = function () {
        console.log("HEELLO");
    };

    var api = new API();
    api.getPlayers(function (playerData) {
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
                editEmail: "",
                editPassword: "",
                editPasswordConfirmation: "",
                email: "",
                password: "",
                passwordConfirmation: "",
                securityInfo: {
                    question: "",
                    answer: ""
                },
                existingPlayer: "",
                deletePlayer: "",
                editPlayer: "",
                existingPlayers: playerData.danglingPlayers,
                users: playerData.playersWithAccounts,
                color: '#03a9f4',
                spinnerVisibility: false,
                invalidCredentials: false
            },
            methods: {
                onSubmit: onSubmit,
                onValid: onValid,
                onInvalid: onInvalid,
                onEmailChange: onEmailChange,
                onDelete: onDelete,
                onEditPlayer: onEditPlayer,
                onChange: onChange,
                showRemoveAccountDiv: showRemoveAccountDiv,
                showCreateAccountDiv: showCreateAccountDiv,
                editPlayerInfo: editPlayerInfo
            },
            watch: {
                "existingPlayer": function (newVal, oldVal) {
                    this.$validate();
                }
            }
        });
    });

})();
