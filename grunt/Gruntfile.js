module.exports = function(grunt) {
    'use strict';
    function loadDependencies(deps) {
        if (deps) {
            for (var key in deps) {
                if (key.indexOf("grunt-") == 0) {
                    grunt.loadNpmTasks(key);
                }
            }
        }
    }
 
    grunt.initConfig({
        pkg: grunt.file.readJSON('package.json'),

        copy: {
            template: {
                files: [{
                    expand: true,
                    dest: "../target/heroku-samples/",
                    cwd: "../src/main/webapp/",
                    src: "**"
                }]
            }
        },

        watch: {
            template: {
                files: [
                    '../src/main/webapp/**'
                ],
                tasks: ['copy'],
                options: {
                    livereload: true
                }
            }
        }
    });
 
    loadDependencies(grunt.config("pkg").devDependencies);

    grunt.registerTask('default', [ 'watch']);
};