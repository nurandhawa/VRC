/**
 * Created by David on 2016-06-15.
 */

var EDIT_BUTTON_HTML = '<a v-on:click="editMatch()" class="edit-button btn btn-info btn-fab btn-fab-mini"><i class="material-icons md-light">create</i></a>';

var Matches = (function () {
    function Matches(matchData) {
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
                closeModal: this.closeModal,
                applyPenalty: this.applyPenalty,
                saveChanges: this.saveModalChanges
            }
        });

        var editButton;
        var blankButton;
        for (var match in matchData){
            editButton = null;
            blankButton = null;

            editButton = Vue.extend({
                props: ['column','index'],
                template: EDIT_BUTTON_HTML,
                methods: {
                    editMatch: function() {
                        this.$parent.openModal(this.index);
                    }
                 }
            });
            Vue.component('edit-button', editButton);

            blankButton = Vue.extend({
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
                openModal: this.openModal
            },
            components: {
                edit: editButton,
                read: blankButton
            }
        });
    }

    Matches.prototype.openModal = function(index) {
        this.showModal = true;
        this.active = index;
        return this.active;
    };

    Matches.prototype.closeModal = function() {
        this.show = false;
        this.active = false;
    };

    Matches.prototype.applyPenalty = function(pair, penaltyType, event) {
        if (penaltyType === "late") {
            pair.latePenalty = true;
        }
        else if (penaltyType === "miss") {
            pair.absentPenalty = true;
        }
        var api = new API();
        api.addPenalty(pair.id, penaltyType, function(response) {
            $(event.srcElement).addClass("btn-raised");
        });
    };

    Matches.prototype.saveModalChanges = function (index) {
        var match = this.matchlist[index];
        var results = [];

        for (var i = 0; i < match.pairs.length; i++) {
            var resultRow = [];
            for (var pair in match.pairs) {
                resultRow.push(match.pairs[pair].results[i]);
            }
            results.push(resultRow);
        }

        var api = new API();
        api.inputMatchResults(match.id, results, function(matchData) {
            this.matchlist = matchData;
        });
        this.closeModal();
    };

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
