/**
 * Created by David on 2016-06-15.
 */

Vue.component('matches', {
    template: '#matches-template',
    props: {
        active: "active",
        isActive: "isActive",
        matchlist: "matchlist",
        show: {
            type: Boolean,
            required: true,
            twoWay: true
        }
    },
    methods: {
        modalActiveContent: function(i) {
            return this.active === i;
        },
        setModalClose: function() {
            this.show = false;
            this.active = false;

        }
    }
});

var Matches = (function() {
    function Matches(matchData) {
        var matchHolderLeft = [];
        var matchHolderMid = [];
        var matchHolderRight = [];
        for (var match in matchData){
            var thisMatch = matchData[match];
            var matchColumn = thisMatch.matchNum % 3;
            switch(matchColumn){
                case 0:
                    matchHolderRight.push(thisMatch);
                    break;
                case 1:
                    matchHolderLeft.push(thisMatch);
                    break;
                case 2:
                    matchHolderMid.push(thisMatch);
            }
        }

        this.component = new Vue({
            el: '#matches',
            data: {
                active: 0,
                showModal: false,
                matchesLeft: matchHolderLeft,
                matchesMid: matchHolderMid,
                matchesRight: matchHolderRight
            },
            methods: {
                modalOpen: function(i) {
                    this.showModal = true;
                    return this.active = i;
                }
            }
        });
    };
    return Matches;
})();