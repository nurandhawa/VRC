module.exports = function(grunt) {

    grunt.initConfig({
        jshint: {
            all: ['js/*.js']
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');

};
