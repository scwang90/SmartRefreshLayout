package com.novoda.gradle.release

import org.gradle.api.Project
import org.gradle.api.file.FileCollection
import org.gradle.api.file.FileTree

class GradlePluginPropertyFinder {
    private static final String FILE_EXTENSION_PROPERTIES = ".properties"
    private final Project project

    GradlePluginPropertyFinder(Project project) {
        this.project = project
    }

    String findBestGradlePluginId() {
        FileTree pluginFiles = project.fileTree(dir: 'src/main/resources/META-INF/gradle-plugins')
        if (pluginFiles.isEmpty()) {
            return null
        }
        FileCollection filteredPluginFiles = pluginFiles.filter {
            it.name.endsWith(FILE_EXTENSION_PROPERTIES) &&
                    isNamespacedPropertyFile(it)
        }
        if (filteredPluginFiles.isEmpty()) {
            return null
        }
        File bestPluginFile = filteredPluginFiles.first()
        return removePropertyFileExtension(bestPluginFile)
    }

    private boolean isNamespacedPropertyFile(File file) {
        return file.name.substring(0, file.name.length() - FILE_EXTENSION_PROPERTIES.length()).contains('.')
    }

    private String removePropertyFileExtension(File bestPluginFile) {
        bestPluginFile.name.substring(0, bestPluginFile.name.length() - FILE_EXTENSION_PROPERTIES.length())
    }
}
