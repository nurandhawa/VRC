var Ladder = (function () {
    "use strict";

    var NUM_ENTRIES_PER_PAGE = 20;

    var STATUS_BUTTON_HTML_PREFIX = '<a v-on:click="changeStatus()"' +
        'class="btn btn-raised btn-xs toggle-button ';
    var TIME_SELECT_HTML = '<button class="btn-link" id="timeSelect">preferred time</button>';
    var STATUS_BUTTON_HTML_SUFFIX = ' ">{{ status }}</a>';

    var EDIT_BUTTON_HTML = '<a v-on:click="editPair()" class="edit-button btn btn-info btn-fab btn-fab-mini"><i class="material-icons md-light">create</i></a>';
    var PAGE_BUTTON_PREFIX = '<a v-on:click="changeCurrentPage()"> Page {{ ';
    var PAGE_BUTTON_SUFFIX = ' }} </a>';

    var EMPTY_NEW_PAIR_DATA =
    {
        player1: {
            type: "",
            existingPlayer: "",
            firstName: "",
            lastName: "",
            phoneNumber: ""
        },
        player2: {
            type: "",
            existingPlayer: "",
            firstName: "",
            lastName: "",
            phoneNumber: ""
        },
        position: ""
    };
    var EMPTY_NEW_PAIR_VALID =
    {
        position: true,
        player1: {
            new: false,
            existing: false
        },
        player2: {
            new: false,
            existing: false
        }
    };

    var showModal = function (index) {
        var modalId = "#modal" + index;
        $(modalId).modal("show");
    };

    var hideModal = function (index) {
        var modalId = "#modal" + index;
        $(modalId).modal("hide");
    };

    function Ladder(ladderData) {


        var playingButton;
        var notPlayingButton;
        var pageButton;
        var ladderPages = [];
        var currentPage = [];
        var searchText = 'none';
        var timeSelectButton;

        playingButton = Vue.extend({
            data: function () {
                return {status: "Playing"};
            },
            props: ['index'],
            template: STATUS_BUTTON_HTML_PREFIX + 'btn-success' + STATUS_BUTTON_HTML_SUFFIX,
            methods: {
                changeStatus: function () {
                    this.$parent.changeStatus(this.index);
                },

                timeSelection: function () {
                    this.$parent.fillTimeModal(this.index);
                }
            }
        });
        Vue.component('playing-button', playingButton);

        timeSelectButton = Vue.extend({
            template: TIME_SELECT_HTML
        });
        Vue.component('time-select-button', timeSelectButton);

        notPlayingButton = Vue.extend({
            data: function () {
                return {status: "Not Playing"};
            },
            props: ['index'],
            template: STATUS_BUTTON_HTML_PREFIX + 'btn-danger' + STATUS_BUTTON_HTML_SUFFIX,
            methods: {
                changeStatus: function (index) {
                    this.$parent.changeStatus(this.index);
                }
            }
        });
        Vue.component('not-playing-button', notPlayingButton);

        pageButton = Vue.extend({
            props: ['index'],
            template: PAGE_BUTTON_PREFIX + 'index' + PAGE_BUTTON_SUFFIX,
            methods: {
                changeCurrentPage: function (index) {
                    this.$parent.changeCurrentPage(this.index - 1);
                }
            }
        });
        Vue.component('page-button', pageButton);

        var editButton = Vue.extend({
            props: ['index'],
            template: EDIT_BUTTON_HTML,
            methods: {
                editPair: function () {
                    showModal(this.index);
                }
            }
        });

        var onValidityChanged = function (property, category, isValid) {
            if (property === "position") {
                this.newPairValid.position = isValid;
            }
            else if (property === "player1") {
                this.newPairValid.player1[category] = isValid;
            }
            else if (property === "player2") {
                this.newPairValid.player2[category] = isValid;
            }
        };

        var checkValidity = function () {
            var positionValid = this.newPairValid.position === true;

            var player1Valid = this.newPairData.player1.type === 'new' ?
                this.newPairValid.player1.new : this.newPairValid.player1.existing;

            var player2Valid = this.newPairData.player2.type === 'new' ?
                this.newPairValid.player2.new : this.newPairValid.player2.existing;

            if (positionValid && player1Valid && player2Valid) {
                this.onValid("#newPairSave");
            }
            else {
                this.onInvalid("#newPairSave");
            }
        };

        var onValid = function (elementId) {
            $(elementId).prop("disabled", false);
        };

        var onInvalid = function (elementId) {
            $(elementId).prop("disabled", true);
        };

        this.component = new Vue({
            el: '#ladder',
            data: {
                currentLadder: ladderData.pairs,
                ladder: ladderData.pairs,
                players: ladderData.players,
                timeStamp: ladderData.timeStamp,
                ladderPages: ladderPages,
                currentPage: currentPage,
                searchText: searchText,
                newPairData: jQuery.extend(true, {}, EMPTY_NEW_PAIR_DATA),
                newPairValid: jQuery.extend(true, {}, EMPTY_NEW_PAIR_VALID),
                mode: 'read'
            },
            watch: {
                'newPairValid': {
                    handler: checkValidity,
                    deep: true
                },
                'newPairData.player1.type': checkValidity,
                'newPairData.player2.type': checkValidity,
                'newPairData.player1.existingPlayer': function (val) {
                    if (val) {
                        onValidityChanged.call(this, 'player1', 'existing', true);
                    }
                    else {
                        onValidityChanged.call(this, 'player1', 'existing', false);
                    }
                },
                'newPairData.player2.existingPlayer': function (val) {
                    if (val) {
                        onValidityChanged.call(this, 'player2', 'existing', true);
                    }
                    else {
                        onValidityChanged.call(this, 'player2', 'existing', false);
                    }
                },
                'searchText': function (val) {
                    var filteredLadder = this.ladder.filter( function (element) {
                        var teamNameWithoutCase = element.teamName.toLowerCase();
                        if (teamNameWithoutCase.indexOf(val.toLowerCase()) >= 0 ) {
                            return true;
                        } else return false;
                    });
                    this.currentLadder = filteredLadder;
                    this.updatePages();
                }
            },
            components: {
                playing: playingButton,
                notplaying: notPlayingButton,
                edit: editButton,
                'v-select': VueSelect.VueSelect
            },
            methods: {
                changeStatus: this.changeStatus,
                changeMode: this.changeMode,
                changeCurrentPage: this.changeCurrentPage,
                onDelete: this.deletePair,
                onAdd: this.addPair,
                onUpdate: this.updatePair,
                refreshLadder: this.refreshLadder,
                refreshMode: this.refreshMode,
                updateLadder: this.updateLadder,
                updatePages: this.updatePages,
                onValidityChanged: onValidityChanged,
                onValid: onValid,
                onInvalid: onInvalid,
                setTime: this.setTime,
                fillTimeModal: this.fillTimeModal
            }
        });
    }

    Ladder.prototype.changeStatus = function (index) {
        var api = new API();
        var pair = this.ladder[index];
        if (pair.playingStatus === "playing") {
            api.updatePairStatus(pair.id, "not playing", function (response) {
                pair.isPlaying = false;
                pair.playingStatus = "notplaying";
            });
        }
        else {
            api.updatePairStatus(pair.id, "playing", function (response) {
                pair.isPlaying = true;
                pair.playingStatus = "playing";
            });
        }
    };

    Ladder.prototype.changeCurrentPage = function (index) {
        this.currentPage = this.ladderPages[index];
    };

    Ladder.prototype.changeMode = function () {
        if (this.mode === 'read') {
            this.mode = 'edit';
            this.ladder.forEach(function (entry) {
                entry.playingStatus = 'edit';
            });
        }
        else {
            this.mode = 'read';
            this.ladder.forEach(function (entry) {
                entry.playingStatus = entry.isPlaying ? 'playing' : 'notplaying';
            });
        }
    };

    Ladder.prototype.deletePair = function (index) {
        var answer = confirm("Are you sure you want to delete this pair?");
        if (answer) {
            var api = new API();
            api.removePair(this.ladder[index].id, this.refreshLadder);
            hideModal(index);
        }
    };

    Ladder.prototype.setTime = function (index) {
        $("#timeSelectModal" + index).modal("hide");
        var pair = this.ladder[index];
        var api = new API();
        if (document.getElementById("checkbox8pm" + index).checked) {
            api.setTime("08:00 pm", pair.id);
            pair.timeSlot = "SLOT_1";
        } else if (document.getElementById("checkbox9pm" + index).checked) {
            api.setTime("09:30 pm", pair.id);
            pair.timeSlot = "SLOT_2";
        } else {
            api.setTime("NO_SLOT", pair.id);
            pair.timeSlot = "NO_SLOT";
        }
        this.refreshLadder();
    };

    Ladder.prototype.addPair = function (event) {
        var api = new API();

        var player1Data = this.newPairData.player1;
        var player1 = null;
        if (player1Data.type === "existing") {
            player1 = api.prepareExistingPlayer(player1Data.existingPlayer.id);
        }
        else {
            player1 = api.prepareNewPlayer(player1Data.firstName,
                player1Data.lastName, player1Data.phoneNumber);
        }

        var player2Data = this.newPairData.player2;
        var player2 = null;
        if (player2Data.type === "existing") {
            player2 = api.prepareExistingPlayer(player2Data.existingPlayer.id);
        }
        else {
            player2 = api.prepareNewPlayer(player2Data.firstName,
              player2Data.lastName, player2Data.phoneNumber);
        }

        var ladderPosition = this.newPairData.position;
        if (ladderPosition === "") {
            ladderPosition = -1;
        }

        api.addPair([player1, player2], ladderPosition, function () {
            this.newPairData = jQuery.extend(true, {}, EMPTY_NEW_PAIR_DATA);
            this.newPairValid = jQuery.extend(true, {}, EMPTY_NEW_PAIR_VALID);
            this.refreshLadder();
        }.bind(this));
        $("#addPairModal").modal("hide");
    };

    Ladder.prototype.updatePair = function (index, event) {
        var api = new API();

        var pair = this.ladder[index];
        var pairId = pair.id;
        var newPosition = pair.newPosition;

        api.updatePairPosition(pairId, newPosition, this.refreshLadder);
        hideModal(index);
    };

    Ladder.prototype.updatePages = function () {
        var ladderPages = [];
        var currentPairs = this.currentLadder;
        var pageIndex = 0;
        if (currentPairs) {
            var numPages = Math.floor(currentPairs.length / NUM_ENTRIES_PER_PAGE) + 1;
            for (var i = 0; i < numPages; i++) {
                ladderPages[i] = [];
            }
            for (i = 0; i < currentPairs.length; i++) {
                if (ladderPages[pageIndex].length >= NUM_ENTRIES_PER_PAGE) {
                    pageIndex++;
                }
                ladderPages[pageIndex].push(currentPairs[i]);
            }
        }
        this.ladderPages = ladderPages;
        this.currentPage = ladderPages[0];
    };

    Ladder.prototype.updateLadder = function (ladderData) {
        this.ladder = ladderData.pairs;
        this.players = ladderData.players;
        this.currentLadder = ladderData.pairs;
        this.updatePages();
    };


    Ladder.prototype.refreshLadder = function () {
        var api = new API();
        api.getLadder(function (ladderData) {
            this.updateLadder(ladderData);
            this.timeStamp = ladderData.timeStamp;
            this.refreshMode();
        }.bind(this));
    };

    Ladder.prototype.refreshMode = function () {
        if (this.mode === 'edit') {
            this.ladder.forEach(function (entry) {
                entry.playingStatus = 'edit';
            });
        }
        else {
            this.ladder.forEach(function (entry) {
                entry.playingStatus = entry.isPlaying ? 'playing' : 'notplaying';
            });
        }
    };

    Ladder.prototype.fillTimeModal = function (index) {
        var timeModal = document.getElementById("timeSelectModal" + index);
        var firstCheckBox = document.getElementById("checkbox8pm" + index);
        var secondCheckBox = document.getElementById("checkbox9pm" + index);

        $("#timeSelectModal" + index).modal("show");

        var pair = this.ladder[index];
        if (pair.timeSlot == "SLOT_1") {
            firstCheckBox.checked = true;
            secondCheckBox.checked = false;
        } else if (pair.timeSlot == "SLOT_2") {
            firstCheckBox.checked = false;
            secondCheckBox.checked = true;
        } else {
            firstCheckBox.checked = false;
            secondCheckBox.checked = false;
        }

        window.onclick = function (event) {
            if (event.target == timeModal) {
                $("#timeSelectModal" + index).modal("hide");
            }
        };
        firstCheckBox.onclick = function () {
            secondCheckBox.checked = false;
        };
        secondCheckBox.onclick = function () {
            firstCheckBox.checked = false;
        };
    };

    return Ladder;
})();
