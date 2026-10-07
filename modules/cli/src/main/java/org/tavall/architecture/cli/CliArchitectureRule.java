package org.tavall.architecture.cli;

import org.tavall.architecture.core.ArchitectureContext;
import org.tavall.architecture.core.ArchitectureRule;
import org.tavall.architecture.core.ArchitectureViolation;
import org.tavall.architecture.core.ProductionClass;
import org.tavall.dependency.annotations.DelegatesTo;
import org.tavall.dependency.maps.DependencyMap;
import org.tavall.dependency.maps.interfaces.IDependencyMap;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Stream;

/**
 * Enforces Tavall CLI architecture doctrine:
 * 1. Commands extending CLICommand must implement a domain interface.
 * 2. Commands extending CLICommand must declare @DelegatesTo.
 * 3. Commands must not declare constructors taking managed collaborators (no constructor injection).
 * 4. Commands must not call IDependencyMap directly; must use DependencyAccess.
 */
public final class CliArchitectureRule implements ArchitectureRule {

    private static final String CLI_COMMAND_BASE = "org.tavall.cli.api.command.CLICommand";
    private static final String ICLI_COMMAND_INTERFACE = "org.tavall.cli.api.command.ICLICommand";

    private static final Pattern DIRECT_MAP_PATTERN = Pattern.compile(
            "\\b(?:IDependencyMap|DependencyMap\\.getDependencyMap\\(\\)|DependencyMap)\\b"
    );

    @Override
    public String id() {
        return "tavall-cli";
    }

    @Override
    public List<ArchitectureViolation> validate(ArchitectureContext context) {
        List<ArchitectureViolation> violations = new ArrayList<>();
        Set<Class<?>> cliCommandClasses = new HashSet<>();

        for (ProductionClass productionClass : context.productionClasses()) {
            if (!productionClass.loaded()) {
                continue;
            }
            Class<?> type = productionClass.type();
            if (type.isSynthetic() || type.isAnnotation() || type.isAnonymousClass()) {
                continue;
            }

            if (isConcreteCliCommand(type)) {
                cliCommandClasses.add(type);
                auditCliCommand(type, violations);
            }
        }

        // Source scanning for direct map access in CLI commands
        for (Path sourceRoot : context.sourceRoots()) {
            scanSources(sourceRoot, cliCommandClasses, violations);
        }

        return List.copyOf(violations);
    }

    private static boolean isConcreteCliCommand(Class<?> type) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return false;
        }
        return extendsCliCommand(type);
    }

    private static boolean extendsCliCommand(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            if (current.getName().equals(CLI_COMMAND_BASE)) {
                return true;
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private static void auditCliCommand(Class<?> commandType, List<ArchitectureViolation> violations) {
        // 1. Must implement a domain interface (distinct from ICLICommand / DependencyAccess)
        if (!implementsDomainInterface(commandType)) {
            violations.add(new ArchitectureViolation(
                    "cli-command-missing-interface",
                    commandType.getName(),
                    "CLI command " + commandType.getSimpleName() + " must implement a typed domain interface contract"
            ));
        }

        // 2. Must declare @DelegatesTo
        DelegatesTo delegatesTo = commandType.getAnnotation(DelegatesTo.class);
        if (delegatesTo == null) {
            violations.add(new ArchitectureViolation(
                    "cli-command-missing-delegatesto",
                    commandType.getName(),
                    "CLI command " + commandType.getSimpleName() + " must declare @DelegatesTo for DI registration"
            ));
        }

        // 3. Must not declare constructors taking managed collaborators (no constructor injection)
        for (Constructor<?> constructor : commandType.getDeclaredConstructors()) {
            if (constructor.getParameterCount() > 0) {
                for (Class<?> paramType : constructor.getParameterTypes()) {
                    if (isManagedCollaborator(paramType)) {
                        violations.add(new ArchitectureViolation(
                                "cli-command-constructor-injection",
                                commandType.getName() + "->" + paramType.getName(),
                                "CLI command " + commandType.getSimpleName() + " must not declare constructors taking managed collaborators ("
                                        + paramType.getSimpleName() + "); use Tavall DI (DependencyAccess) instead"
                        ));
                    }
                }
            }
        }

        // 4. Must not use IDependencyMap / DependencyMap directly in fields or methods
        auditDirectMapUsage(commandType, violations);
    }

    private static boolean implementsDomainInterface(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Class<?> iface : current.getInterfaces()) {
                String name = iface.getName();
                if (!name.startsWith("java.")
                        && !name.equals(ICLI_COMMAND_INTERFACE)
                        && !name.startsWith("org.tavall.dependency.DependencyAccess")
                        && !name.startsWith("org.tavall.dependency.IDependencyAccess")
                        && !name.startsWith("org.tavall.cli.api.command.ICLICommand")) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private static boolean isManagedCollaborator(Class<?> type) {
        if (type.isPrimitive()) return false;
        if (type == String.class || type == Path.class || Number.class.isAssignableFrom(type) || type == Boolean.class) {
            return false;
        }
        if (type.getName().startsWith("org.tavall.cli.api.command.data.")
                || type.getName().startsWith("org.tavall.cli.api.command.CLICommandPath")
                || type.getName().startsWith("org.tavall.cli.api.command.argument.")
                || type.getName().startsWith("org.tavall.cli.api.command.option.")) {
            return false;
        }
        return type.isInterface() || type.getName().startsWith("org.tavall.");
    }

    private static void auditDirectMapUsage(Class<?> commandType, List<ArchitectureViolation> violations) {
        for (Field field : commandType.getDeclaredFields()) {
            if (isDependencyMapType(field.getType())) {
                violations.add(new ArchitectureViolation(
                        "cli-command-direct-map-access",
                        commandType.getName(),
                        "CLI command must not reference IDependencyMap or DependencyMap directly; use DependencyAccess instead"
                ));
                return;
            }
        }
        for (Method method : commandType.getDeclaredMethods()) {
            if (isDependencyMapType(method.getReturnType())) {
                violations.add(new ArchitectureViolation(
                        "cli-command-direct-map-access",
                        commandType.getName(),
                        "CLI command must not reference IDependencyMap or DependencyMap directly; use DependencyAccess instead"
                ));
                return;
            }
            for (Class<?> p : method.getParameterTypes()) {
                if (isDependencyMapType(p)) {
                    violations.add(new ArchitectureViolation(
                            "cli-command-direct-map-access",
                            commandType.getName(),
                            "CLI command must not reference IDependencyMap or DependencyMap directly; use DependencyAccess instead"
                    ));
                    return;
                }
            }
        }
    }

    private static boolean isDependencyMapType(Class<?> type) {
        return type == IDependencyMap.class || type == DependencyMap.class;
    }

    private static void scanSources(Path root, Set<Class<?>> cliCommandClasses, List<ArchitectureViolation> violations) {
        Set<String> simpleNames = new HashSet<>();
        for (Class<?> clazz : cliCommandClasses) {
            simpleNames.add(clazz.getSimpleName());
        }
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .forEach(path -> {
                        String fileName = path.getFileName().toString();
                        String className = fileName.substring(0, fileName.length() - 5);
                        if (simpleNames.contains(className)) {
                            inspectCommandSource(root, path, violations);
                        }
                    });
        } catch (IOException ignored) {
        }
    }

    private static void inspectCommandSource(Path root, Path path, List<ArchitectureViolation> violations) {
        try {
            String raw = Files.readString(path);
            String source = raw.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "");
            String subject = root.relativize(path).toString().replace('\\', '/');

            if (DIRECT_MAP_PATTERN.matcher(source).find()) {
                violations.add(new ArchitectureViolation(
                        "cli-command-direct-map-access",
                        subject,
                        "CLI command must not reference IDependencyMap or DependencyMap directly; use DependencyAccess instead"
                ));
            }
        } catch (IOException ignored) {
        }
    }
}
