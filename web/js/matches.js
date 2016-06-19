/**
 * Created by David on 2016-06-15.
 */
Vue.component('mymodal', {
    template: '#modal-template',
    props: {
        show: {
            type: Boolean,
            required: true,
            twoWay: true
        }
    }
})

// start app
new Vue({
    el: '#myDiv',
    data: {
        showModal: false
    }
})

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
                matchesLeft: matchHolderLeft,
                matchesMid: matchHolderMid,
                matchesRight: matchHolderRight
            }
        });
    };
    return Matches;
})();


