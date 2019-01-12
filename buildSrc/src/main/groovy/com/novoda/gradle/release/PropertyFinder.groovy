package com.novoda.gradle.release

import org.gradle.api.Project;

class PropertyFinder {

    private final Project project
    private final PublishExtension extension

    PropertyFinder(Project project, PublishExtension extension) {
        this.extension = extension
        this.project = project
    }

    def getBintrayUser() {
        getString(project, 'bintrayUser', extension.bintrayUser)
    }

    def getBintrayKey() {
        getString(project, 'bintrayKey', extension.bintrayKey)
    }

    def getDryRun() {
        getBoolean(project, 'dryRun', extension.dryRun)
    }

    def getOverride() {
        getBoolean(project, 'override', extension.override)
    }

    def getPublishVersion() {
        getString(project, 'publishVersion', extension.publishVersion)
    }

    private String getString(Project project, String propertyName, String defaultValue) {
        project.hasProperty(propertyName) ? project.getProperty(propertyName) : defaultValue
    }

    private boolean getBoolean(Project project, String propertyName, boolean defaultValue) {
        project.hasProperty(propertyName) ? Boolean.parseBoolean(project.getProperty(propertyName)) : defaultValue
    }

}
