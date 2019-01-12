package com.novoda.gradle.release

import groovy.transform.PackageScope

/**
 * A gradle extension which will be used to configure the plugin.
 *
 * Most of the properties will be used to setup the `bintray-Extension` in BintrayConfiguration.
 * See also: https://github.com/bintray/gradle-bintray-plugin#plugin-dsl
 *
 * Some properties are mandatory and have to be validated before any action on it happen.
 * The other ones are all optional or provide a default value.
 *
 * Optional doesn't mean they aren't needed but that they will handled correctly by the plugin!
 */
class PublishExtension {

    String repoName = 'maven'
    String userOrg

    String groupId
    String artifactId

    String publishVersion

    Map<String, String> versionAttributes = [:]

    String[] licences = ['Apache-2.0']

    String uploadName = ''

    String desc

    String website = ''
    String issueTracker = ''
    String repository = ''
    boolean autoPublish = true

    String bintrayUser = ''
    String bintrayKey = ''
    boolean dryRun = true
    boolean override = false

    String[] publications

    /**
     * Validate all mandatory properties for this extension.
     *
     * Will throw a Exception if not setup correctly.
     */
    @PackageScope
    void validate() {
        String extensionError = "";
        if (userOrg == null) {
            extensionError += "Missing userOrg. "
        }
        if (groupId == null) {
            extensionError += "Missing groupId. "
        }
        if (artifactId == null) {
            extensionError += "Missing artifactId. "
        }
        if (publishVersion == null) {
            extensionError += "Missing publishVersion. "
        }
        if (desc == null) {
            extensionError += "Missing desc. "
        }

        if (extensionError) {
            String prefix = "Have you created the publish closure? "
            throw new IllegalStateException(prefix + extensionError)
        }
    }

}
