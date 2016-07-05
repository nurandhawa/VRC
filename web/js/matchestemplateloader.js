(function () {
    "use strict";

    var api = new API();

    api.getMatches(function(matchData) {
        var matchCardsComponent = new Vue({
            el: '#matchcards',
            data: {
                matches: matchData
            }
        });
    });

})();
