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

    var reorderLadderButton = Vue.extend({
        template: "#reorderLadderButtonTemplate",
        data: function() {
            return { disabled: !matches.component.allDone };
        },
        methods: {
            saveResults: function() {
                var api = new API();
                api.reorderLadder(api.gameSession.LATEST);
            }
        }
    });
    Vue.component('reorder-ladder-button', reorderLadderButton);

    matches.component.$watch("allDone", function(newVal, oldVal) {
        if (newVal === true) {
            reorderLadderButton.disabled = false;
            $("#reorderLadderButton").prop("disabled", false);
        }
        else {
            reorderLadderButton.disabled = true;
            $("#reorderLadderButton").prop("disabled", true);
        }
    });

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

    var api = new API();
    api.getMatches(api.gameSession.LATEST, function (response) {
        matches.updateMatches.call(matches.component, response);
        header.updateHeader.call(header.component, response.dateCreated);
    });
})();
