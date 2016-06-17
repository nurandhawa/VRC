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
        this.component = new Vue({
            el: '#matches',
            data: {
                matches: matchData
            }
        });
    };
    return Matches;
})();


