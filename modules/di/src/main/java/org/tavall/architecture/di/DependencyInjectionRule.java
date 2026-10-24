package org.tavall.architecture.di;

import org.tavall.architecture.core.ArchitectureContext;
import org.tavall.architecture.core.ArchitectureRule;
import org.tavall.architecture.core.ArchitectureViolation;
import org.tavall.architecture.core.ProductionClass;
import org.tavall.dependency.annotations.CompositionBoundary;
import org.tavall.dependency.annotations.DelegatesTo;
import org.tavall.dependency.annotations.ExplicitNonDi;
import org.tavall.dependency.architecture.DiArchitectureSemantics;
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
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Enforces the canonical Tavall DI architecture doctrine:
 * <pre>
 * Tavall-owned behavior is interface-first and DI-managed by default.
 * behavior -> interface / contract -> tavall-di -> implementation
 * </pre>
 *
 * Catches:
 * 1. Retired DI markers
 * 2. @DelegatesTo declaration validity
 * 3. Behavioral Tavall components that bypass DI or miss interface contracts
 * 4. Concrete Tavall implementation dependencies where an interface/contract should be used
 * 5. Ordinary production code directly accessing IDependencyMap / DependencyMap
 * 6. Direct construction of DI-managed implementations outside approved composition boundaries
 * 7. Invalid dependency direction between API contracts and implementations
 */
public final class DependencyInjectionRule implements ArchitectureRule {

    private static final Set<String> RETIRED_DI_MARKERS = Set.of(
            "org.tavall.dependency.IDependencyInjectableConcrete",
            "org.tavall.dependency.IDependencyInjectableInterface"
    );

    private static final Pattern NEW_INSTANCE_PATTERN = Pattern.compile(
            "\\bnew\\s+([A-Za-z0-9_]+(?:<[^>]*>)?)\\s*\\("
    );

    private static final Pattern DIRECT_MAP_PATTERN = Pattern.compile(
            "\\b(?:IDependencyMap|DependencyMap\\.getDependencyMap\\(\\)|DependencyMap)\\b"
    );

    @Override
    public String id() {
        return "tavall-di";
    }

    @Override
    public List<ArchitectureViolation> validate(ArchitectureContext context) {
        List<ArchitectureViolation> violations = new ArrayList<>();
        Set<Class<?>> diManagedClasses = new HashSet<>();

        // Phase 1: Collect DI-managed types and validate retired markers & @DelegatesTo
        for (ProductionClass productionClass : context.productionClasses()) {
            if (!productionClass.loaded()) {
                continue;
            }
            Class<?> type = productionClass.type();
            if (type.isSynthetic()) {
                continue;
            }

            Set<String> retiredMarkers = findRetiredMarkers(type, new HashSet<>());
            if (!retiredMarkers.isEmpty()) {
                violations.add(new ArchitectureViolation(
                        "retired-di-marker",
                        type.getName(),
                        "Production type still depends on retired DI markers "
                                + retiredMarkers.stream().sorted().collect(Collectors.joining(", "))
                ));
            }

            DelegatesTo delegatesTo = type.getAnnotation(DelegatesTo.class);
            if (delegatesTo != null) {
                auditDelegation(type, delegatesTo, violations);
            }

            if (DiArchitectureSemantics.isDiManaged(type)) {
                diManagedClasses.add(type);
            }
        }

        // Phase 2: Class-level structural and dependency direction checks
        for (ProductionClass productionClass : context.productionClasses()) {
            if (!productionClass.loaded()) {
                continue;
            }
            Class<?> type = productionClass.type();
            if (type.isSynthetic() || type.isAnnotation() || type.isAnonymousClass()) {
                continue;
            }

            if (type.getName().startsWith("org.tavall.")) {
                auditBehavioralComponent(type, violations);
                auditDirectMapUsageInClass(type, violations);
                auditConsumerDependencies(type, violations);
                auditApiDependencyDirection(type, violations);
            }
        }

        // Phase 3: Source-level checks (direct map access & direct construction)
        Set<String> diManagedSimpleNames = diManagedClasses.stream()
                .map(Class::getSimpleName)
                .collect(Collectors.toSet());

        for (Path sourceRoot : context.sourceRoots()) {
            scanSources(sourceRoot, diManagedSimpleNames, violations);
        }

        return List.copyOf(violations);
    }

    private static void auditBehavioralComponent(Class<?> type, List<ArchitectureViolation> violations) {
        if (type.isInterface() || Modifier.isAbstract(type.getModifiers())) {
            return;
        }
        if (DiArchitectureSemantics.isExplicitDiException(type)
                || DiArchitectureSemantics.isApprovedCompositionBoundary(type)) {
            return;
        }

        if (DiArchitectureSemantics.isBehavioralComponent(type)) {
            // Must participate in DI
            if (!DiArchitectureSemantics.isDiManaged(type)) {
                violations.add(new ArchitectureViolation(
                        "unmanaged-behavioral-component",
                        type.getName(),
                        "Tavall behavioral component does not participate in tavall-di; annotate with @DelegatesTo or @ExplicitNonDi if an exception is justified"
                ));
            }

            // Must implement an interface contract
            if (!implementsDomainInterface(type)) {
                violations.add(new ArchitectureViolation(
                        "behavior-missing-interface",
                        type.getName(),
                        "Tavall behavioral component must implement an interface contract"
                ));
            }
        }
    }

    private static void auditDirectMapUsageInClass(Class<?> type, List<ArchitectureViolation> violations) {
        if (DiArchitectureSemantics.isApprovedCompositionBoundary(type)
                || type.getName().startsWith("org.tavall.dependency.")) {
            return;
        }

        for (Field field : type.getDeclaredFields()) {
            if (isDependencyMapType(field.getType())) {
                violations.add(new ArchitectureViolation(
                        "direct-dependency-map-access",
                        type.getName(),
                        "Ordinary production code must not reference IDependencyMap or DependencyMap directly; use typed DependencyAccess<...> instead"
                ));
                return;
            }
        }

        for (Method method : type.getDeclaredMethods()) {
            if (isDependencyMapType(method.getReturnType())) {
                violations.add(new ArchitectureViolation(
                        "direct-dependency-map-access",
                        type.getName(),
                        "Ordinary production code must not reference IDependencyMap or DependencyMap directly; use typed DependencyAccess<...> instead"
                ));
                return;
            }
            for (Class<?> paramType : method.getParameterTypes()) {
                if (isDependencyMapType(paramType)) {
                    violations.add(new ArchitectureViolation(
                            "direct-dependency-map-access",
                            type.getName(),
                            "Ordinary production code must not reference IDependencyMap or DependencyMap directly; use typed DependencyAccess<...> instead"
                    ));
                    return;
                }
            }
        }
    }

    private static void auditConsumerDependencies(Class<?> consumer, List<ArchitectureViolation> violations) {
        if (DiArchitectureSemantics.isApprovedCompositionBoundary(consumer)
                || DiArchitectureSemantics.isExplicitDiException(consumer)
                || consumer.getName().startsWith("org.tavall.dependency.")) {
            return;
        }

        for (Field field : consumer.getDeclaredFields()) {
            if (field.isSynthetic() || Modifier.isStatic(field.getModifiers())) {
                continue;
            }
            Class<?> fieldType = field.getType();
            if (isProhibitedConcreteDependency(fieldType)) {
                violations.add(new ArchitectureViolation(
                        "concrete-implementation-dependency",
                        consumer.getName() + "->" + fieldType.getName(),
                        "Consumer depends directly on concrete Tavall implementation " + fieldType.getName() + " instead of an interface contract"
                ));
            }
        }

        for (Constructor<?> constructor : consumer.getDeclaredConstructors()) {
            for (Class<?> paramType : constructor.getParameterTypes()) {
                if (isProhibitedConcreteDependency(paramType)) {
                    violations.add(new ArchitectureViolation(
                            "concrete-implementation-dependency",
                            consumer.getName() + "->" + paramType.getName(),
                            "Consumer constructor parameter depends directly on concrete Tavall implementation " + paramType.getName() + " instead of an interface contract"
                    ));
                }
            }
        }
    }

    private static void auditApiDependencyDirection(Class<?> type, List<ArchitectureViolation> violations) {
        boolean isApiPackage = type.getName().contains(".api.") || type.getPackageName().endsWith(".api");
        boolean isContractInterface = type.isInterface()
                && !type.getName().contains(".impl.")
                && !type.getName().contains(".internal.");

        if (!isApiPackage && !isContractInterface) {
            return;
        }

        for (Method method : type.getDeclaredMethods()) {
            checkApiReference(type, method.getReturnType(), violations);
            for (Class<?> paramType : method.getParameterTypes()) {
                checkApiReference(type, paramType, violations);
            }
        }

        for (Class<?> iface : type.getInterfaces()) {
            checkApiReference(type, iface, violations);
        }
    }

    private static void checkApiReference(Class<?> apiType, Class<?> target, List<ArchitectureViolation> violations) {
        if (target == null || target.isPrimitive() || target.isArray()) {
            return;
        }
        if (!target.getName().startsWith("org.tavall.")) {
            return;
        }
        if (target.getName().contains(".impl.")
                || target.getName().contains(".internal.")
                || (target.getSimpleName().endsWith("Impl") && !target.isInterface())) {
            violations.add(new ArchitectureViolation(
                    "invalid-api-dependency-direction",
                    apiType.getName() + "->" + target.getName(),
                    "API contract " + apiType.getName() + " must not depend on implementation type " + target.getName()
            ));
        }
    }

    private static boolean isProhibitedConcreteDependency(Class<?> target) {
        if (target == null || target.isPrimitive() || target.isArray()) {
            return false;
        }
        if (!target.getName().startsWith("org.tavall.")) {
            return false;
        }
        if (target.isInterface() || Modifier.isAbstract(target.getModifiers())) {
            return false;
        }
        if (DiArchitectureSemantics.isExplicitDiException(target)) {
            return false;
        }
        return DiArchitectureSemantics.isDiManaged(target)
                || target.getName().contains(".impl.")
                || target.getName().contains(".internal.")
                || implementsDomainInterface(target);
    }

    private static boolean isDependencyMapType(Class<?> type) {
        return type == IDependencyMap.class || type == DependencyMap.class;
    }

    private static boolean implementsDomainInterface(Class<?> type) {
        Class<?> current = type;
        while (current != null && current != Object.class) {
            for (Class<?> iface : current.getInterfaces()) {
                if (!iface.getPackageName().startsWith("java.")
                        && !iface.getName().equals("org.tavall.dependency.IDependencyAccess")
                        && !iface.getName().startsWith("org.tavall.dependency.DependencyAccess")) {
                    return true;
                }
            }
            current = current.getSuperclass();
        }
        return false;
    }

    private static void auditDelegation(
            Class<?> concreteType,
            DelegatesTo delegatesTo,
            List<ArchitectureViolation> violations
    ) {
        if (concreteType.isInterface() || Modifier.isAbstract(concreteType.getModifiers())) {
            violations.add(new ArchitectureViolation(
                    "delegation-concrete",
                    concreteType.getName(),
                    "@DelegatesTo is declared on a type that is not concrete"
            ));
        }
        Set<Class<?>> aliases = new HashSet<>();
        Arrays.stream(delegatesTo.value()).forEach(alias -> {
            if (!aliases.add(alias)) {
                violations.add(new ArchitectureViolation(
                        "delegation-duplicate",
                        concreteType.getName() + "->" + alias.getName(),
                        "Delegation alias is repeated"
                ));
            }
            if (!alias.isAssignableFrom(concreteType)) {
                violations.add(new ArchitectureViolation(
                        "delegation-assignability",
                        concreteType.getName() + "->" + alias.getName(),
                        "Concrete type cannot satisfy delegation alias"
                ));
            }
        });
    }

    private static Set<String> findRetiredMarkers(Class<?> type, Set<Class<?>> visited) {
        if (type == null || !visited.add(type)) {
            return Set.of();
        }
        Set<String> markers = new HashSet<>();
        for (Class<?> implementedInterface : type.getInterfaces()) {
            if (RETIRED_DI_MARKERS.contains(implementedInterface.getName())) {
                markers.add(implementedInterface.getName());
            }
            markers.addAll(findRetiredMarkers(implementedInterface, visited));
        }
        markers.addAll(findRetiredMarkers(type.getSuperclass(), visited));
        return Set.copyOf(markers);
    }

    private static void scanSources(Path root, Set<String> diManagedSimpleNames, List<ArchitectureViolation> violations) {
        try (Stream<Path> paths = Files.walk(root)) {
            paths.filter(Files::isRegularFile)
                    .filter(path -> path.getFileName().toString().endsWith(".java"))
                    .sorted()
                    .forEach(path -> inspectSource(root, path, diManagedSimpleNames, violations));
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to scan Java source root " + root, exception);
        }
    }

    private static void inspectSource(
            Path root,
            Path path,
            Set<String> diManagedSimpleNames,
            List<ArchitectureViolation> violations
    ) {
        try {
            String rawSource = Files.readString(path);
            String source = stripComments(rawSource);
            String subject = root.relativize(path).toString().replace('\\', '/');

            // Skip approved composition boundaries or explicit exceptions
            if (isSourceCompositionBoundary(subject, source)) {
                return;
            }

            // Direct map usage check in ordinary source
            if (DIRECT_MAP_PATTERN.matcher(source).find()) {
                violations.add(new ArchitectureViolation(
                        "direct-dependency-map-access",
                        subject,
                        "Ordinary production code must not reference IDependencyMap or DependencyMap directly; use typed DependencyAccess<...> instead"
                ));
            }

            // Direct construction of DI-managed implementations check
            Matcher newMatcher = NEW_INSTANCE_PATTERN.matcher(source);
            while (newMatcher.find()) {
                String constructedType = newMatcher.group(1);
                int genericIdx = constructedType.indexOf('<');
                if (genericIdx > 0) {
                    constructedType = constructedType.substring(0, genericIdx);
                }
                String simpleName = constructedType;
                int dotIdx = simpleName.lastIndexOf('.');
                if (dotIdx >= 0) {
                    simpleName = simpleName.substring(dotIdx + 1);
                }

                if (isProhibitedConstructedType(simpleName, diManagedSimpleNames)) {
                    violations.add(new ArchitectureViolation(
                            "direct-construction-di-managed",
                            subject + "->" + simpleName,
                            "Direct construction of DI-managed implementation " + simpleName + " outside approved composition boundaries"
                    ));
                }
            }
        } catch (IOException exception) {
            throw new IllegalStateException("Failed to read production source " + path, exception);
        }
    }

    private static boolean isSourceCompositionBoundary(String subject, String source) {
        if (subject.startsWith("org/tavall/dependency/")) {
            return true;
        }
        if (subject.contains("/bootstrap/") || subject.contains("/composition/")) {
            return true;
        }
        if (subject.endsWith("Bootstrap.java")
                || subject.endsWith("CompositionRoot.java")
                || subject.endsWith("Factory.java")) {
            return true;
        }
        return source.contains("@CompositionBoundary") || source.contains("@ExplicitNonDi");
    }

    private static boolean isProhibitedConstructedType(String simpleName, Set<String> diManagedSimpleNames) {
        if (simpleName.endsWith("Builder")
                || simpleName.endsWith("Exception")
                || simpleName.endsWith("Event")
                || simpleName.endsWith("Data")
                || simpleName.endsWith("State")
                || simpleName.endsWith("Result")
                || simpleName.endsWith("Config")
                || simpleName.endsWith("Configuration")
                || simpleName.endsWith("Model")
                || simpleName.endsWith("DTO")
                || simpleName.endsWith("Dto")
                || simpleName.endsWith("Factory")) {
            return false;
        }
        if (diManagedSimpleNames.contains(simpleName)) {
            return true;
        }
        return simpleName.endsWith("ServiceImpl")
                || simpleName.endsWith("HandlerImpl")
                || simpleName.endsWith("OrchestratorImpl")
                || simpleName.endsWith("ProviderImpl")
                || simpleName.endsWith("ResolverImpl")
                || simpleName.endsWith("AdapterImpl")
                || simpleName.endsWith("GatewayImpl");
    }

    private static String stripComments(String source) {
        return source.replaceAll("/\\*.*?\\*/", "").replaceAll("//.*", "");
    }
}
