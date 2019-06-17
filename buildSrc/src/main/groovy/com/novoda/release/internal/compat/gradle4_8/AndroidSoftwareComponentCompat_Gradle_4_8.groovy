package com.novoda.release.internal.compat.gradle4_8

import org.gradle.api.DomainObjectSet
import org.gradle.api.artifacts.*
import org.gradle.api.attributes.AttributeContainer
import org.gradle.api.attributes.Usage
import org.gradle.api.capabilities.Capability
import org.gradle.api.internal.artifacts.configurations.Configurations
import org.gradle.api.internal.attributes.ImmutableAttributes
import org.gradle.api.internal.attributes.ImmutableAttributesFactory
import org.gradle.api.internal.component.SoftwareComponentInternal
import org.gradle.api.internal.component.UsageContext
import org.gradle.api.model.ObjectFactory

import javax.inject.Inject

/**
 * This implementation of {@code SoftwareComponentInternal} is heavily inspired by {@code JavaLibrary},
 * see: https://github.com/gradle/gradle/blob/v4.8.0/subprojects/plugins/src/main/java/org/gradle/api/internal/java/JavaLibrary.java
 */
class AndroidSoftwareComponentCompat_Gradle_4_8 implements SoftwareComponentInternal {

    private final UsageContext runtimeUsage
    private final UsageContext compileUsage
    protected final ConfigurationContainer configurations
    protected final ObjectFactory objectFactory
    protected final ImmutableAttributesFactory attributesFactory

    @Inject
    AndroidSoftwareComponentCompat_Gradle_4_8(ObjectFactory objectFactory, ConfigurationContainer configurations, ImmutableAttributesFactory attributesFactory) {
        this.configurations = configurations
        this.objectFactory = objectFactory
        this.attributesFactory = attributesFactory
        this.runtimeUsage = createRuntimeUsageContext()
        this.compileUsage = createCompileUsageContext()
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

    private UsageContext createRuntimeUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_RUNTIME, 'runtime', 'implementation')
    }

    private UsageContext createCompileUsageContext() {
        return new ConfigurationUsageContext(Usage.JAVA_API, 'api', 'api')
    }

    private class ConfigurationUsageContext extends AbstractUsageContext {
        private final String name
        private final String configurationName
        private DomainObjectSet<ModuleDependency> dependencies
        private DomainObjectSet<DependencyConstraint> dependencyConstraints
        private Set<? extends Capability> capabilities
        private Set<ExcludeRule> excludeRules


        ConfigurationUsageContext(String usageName, String name, String configurationName) {
            super(usageName)
            this.name = name
            this.configurationName = configurationName
        }

        @Override
        String getName() {
            return name
        }

        @Override
        Set<ModuleDependency> getDependencies() {
            if (dependencies == null) {
                dependencies = getConfiguration().getIncoming().getDependencies().withType(ModuleDependency.class)
            }
            return dependencies
        }

        @Override
        Set<? extends DependencyConstraint> getDependencyConstraints() {
            if (dependencyConstraints == null) {
                dependencyConstraints = getConfiguration().getIncoming().getDependencyConstraints()
            }
            return dependencyConstraints
        }

        @Override
        Set<? extends Capability> getCapabilities() {
            if (capabilities == null) {
                this.capabilities = Configurations.collectCapabilities(getConfiguration(), Collections.emptySet(), Collections.emptySet()).asImmutable()
            }
            return capabilities
        }

        @Override
        Set<ExcludeRule> getGlobalExcludes() {
            if (excludeRules == null) {
                this.excludeRules = getConfiguration().getExcludeRules().asImmutable()
            }
            return excludeRules
        }

        private Configuration getConfiguration() {
            return configurations.getByName(configurationName)
        }
    }

}
