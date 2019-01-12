package com.novoda.gradle.release

import com.jfrog.bintray.gradle.BintrayPlugin
import com.novoda.gradle.release.internal.AndroidAttachments
import com.novoda.gradle.release.internal.JavaAttachments
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.publish.PublicationContainer
import org.gradle.api.publish.PublishingExtension
import org.gradle.api.publish.maven.MavenPublication

class ReleasePlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        PublishExtension extension = project.extensions.create('publish', PublishExtension)
        project.afterEvaluate {
            extension.validate()
            attachArtifacts(extension, project)
            new BintrayConfiguration(extension).configure(project)
        }
        project.apply([plugin: 'maven-publish'])
        new BintrayPlugin().apply(project)
    }

    private static void attachArtifacts(PublishExtension extension, Project project) {
        project.plugins.withId('com.android.library') {
            project.android.libraryVariants.all { variant ->
                String publicationName = variant.name
                MavenPublication publication = createPublication(publicationName, project, extension)
                new AndroidAttachments(publicationName, project, variant).attachTo(publication)
            }
        }
        project.plugins.withId('java') {
            String publicationName = 'maven'
            MavenPublication publication = createPublication(publicationName, project, extension)
            new JavaAttachments(publicationName, project).attachTo(publication)
        }
    }

    private static MavenPublication createPublication(String publicationName, Project project, PublishExtension extension) {
        PropertyFinder propertyFinder = new PropertyFinder(project, extension)
        String groupId = extension.groupId
        String artifactId = extension.artifactId
        String version = propertyFinder.publishVersion

        PublicationContainer publicationContainer = project.extensions.getByType(PublishingExtension).publications
        return publicationContainer.create(publicationName, MavenPublication) { MavenPublication publication ->
            publication.groupId = groupId
            publication.artifactId = artifactId
            publication.version = version
        } as MavenPublication
    }
}
