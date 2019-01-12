package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.component.SoftwareComponent
import org.gradle.api.publish.maven.MavenPublication
import org.gradle.api.tasks.bundling.Jar
import org.gradle.api.tasks.javadoc.Javadoc

class MavenPublicationAttachments {

    private final SoftwareComponent softwareComponent
    private final List<Object> allArtifactSources

    MavenPublicationAttachments(SoftwareComponent softwareComponent, def ... allArtifactSources) {
        this(softwareComponent, Arrays.asList(allArtifactSources).asImmutable())
    }

    MavenPublicationAttachments(SoftwareComponent softwareComponent, List<Object> allArtifactSources) {
        this.softwareComponent = softwareComponent
        this.allArtifactSources = allArtifactSources
    }

    final void attachTo(MavenPublication publication) {
        allArtifactSources.each { publication.artifact it }
        publication.from softwareComponent
    }

    protected static Task sourcesJarTask(Project project, String publicationName, def ... sourcePaths) {
        return project.task("genereateSourcesJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
            jar.classifier = 'sources'
            jar.from sourcePaths
        }
    }

    protected static Task javadocsJarTask(Project project, String publicationName, Javadoc javadoc) {
        return project.task("genereateJavadocsJarFor${publicationName.capitalize()}Publication", type: Jar) { Jar jar ->
            jar.classifier = 'javadoc'
            jar.from project.files(javadoc)
        }
    }
}
