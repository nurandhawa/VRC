var Ladder = (function() {
    "use strict";

    var STATUS_BUTTON_HTML_PREFIX = '<a v-on:click="changeStatus()"' +
    'class="btn btn-raised btn-xs toggle-button ';
    var STATUS_BUTTON_HTML_SUFFIX = ' ">{{ status }}</a>';

    var EDIT_BUTTON_HTML = '<a v-on:click="editPair()" class="edit-button btn btn-info btn-fab btn-fab-mini"><i class="material-icons md-light">create</i></a>';

    function Ladder(ladderData) {

        var playingButton;
        var notPlayingButton;

        playingButton = Vue.extend({
            data: function() {
                return { status: "Playing" };
            },
            props: ['index'],
            template: STATUS_BUTTON_HTML_PREFIX + 'btn-success' + STATUS_BUTTON_HTML_SUFFIX,
            methods: {
                changeStatus: function() {
                    this.$parent.changeStatus(this.index);
                }
            }
        });
        Vue.component('playing-button', playingButton);

        notPlayingButton = Vue.extend({
            data: function() {
                return { status: "Not Playing" };
            },
            props: ['index'],
            template: STATUS_BUTTON_HTML_PREFIX + 'btn-danger' + STATUS_BUTTON_HTML_SUFFIX,
            methods: {
                changeStatus: function(index) {
                    this.$parent.changeStatus(this.index);
                }
            }
        });
        Vue.component('not-playing-button', notPlayingButton);

        var editButton = Vue.extend({
            props: ['index'],
            template: EDIT_BUTTON_HTML,
            methods: {
                editPair: function() {
                    var modalId = "#modal" + this.index;
                    $(modalId).modal("show");
                }
            }
        });
        Vue.component('edit-button', editButton);

        this.component = new Vue({
            el: '#ladder',
            data: {
                ladder: ladderData,
                mode: 'read'
            },
            components: {
                playing: playingButton,
                notplaying: notPlayingButton,
                edit: editButton
            },
            methods: {
                changeStatus: this.changeStatus,
                changeMode: this.changeMode
            }
        });
    }

    Ladder.prototype.changeStatus = function(index) {
        if (this.ladder[index].playingStatus === "playing") {
            this.ladder[index].playingStatus = "notplaying";
        }
        else {
            this.ladder[index].playingStatus = "playing";
        }
    };

    Ladder.prototype.changeMode = function() {
        if (this.mode === 'read') {
            this.mode = 'edit';
            this.ladder.forEach(function(entry) {
                entry.playingStatus = 'edit';
            });
        }
        else {
            this.mode = 'read';
            this.ladder.forEach(function(entry) {
                // TODO: set status to previous state
                entry.playingStatus = 'playing';
            });
        }
    };

    return Ladder;
})();
