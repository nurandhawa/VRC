(function () {
    "use strict";

    $.material.options.validate = false;
    $.material.init();

    var ANNOUNCEMENT_DIV_ID = "#announcements";

    var announcementComponent = new Vue({
        el: ANNOUNCEMENT_DIV_ID,
        data: {
            announcements: [],
            announcementIndex: -1,
            announcementTitle: "",
            announcementMessage: ""
        },
        methods: {
            nextAnnouncement: function() {
                if (this.announcementIndex < (this.announcements.length - 1)) {
                    this.announcementIndex++;
                    this.refreshAnnouncement();
                }
            },
            previousAnnouncement: function() {
                if (this.announcementIndex > 0) {
                    this.announcementIndex--;
                    this.refreshAnnouncement();
                }
            },
            refreshAnnouncement: function() {
                var currentAnnouncement = this.announcements[this.announcementIndex];
                this.announcementTitle = currentAnnouncement.title;
                this.announcementMessage = currentAnnouncement.message;
            }
        }
    });

    var api = new API();
    api.getAnnouncements(function(newAnnouncements) {
        announcementComponent.announcements = newAnnouncements;
        announcementComponent.nextAnnouncement();
    });
})();
