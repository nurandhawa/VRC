/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();

    var matchData =  [];

    var matches = new Matches(matchData);

    var editFunction = function() {
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

    var userRole = Cookies.get("userRole");

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction, userRole);

    var api = new API();
    api.getMatches(api.gameSession.LATEST, function (response) {
        matches.updateMatches(response, api.gameSession.LATEST);
        header.updateHeader.call(header.component, response.dateCreated);
    });

    api.getMatches(api.gameSession.PREVIOUS, function (response) {
        matches.updateMatches(response, api.gameSession.PREVIOUS);
    });
})();
