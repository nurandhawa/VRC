(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var PASS_RECOVERY_FORM_ID = "#recoveryForm";

    // Override the submit button redirect
    $(PASS_RECOVERY_FORM_ID).submit(function () {
        return false;
    });

    var onEmailSubmit = function() {
        $("#submitEmailButton").prop("disabled", true);
        this.spinnerVisibilityTop = true;
        var api = new API();
        api.getUserSecurityQuestion(this.email, onGotQuestion.bind(this), onGotQuestionError.bind(this));
    };

    var onGotQuestion = function (response){
        recoveryForm.emailRetrieved = true;
        this.spinnerVisibilityTop = false;
        recoveryForm.securityQuestion = response.securityQuestion;
    };

    var onGotQuestionError = function (response) {
        this.spinnerVisibilityTop = false;
        if (response.status == 401) {
            this.invalidCredentials = true;
            $("#inputEmail").parent().addClass("has-error");
            $("#inputEmail").focus();
        }
    };

    var onAnswerSubmit = function() {
        this.spinnerVisibilityMid = true;
        var api = new API();
        api.answerSecurityQuestion(this.email, this.securityAnswer, onAnsweredQuestion.bind(this), onAnsweredQuestionError.bind(this));
    };

    var onAnsweredQuestion = function (response) {
        this.spinnerVisibilityMid = false;
        recoveryForm.questionAnswered = true;
        this.voucherCode = response.voucherCode;
    };

     var onAnsweredQuestionError = function (response) {
         this.spinnerVisibilityMid = false;
         if (response.status == 401) {
             this.invalidCredentials = true;
             $("#inputSecurityAnswer").parent().addClass("has-error");
             $("#inputSecurityAnswer").focus();
         }
     };

     var onPasswordSubmit = function () {
        if (this.password === this.passwordConfirm){
            this.spinnerVisibilityBottom = true;
            var api = new API();
            api.changePassword(this.email, this.voucherCode, this.password, onChangedPassword.bind(this), onChangedPasswordError.bind(this));
        } else {
            $("#submitPasswordButton").prop("disabled", true);
            alert("Passwords must match.");
        }
     };

     var onChangedPassword = function () {
         this.spinnerVisibilityBottom = false;
         alert("Password successfully changed! Please log in.");
     };

     var onChangedPasswordError = function (response) {
         this.spinnerVisibilityBottom = false;
         if (response.status == 401) {
             this.invalidCredentials = true;
             $("#inputPassword").parent().addClass("has-error");
             $("#inputPassword").focus();
         }
     };

    Vue.validator('email', function (val) {
        return /^(([^<>()[\]\\.,;:\s@\"]+(\.[^<>()[\]\\.,;:\s@\"]+)*)|(\".+\"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/.test(val);
    });

    Vue.validator('passwordConfirm', function (val) {
        if (recoveryForm){
            return (recoveryForm.password === val);
        } else {
            return false;
        }
    });

    Vue.validator('minLength', function (val) {
        return /^.{6,}$/.test(val);
    });

    var onValid = function() {
        if (this.$emailValidator.touched && !recoveryForm.emailRetrieved && !recoveryForm.questionAnswered) {
            $("#submitEmailButton").prop("disabled", false);
        } else if (this.$passwordValidator.touched && recoveryForm.emailRetrieved && !recoveryForm.questionAnswered) {
            $("#submitAnswerButton").prop("disabled", false);
        } else if (this.$passwordValidator.touched && recoveryForm.emailRetrieved && recoveryForm.questionAnswered) {
            $("#submitPasswordButton").prop("disabled", false);
        }
    };

    var onInvalid = function() {
        if (!recoveryForm.emailRetrieved) {
            $("#submitEmailButton").prop("disabled", true);
        } else {
            $("#submitAnswerButton").prop("disabled", true);
        }
    };

    var recoveryForm = new Vue({
        el: PASS_RECOVERY_FORM_ID,
        components: {
            'ClipLoader': VueSpinner.ClipLoader
        },
        data: {
            email: "",
            password: "",
            passwordConfirm: "",
            securityQuestion: "",
            securityAnswer: "",
            voucherCode: "",
            emailRetrieved: false,
            questionAnswered: false,
            color: '#03a9f4',
            spinnerVisibilityTop: false,
            spinnerVisibilityMid: false,
            spinnerVisibilityBottom: false
        },
        methods: {
            onAnswerSubmit: onAnswerSubmit,
            onEmailSubmit: onEmailSubmit,
            onValid: onValid,
            onInvalid: onInvalid,
            onPasswordSubmit: onPasswordSubmit
        }
    });


})();
