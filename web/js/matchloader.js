/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();

    var matchData =  [];

    var matches = new Matches(matchData);

    var editFunction = function() {
        matches.changeMode.call(matches.component);
    };

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

    var saveResultsButton = Vue.extend({
        template: '<a v-on:click="saveResults()" class="btn btn-raised btn-success header-button">Save Results</a>',
        methods: {
            saveResults: function() {
                var api = new API();
                api.reorderLadder(api.gameSession.LATEST);
            }
        },
        parent: header.component
    });
    header.addButton(saveResultsButton);

    var api = new API();
    api.getMatches(api.gameSession.LATEST, function (response) {
        matches.updateMatches.call(matches.component, response);
        header.updateHeader.call(header.component, response.dateCreated);
    });
})();
