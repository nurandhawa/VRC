(function () {
    "use strict";

    var api = new API();

    api.getMatches("latest", function(matchData) {
        var matchCardsComponent = new Vue({
            el: '#matchcards',
            data: {
                matches: matchData
            }
        });
    });

})();
