(function () {
    "use strict";

    $.material.init();

    var api = new API();

    api.getLadder(function(ladderData) {

        var ladder = new Ladder(ladderData);

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
    });

})();
