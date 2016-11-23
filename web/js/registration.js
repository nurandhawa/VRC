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

    var onEditSuccess = function (response) {
        this.spinnerVisibility = false;
        alert("Player info successfully modified!");
    };

    var onEditError = function (response) {
        this.spinnerVisibility = false;
        var failureMessage = "Failed to modify the player info.\n";
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
        var isValid = /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$|^$/.test(val);
        if (isValid) {
            $("#submitButtonEdit").prop("disabled", false);
            return true;
        }
        $("#submitButtonEdit").prop("disabled", true);
        return false;
    });

    Vue.validator('editPasswordConfirmation', function (val) {
        var isValid =  (this._vm.editPassword === val);
        if (isValid) {
            $("#submitButtonEdit").prop("disabled", false);
            return true;
        }
        $("#submitButtonEdit").prop("disabled", true);
        return false;
    });

    Vue.validator('editMinLength', function (val) {
        var isValid =  /^.{6,}$|^$/.test(val);
        if (isValid) {
            $("#submitButtonEdit").prop("disabled", false);
            return true;
        }
        $("#submitButtonEdit").prop("disabled", true);
        return false;
    });

    Vue.validator('minLength', function (val) {
        return /^.{6,}$/.test(val);
    });

    Vue.validator('alpha', function (val) {
        return /^[A-z]+$/.test(val);
    });

    // Same as alpha, but empty string also returns true
    Vue.validator('alphaEmpty', function (val) {
        var isValid = /^[A-z]+$|^$/.test(val);
        if (isValid) {
            $("#submitButtonEdit").prop("disabled", false);
            return true;
        }
        $("#submitButtonEdit").prop("disabled", true);
        return false;
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
        this.spinnerVisibility = true;
        var api = new API();
        api.editPlayer(this.editEmail, this.editPassword, this.firstName, this.lastName, 
                        this.editPlayer.id, onEditSuccess.bind(this), onEditError.bind(this));
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
                allPlayers: playerData.allPlayers,
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
                showRemoveAccountDiv: showRemoveAccountDiv,
                showCreateAccountDiv: showCreateAccountDiv,
                editPlayerInfo: editPlayerInfo
            },
            watch: {
                "existingPlayer": function (newVal, oldVal) {
                    this.$validate();
                },
                "editPlayer": function () {
                    if (this.editPlayer) {
                        $("#submitButtonEdit").prop("disabled", true);
                        var name = (this.editPlayer.label).split(" ");
                        this.editEmail = this.editPlayer.email;
                        this.firstName = name[0];
                        this.lastName = name[1];
                        this.editPassword = "";
                        this.editPasswordConfirmation = "";
                    } else {
                        this.editEmail = null;
                        this.firstName = "";
                        this.lastName = "";
                        this.editPassword = "";
                        this.editPasswordConfirmation = "";
                        $("#submitButtonEdit").prop("disabled", false);
                    }

                    if (this.editEmail) {
                        $("#editEmail").prop("disabled", true);
                        $("#editPassword").prop("disabled", true);
                        $("#editPasswordConfirmation").prop("disabled", true);
                    } else {
                        $("#editEmail").prop("disabled", false);
                        $("#editPassword").prop("disabled", false);
                        $("#editPasswordConfirmation").prop("disabled", false);
                    }
                }
            }
        });
    });

})();
