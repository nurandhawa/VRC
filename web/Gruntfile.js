module.exports = function(grunt) {

    grunt.initConfig({
        jshint: {
            all: ['js/*.js'],
            with_overrides: {
                files: {
                    src: ['js/vue.js', 'js/jquery-1.12.4.js']
                }
            }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');

};
