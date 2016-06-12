"use strict";

var ladder = [
  {
    position: 1,
    name: "Alex Land and David Li",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 2,
    name: "Gordon Shieh and Samuel Kim",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 3,
    name: "Jas Jassal and Noor Randhawa",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  },
  {
    position: 4,
    name: "Constantin Koval and Raymond Chan",
    playingStatus: {
      status: "Playing",
      'btn-success': true
    }
  }
];

var changeStatus = function(index) {
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

var headerComponent = new Vue({
  el: '#header',
  data: {
    title: 'Ladder',
    timestamp: function() {
      // TODO: Use timestamp of when ladder was last modified on the server
      var currentdate = new Date();
      return currentdate.toDateString() + " at " + currentdate.toTimeString();
    }()
  }
});

var ladderComponent = new Vue({
  el: '#ladder',
  data: {
    ladder: ladder
  },
  methods: {
    changeStatus: changeStatus
  }
});

function test() {
  console.log("boo");
}
