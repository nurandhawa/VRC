/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();

    var matchData =  [];

    // var latestMatches = new Matches(matchData, "#matchesLatest");
    // var previousMatches = new Matches(matchData, "#matchesPrevious");

    var matches = new Matches(matchData, "#matchesLatest");

    var editFunction = function() {
        // latestMatches.changeMode.call(latestMatches.component);
        // previousMatches.changeMode.call(previousMatches.component);
        matches.changeMode();
    };

    var reorderLadderButton = Vue.extend({
        template: "#reorderLadderButtonTemplate",
        data: function() {
            return {
                disabled: !matches.isAllDone()
            };
        },
        methods: {
            saveResults: function() {
                var api = new API();
                api.reorderLadder(matches.component.activeGameSession);
            }
        }
    });
    Vue.component('reorder-ladder-button', reorderLadderButton);

    // latestMatches.component.$watch("allDone", function(newVal, oldVal) {
        // if (newVal === true) {
        //     reorderLadderButton.disabled = false;
        //     $("#reorderLadderButton").prop("disabled", false);
        // }
        // else {
        //     reorderLadderButton.disabled = true;
        //     $("#reorderLadderButton").prop("disabled", true);
        // }
    // });

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

    var api = new API();
    api.getMatches(api.gameSession.LATEST, function (response) {
        // latestMatches.updateMatches.call(latestMatches.component, response);
        matches.updateMatches(response, api.gameSession.LATEST);
        header.updateHeader.call(header.component, response.dateCreated);
    });

    api.getMatches(api.gameSession.PREVIOUS, function (response) {
        // previousMatches.updateMatches.call(previousMatches.component, response);
        // matches.component.$broadcast("updateMatches", api.gameSession.PREVIOUS);
        matches.updateMatches(response, api.gameSession.PREVIOUS);
    });
})();
