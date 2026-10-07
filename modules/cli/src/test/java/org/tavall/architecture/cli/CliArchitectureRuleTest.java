package org.tavall.architecture.cli;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.tavall.architecture.core.ArchitectureContext;
import org.tavall.architecture.core.ArchitectureViolation;
import org.tavall.cli.api.command.CLICommand;
import org.tavall.cli.api.command.ICLICommand;
import org.tavall.cli.api.command.builder.CLICommandBuilder;
import org.tavall.cli.api.command.data.CLICommandInput;
import org.tavall.dependency.DependencyAccess;
import org.tavall.dependency.annotations.DelegatesTo;
import org.tavall.dependency.maps.interfaces.IDependencyMap;

import java.nio.file.Path;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

final class CliArchitectureRuleTest {

    private String previousClassRoots;

    @BeforeEach
    void setUp() {
        previousClassRoots = System.getProperty("tavall.architecture.classRoots");
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
    }

    @Test
    void allowsValidCliCommands() {
        List<ArchitectureViolation> violations = new CliArchitectureRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.subject().contains(ValidTestCLICommand.class.getName()))
                .toList();

        assertTrue(violations.isEmpty(), "Valid CLI command must produce zero violations: " + violations);
    }

    @Test
    void catchesMissingDomainInterface() {
        List<ArchitectureViolation> violations = new CliArchitectureRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("cli-command-missing-interface")
                        && v.subject().equals(MissingInterfaceCLICommand.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void catchesMissingDelegatesTo() {
        List<ArchitectureViolation> violations = new CliArchitectureRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("cli-command-missing-delegatesto")
                        && v.subject().equals(MissingDelegatesToCLICommand.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    @Test
    void catchesConstructorInjectionOfManagedCollaborators() {
        List<ArchitectureViolation> violations = new CliArchitectureRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("cli-command-constructor-injection")
                        && v.subject().startsWith(ConstructorInjectionCLICommand.class.getName()))
                .toList();

        assertEquals(1, violations.size());
        assertTrue(violations.getFirst().subject().contains(ITestCollaborator.class.getName()));
    }

    @Test
    void catchesDirectMapUsage() {
        List<ArchitectureViolation> violations = new CliArchitectureRule()
                .validate(ArchitectureContext.fromSystemProperties())
                .stream()
                .filter(v -> v.ruleId().equals("cli-command-direct-map-access")
                        && v.subject().equals(DirectMapCLICommand.class.getName()))
                .toList();

        assertEquals(1, violations.size());
    }

    // Fixtures

    public interface ITestCollaborator {
        void execute();
    }

    public interface IValidTestCLICommand extends ICLICommand<String> {
    }

    @DelegatesTo(IValidTestCLICommand.class)
    public static final class ValidTestCLICommand extends CLICommand<String>
            implements IValidTestCLICommand, DependencyAccess<ITestCollaborator> {

        public ValidTestCLICommand() {
            super(CLICommandBuilder.create("valid").description("valid test command").build());
        }

        @Override
        protected String handle(CLICommandInput input) {
            return "ok";
        }
    }

    @DelegatesTo(ICLICommand.class)
    public static final class MissingInterfaceCLICommand extends CLICommand<String>
            implements DependencyAccess<ITestCollaborator> {

        public MissingInterfaceCLICommand() {
            super(CLICommandBuilder.create("missing-interface").description("missing interface").build());
        }

        @Override
        protected String handle(CLICommandInput input) {
            return "missing-interface";
        }
    }

    public interface IMissingDelegatesToCLICommand extends ICLICommand<String> {
    }

    public static final class MissingDelegatesToCLICommand extends CLICommand<String>
            implements IMissingDelegatesToCLICommand {

        public MissingDelegatesToCLICommand() {
            super(CLICommandBuilder.create("missing-delegatesto").description("missing delegatesTo").build());
        }

        @Override
        protected String handle(CLICommandInput input) {
            return "missing-delegatesto";
        }
    }

    public interface IConstructorInjectionCLICommand extends ICLICommand<String> {
    }

    @DelegatesTo(IConstructorInjectionCLICommand.class)
    public static final class ConstructorInjectionCLICommand extends CLICommand<String>
            implements IConstructorInjectionCLICommand {

        private final ITestCollaborator collaborator;

        public ConstructorInjectionCLICommand(ITestCollaborator collaborator) {
            super(CLICommandBuilder.create("constructor-injection").description("constructor injection").build());
            this.collaborator = collaborator;
        }

        @Override
        protected String handle(CLICommandInput input) {
            collaborator.execute();
            return "injected";
        }
    }

    public interface IDirectMapCLICommand extends ICLICommand<String> {
    }

    @DelegatesTo(IDirectMapCLICommand.class)
    public static final class DirectMapCLICommand extends CLICommand<String>
            implements IDirectMapCLICommand {

        private final IDependencyMap directMap = null;

        public DirectMapCLICommand() {
            super(CLICommandBuilder.create("direct-map").description("direct map").build());
        }

        @Override
        protected String handle(CLICommandInput input) {
            return "direct-map";
        }
    }
}
