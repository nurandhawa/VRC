var Ladder = (function() {
    function Ladder(ladderData) {
        this.component = new Vue({
            el: '#ladder',
            data: {
                ladder: ladderData
            },
            methods: {
                changeStatus: this.changeStatus
            }
        });
    };

    Ladder.prototype.changeStatus = function(index) {
        if (this.ladder[index].playingStatus.status === "Playing") {
            this.ladder[index].playingStatus = {
                status: "Not Playing",
                'btn-danger': true
            };
        }
        else {
            this.ladder[index].playingStatus = {
                status: "Playing",
                'btn-success': true
            };
        }
    };

    return Ladder;
})();
