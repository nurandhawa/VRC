/**
 * Created by David on 2016-06-15.
 */

var EDIT_BUTTON_HTML = '<a v-on:click="editMatch()" class="edit-button btn btn-info btn-fab btn-fab-mini"><i class="material-icons md-light">create</i></a>';

Vue.filter('filterLeft', function (array, matchNum) {
    var filteredArray = [];
    if (matchNum % 3 == 1) {
        filteredArray.add(array[matchNum]);
    }
});

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
    filters: {
        filterLeft: function (matchNum) {
            var matches = [];
            matches.add(1);
            matches.add(2);
            matches.add(3);
            return matches;
        }
    },
    methods: {
        modalActiveContent: function (i) {
            return this.active === i;
        },
        setModalClose: function () {
            this.show = false;
            this.active = false;

        }
    }
});

var Matches = (function () {
    function Matches(matchData) {
        for (var match in matchData){
            var editButton = Vue.extend({
                props: ['column','index'],
                template: EDIT_BUTTON_HTML,
                methods: {
                    editMatch: function() {
                        this.$parent.modalOpen(this.index);
                    }
                 }
            });
            Vue.component('edit-button', editButton);

            var blankButton = Vue.extend({
                template: "<a></a>"
            });
        }

        this.component = new Vue({
            el: '#matches',
            data: {
                active: 0,
                showModal: false,
                matches: matchData,
                mode: 'read'
            },
            methods: {
                filterLeft: function () {
                    return 1;
                },
                modalOpen: function (i) {
                    this.showModal = true;
                    this.active = i;
                    return this.active;
                }
            },
            components: {
                edit: editButton,
                read: blankButton
            }
        });
    }

    Matches.prototype.changeMode = function () {
        if (this.mode === 'read') {
            this.mode = 'edit';
        }
        else {
            this.mode = 'read';
        }
    };

    return Matches;
})();