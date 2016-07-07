(function () {
    "use strict";

    $.material.init();

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
