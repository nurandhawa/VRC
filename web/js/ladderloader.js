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

    var api = new API();
    var ladderData = {
        pairs: [],
        dateCreated: ""
    };

    var ladder = new Ladder(ladderData.pairs);

    var editFunction = function() {
        ladder.changeMode.call(ladder.component);
    };

    var header = new Header("Ladder", "Edit Ladder", ladderData.dateCreated, editFunction);

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
        header.updateHeader.call(header.component, response.dateCreated);
    });

})();
