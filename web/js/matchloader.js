/**
 * Created by David on 2016-06-22.
 */
(function () {
    "use strict";

    $.material.init();


    var matchData =  [];

    var latestMatches = new Matches(matchData, "#matches-latest");
    var previousMatches = new Matches(matchData, "#matches-previous");

    var tabs = new Vue({
      el: "#tabs",
      data: {
        activeTab: 'latest'
      },
      methods: {
        setActive: function(tabClicked) {
          this.activeTab = tabClicked;
        }
      }
    });

    var editFunction = function() {
        latestMatches.changeMode.call(latestMatches.component);
        previousMatches.changeMode.call(previousMatches.component);
    };

    var saveResultsButton = new Vue({
        el: "#reorderLadderButton",
        data: function() {
            return { disabled: !matches.component.allDone };
        },
        methods: {
            saveResults: function() {
                var api = new API();
                api.reorderLadder(tabs.activeTab);
            }
        },
    });

    matches.component.$watch("allDone", function(newVal, oldVal) {
        if (newVal === true) {
            saveResultsButton.disabled = false;
            $("#reorderLadderButton").prop("disabled", false);
        }
        else {
            saveResultsButton.disabled = true;
            $("#reorderLadderButton").prop("disabled", true);
        }
    });

    var header = new Header("Matches", "Edit Matches", "TBD", editFunction);

    var api = new API();
    api.getMatches(api.gameSession.LATEST, function (response) {
        latestMatches.updateMatches.call(latestMatches.component, response);
        header.updateHeader.call(header.component, response.dateCreated);
    });

    api.getMatches(api.gameSession.PREVIOUS, function (response) {
        previousMatches.updateMatches.call(previousMatches.component, response);
    });
})();
