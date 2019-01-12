package com.novoda.release.internal.compat.gradle4_5

import org.gradle.api.artifacts.*
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

/**
 * This implementation of {@code SoftwareComponentInternal} is heavily inspired by {@code JavaLibrary},
 * see: https://github.com/gradle/gradle/blob/v4.5.0/subprojects/plugins/src/main/java/org/gradle/api/internal/java/JavaLibrary.java
 */
class AndroidSoftwareComponentCompat_Gradle_4_5 implements SoftwareComponentInternal {

    private final UsageContext runtimeUsage
    private final UsageContext compileUsage
    protected final ConfigurationContainer configurations
    protected final ObjectFactory objectFactory
    protected final ImmutableAttributesFactory attributesFactory

    @Inject
    AndroidSoftwareComponentCompat_Gradle_4_5(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory) {
        this.configurations = configurations
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
        this.runtimeUsage = new RuntimeUsageContext(Usage.JAVA_RUNTIME)
        this.compileUsage = new CompileUsageContext(Usage.JAVA_API)
    }

    @Override
    Set<? extends UsageContext> getUsages() {
        return ([runtimeUsage, compileUsage] as Set).asImmutable()
    }

    @Override
    String getName() {
        return 'android'
    }

    private abstract class AbstractUsageContext implements UsageContext {
        private final Usage usage
        private final ImmutableAttributes attributes

        AbstractUsageContext(String usageName) {
            this.usage = objectFactory.named(Usage.class, usageName)
            this.attributes = attributesFactory.of(Usage.USAGE_ATTRIBUTE, usage)
        }

        @Override
        AttributeContainer getAttributes() {
            return attributes
        }

        @Override
        Usage getUsage() {
            return usage
        }

        @Override
        Set<PublishArtifact> getArtifacts() {
            return Collections.emptySet()
        }
    }

    private class RuntimeUsageContext extends AbstractUsageContext {
        private DependencySet dependencies

        RuntimeUsageContext(String usageName) {
            super(usageName)
        }

        @Override
        String getName() {
            return 'runtime'
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            return getRuntimeDependencies().withType(ModuleDependency.class)
        }

        @Override
        Set<? extends DependencyConstraint> getDependencyConstraints() {
            return getRuntimeDependencies().withType(DependencyConstraint.class)
        }

        private DependencySet getRuntimeDependencies() {
            if (dependencies == null) {
                dependencies = configurations.getByName('implementation').getIncoming().getDependencies()
            }
            return dependencies
        }
    }

    private class CompileUsageContext extends AbstractUsageContext {
        private DependencySet dependencies

        CompileUsageContext(String usageName) {
            super(usageName)
        }

        @Override
        String getName() {
            return 'api'
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            return getApiDependencies().withType(ModuleDependency.class)
        }

        @Override
        Set<? extends DependencyConstraint> getDependencyConstraints() {
            return getApiDependencies().withType(DependencyConstraint.class)
        }

        private DependencySet getApiDependencies() {
            if (dependencies == null) {
                dependencies = configurations.getByName('api').getIncoming().getDependencies()
            }
            return dependencies
        }
    }
}
