(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    Vue.validator('position', function (val) {
        return /^[1-9][0-9]*$|^$/.test(val);
    });

    Vue.validator('alpha', function (val) {
        return /^[A-z]+$/.test(val);
    });

    // Same as alpha, but empty string also returns true
    Vue.validator('alphaEmpty', function (val) {
        return /^[A-z]+$|^$/.test(val);
    });

    Vue.validator('phone', function (val) {
        return /^(\+\d{1,2}\s)?\(?\d{3}\)?[\s.-]?\d{3}[\s.-]?\d{4}$|^$/.test(val);
    });

    var ladderData = {
        pairs: [],
        players: [],
        timeStamp: ""
    };

    var ladder = new Ladder(ladderData);

    var editFunction = function() {
        ladder.changeMode.call(ladder.component);
    };

    var userRole = Cookies.get("userRole");

    var navbar = new Navbar(userRole);

    var header = new Header("Ladder", "Edit Ladder", ladderData.timeStamp, editFunction, userRole);
    ladder.component.$watch("timeStamp", function (newVal, oldVal) {
        header.updateHeader.call(header.component, newVal);
    });

    var addPairButton = Vue.extend({
        template: '<a v-on:click="addPair()" class="btn btn-raised btn-success header-button">Add Pair</a>',
        methods: {
            addPair: function() {
                $("#addPairModal").modal("show");
            }
        },
        parent: header.component
    });
    header.addButton(addPairButton);

    var api = new API();
    api.getLadder(function(response) {
        ladder.updateLadder.call(ladder.component, response);
        header.updateHeader.call(header.component, response.timeStamp);
    });

})();
