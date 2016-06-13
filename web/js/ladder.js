"use strict";

var STATUS_BUTTON_HTML_PREFIX = '<a v-on:click="changeStatus($index)"' +
'class="btn btn-raised btn-xs toggle-button ';
var STATUS_BUTTON_HTML_SUFFIX = ' ">{{ status }}</a>';

var Ladder = (function() {
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

        this.component = new Vue({
            el: '#ladder',
            data: {
                ladder: ladderData
            },
            components: {
                playing: playingButton,
                notplaying: notPlayingButton
            },
            methods: {
                changeStatus: this.changeStatus
            }
        });
    };

    Ladder.prototype.changeStatus = function(index) {
        if (this.ladder[index].playingStatus === "playing") {
            this.ladder[index].playingStatus = "notplaying";
        }
        else {
            this.ladder[index].playingStatus = "playing";
        }
    };

    return Ladder;
})();
