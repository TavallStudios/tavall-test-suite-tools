package org.tavall.architecture.di;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.tavall.architecture.core.ArchitectureContext;
import org.tavall.architecture.core.ArchitectureViolation;
import org.tavall.dependency.IDependencyAccess;
import org.tavall.dependency.IDependencyInjectableConcrete;
import org.tavall.dependency.IDependencyInjectableInterface;
import org.tavall.dependency.annotations.CompositionBoundary;
import org.tavall.dependency.annotations.DelegatesTo;
import org.tavall.dependency.annotations.ExplicitNonDi;
import org.tavall.dependency.maps.DependencyMap;
import org.tavall.dependency.maps.interfaces.IDependencyMap;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class DependencyInjectionRuleTest {

    private String previousClassRoots;
    private String previousSourceRoots;

    @BeforeEach
    void setUp() {
        previousClassRoots = System.getProperty("tavall.architecture.classRoots");
        previousSourceRoots = System.getProperty("tavall.architecture.sourceRoots");
        System.setProperty(
                "tavall.architecture.classRoots",
                Path.of("build/classes/java/test").toAbsolutePath().toString()
        );
    }

    @AfterEach
    void tearDown() {
        if (previousClassRoots == null) {
            System.clearProperty("tavall.architecture.classRoots");
        } else {
            System.setProperty("tavall.architecture.classRoots", previousClassRoots);
        }
        if (previousSourceRoots == null) {
            System.clearProperty("tavall.architecture.sourceRoots");
        } else {
            System.setProperty("tavall.architecture.sourceRoots", previousSourceRoots);
        }
    }

    @Test
    void aggregatesInheritedRetiredMarkersIntoOneClassViolation() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().equals(BothRetiredMarkers.class.getName()))
                .toList();

        assertEquals(1, violations.size());
        assertEquals("retired-di-marker|" + BothRetiredMarkers.class.getName(), violations.getFirst().debtKey());
    }

    @Test
    void allowsInterfaceFirstDiManagedComponents() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().startsWith(ValidSampleConsumer.class.getName())
                        || v.subject().startsWith(ValidSampleServiceImpl.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Valid interface-first components must not produce violations: " + violations);
    }

    @Test
    void catchesConcreteImplementationDependencyInField() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("concrete-implementation-dependency")
                        && v.subject().startsWith(BadConcreteConsumer.class.getName()))
                .toList();

        assertFalse(violations.isEmpty(), "Direct dependency on concrete implementation must be flagged");
        assertTrue(violations.getFirst().subject().contains(ValidSampleServiceImpl.class.getName()));
    }

    @Test
    void emitsOneDebtFindingWhenFieldAndConstructorShareTheSameConcreteDependency() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("concrete-implementation-dependency")
                        && v.subject().startsWith(BadConcreteConsumer.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void catchesDirectMapUsageInOrdinaryProductionClass() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("direct-dependency-map-access")
                        && v.subject().equals(BadMapConsumer.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void allowsGeneratedDependencyAccessAdapterToReferenceItsOwningMap() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().startsWith(GeneratedAccessFixtureDependencyAccess.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Generated dependency access is an approved DI bridge: " + violations);
    }

    @Test
    void allowsConsumerToReceiveGeneratedDependencyAccessAdapter() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().startsWith(GeneratedAccessConsumer.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Generated DI access adapters are valid consumer dependencies: " + violations);
    }

    @Test
    void catchesUnmanagedBehavioralComponent() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("unmanaged-behavioral-component")
                        && v.subject().equals(UnmanagedTestService.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void catchesBehavioralComponentMissingInterfaceContract() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("behavior-missing-interface")
                        && v.subject().equals(NoInterfaceTestService.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void catchesApiContractDependingOnImplementation() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("invalid-api-dependency-direction")
                        && v.subject().startsWith(InvalidApiContract.class.getName()))
                .toList();

        assertEquals(1, violations.size());
        assertTrue(violations.getFirst().subject().contains(ValidSampleServiceImpl.class.getName()));
    }

    @Test
    void allowsCompositionBoundaryToAccessMapAndImplementations() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().startsWith(SampleCompositionRoot.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Composition roots must be permitted to access map and wire concrete implementations: " + violations);
    }

    @Test
    void allowsExplicitNonDiException() {
        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().startsWith(ExplicitNonDiTestService.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Explicit non-DI exception must be honored: " + violations);
    }

    @Test
    void detectsDirectConstructionAndMapAccessInSource(@TempDir Path tempSourceDir) throws IOException {
        Path invalidSource = tempSourceDir.resolve("OrdinaryConsumer.java");
        Files.writeString(invalidSource, """
                package org.tavall.sample;

                import org.tavall.dependency.maps.interfaces.IDependencyMap;

                public final class OrdinaryConsumer {
                    public void run() {
                        ValidSampleServiceImpl service = new ValidSampleServiceImpl();
                        IDependencyMap map = null;
                    }

                    public void runAgain() {
                        ValidSampleServiceImpl service = new ValidSampleServiceImpl();
                    }
                }
                """);

        Path validBootstrapSource = tempSourceDir.resolve("AppBootstrap.java");
        Files.writeString(validBootstrapSource, """
                package org.tavall.sample;

                import org.tavall.dependency.annotations.CompositionBoundary;
                import org.tavall.dependency.maps.DependencyMap;

                @CompositionBoundary
                public final class AppBootstrap {
                    public void configure(DependencyMap map) {
                        ValidSampleServiceImpl service = new ValidSampleServiceImpl();
                    }
                }
                """);

        Path generatedAccessSource = tempSourceDir.resolve("GeneratedConsumerDependencyAccess.java");
        Files.writeString(generatedAccessSource, """
                /* Generated by DependencyAccessSourceLowerer. */
                package org.tavall.sample;

                import org.tavall.dependency.annotations.DelegatesTo;
                import org.tavall.dependency.maps.interfaces.IDependencyMap;

                @DelegatesTo
                public final class GeneratedConsumerDependencyAccess {
                    private final IDependencyMap dependencyMap = null;
                }
                """);

        System.setProperty("tavall.architecture.sourceRoots", tempSourceDir.toAbsolutePath().toString());

        List<ArchitectureViolation> violations = new DependencyInjectionRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().contains("OrdinaryConsumer.java")
                        || v.subject().contains("AppBootstrap.java")
                        || v.subject().contains("GeneratedConsumerDependencyAccess.java"))
                .toList();

        assertTrue(violations.stream().anyMatch(v -> v.ruleId().equals("direct-construction-di-managed")),
                "Expected direct-construction-di-managed violation in OrdinaryConsumer");
        assertEquals(1, violations.stream()
                .filter(v -> v.ruleId().equals("direct-construction-di-managed")
                        && v.subject().endsWith("OrdinaryConsumer.java->ValidSampleServiceImpl"))
                .count());
        assertTrue(violations.stream().anyMatch(v -> v.ruleId().equals("direct-dependency-map-access")),
                "Expected direct-dependency-map-access violation in OrdinaryConsumer");
        assertFalse(violations.stream().anyMatch(v -> v.subject().contains("GeneratedConsumerDependencyAccess.java")),
                "Generated dependency-access source must not be reported as hand-written consumer code");
        assertFalse(violations.stream().anyMatch(v -> v.subject().contains("AppBootstrap.java")),
                "AppBootstrap composition boundary must not produce violations");
    }

    // Fixtures

    interface RetiredContract extends IDependencyInjectableInterface {
    }

    static final class BothRetiredMarkers implements RetiredContract, IDependencyInjectableConcrete {
    }

    public interface ValidSampleService {
        void execute();
    }

    @DelegatesTo(ValidSampleService.class)
    public static final class ValidSampleServiceImpl implements ValidSampleService {
        @Override
        public void execute() {
        }
    }

    public interface ValidSampleConsumerContract {
        ValidSampleService sampleService();
    }

    @DelegatesTo(ValidSampleConsumerContract.class)
    public static final class ValidSampleConsumer implements ValidSampleConsumerContract {
        private final ValidSampleService sampleService;

        public ValidSampleConsumer(ValidSampleService sampleService) {
            this.sampleService = sampleService;
        }

        @Override
        public ValidSampleService sampleService() {
            return sampleService;
        }
    }

    public static final class BadConcreteConsumer {
        private final ValidSampleServiceImpl concreteService;

        public BadConcreteConsumer(ValidSampleServiceImpl concreteService) {
            this.concreteService = concreteService;
        }
    }

    public static final class BadMapConsumer {
        private final IDependencyMap map = null;
    }

    @DelegatesTo
    public static final class GeneratedAccessFixtureDependencyAccess implements IDependencyAccess {
        private final IDependencyMap dependencyMap = DependencyMap.getDependencyMap();

        @Override
        public IDependencyMap getDependencyMap() {
            return dependencyMap;
        }
    }

    public interface GeneratedAccessConsumerContract {
        IDependencyAccess dependencyAccess();
    }

    @DelegatesTo(GeneratedAccessConsumerContract.class)
    public static final class GeneratedAccessConsumer implements GeneratedAccessConsumerContract {
        private final GeneratedAccessFixtureDependencyAccess dependencyAccess;

        public GeneratedAccessConsumer(GeneratedAccessFixtureDependencyAccess dependencyAccess) {
            this.dependencyAccess = dependencyAccess;
        }

        @Override
        public IDependencyAccess dependencyAccess() {
            return dependencyAccess;
        }
    }

    public interface InvalidApiContract {
        ValidSampleServiceImpl resolveImplementation();
    }

    public static final class UnmanagedTestService implements ValidSampleService {
        @Override
        public void execute() {
        }
    }

    @DelegatesTo
    public static final class NoInterfaceTestService {
    }

    @ExplicitNonDi(reason = "Specialized non-DI test harness service")
    public static final class ExplicitNonDiTestService {
    }

    @CompositionBoundary("ROOT")
    public static final class SampleCompositionRoot {
        private final IDependencyMap dependencyMap = null;
        private final ValidSampleServiceImpl implementation = null;
    }
}
