module.exports = function(grunt) {

    grunt.initConfig({
        jshint: {
            all: ['js/*.js']
        },
        jasmine: {
          api: {
            src: ['js/api.js'],
            options: {
              specs: 'test/APITest.js',
              vendor: ['vendors/jquery-1.12.4.js']
            }
          }
        }
    });

    grunt.loadNpmTasks('grunt-contrib-jshint');
    grunt.loadNpmTasks('grunt-contrib-jasmine');

};
