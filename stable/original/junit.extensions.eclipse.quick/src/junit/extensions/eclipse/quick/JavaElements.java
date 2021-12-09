package junit.extensions.eclipse.quick;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMember;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class JavaElements {
    public static boolean isTestMethod(final IJavaElement element) throws JavaModelException {
        if (!(element instanceof IMethod))
            return false;
        final IMethod method = (IMethod) element;
        if (!method.getReturnType().equals("V"))
            return false;
        final int flags = method.getFlags();
        if (Flags.isPrivate(flags) || Flags.isStatic(flags))
            return false;
        if (method.getElementName().startsWith("test"))
            return true;

        return hasTestAnnotationOnMethod(method);
    }

    public static IType getPrimaryTypeOf(final IJavaElement element) {
        if (element == null)
            return null;
        ICompilationUnit cu = null;
        if (element instanceof ICompilationUnit) {
            cu = (ICompilationUnit) element;
        } else if (element instanceof IMember) {
            cu = ((IMember) element).getCompilationUnit();
        }
        return cu != null ? cu.findPrimaryType() : null;
    }

    public static boolean isTestClass(final IType type) throws JavaModelException {
        final ITypeHierarchy superTypeHierarchy = type.newSupertypeHierarchy(null);
        final IType superTypes[] = superTypeHierarchy.getAllInterfaces();
        for (int i = 0; i < superTypes.length; ++i) {
            final IType superType = superTypes[i];
            if (superType.getFullyQualifiedName().equals(JavaTypes.TEST_INTERFACE_NAME))
                return true;
        }
        return false;
    }

    public static IJavaElement getTestMethodOrClass(IJavaElement element)
        throws JavaModelException {
        while (element != null) {
            if (isTestMethod(element)) {
                final IType declaringType = ((IMethod) element).getDeclaringType();
                if (hasParameterizedAnnotation(declaringType)) {
                    return declaringType;
                }
                return element;
            }

            if (isTestRunnerPassibleClass(element)) {
                final IType type = (IType) element;
                if (isTestClass(type))
                    return element;
                if (hasSuiteMethod(type))
                    return element;
                if (hasSuiteAnnotation(type))
                    return element;
                if (hasTestAnnotation(type))
                    return element;
            }
            element = element.getParent();
        }
        return null;
    }

    private static boolean hasParameterizedAnnotation(final IType type) throws JavaModelException {
        final String source = type.getSource();
        if (source == null)
            return false;
        return source.indexOf("Parameterized") != -1;
    }

    private static boolean hasSuiteAnnotation(final IType type) throws JavaModelException {
        final String source = type.getSource();
        if (source == null)
            return false;
        return source.indexOf("@SuiteClasses") != -1 && source.indexOf("Suite.class") != -1;
    }

    private static boolean isTestRunnerPassibleClass(final IJavaElement element) throws JavaModelException {
        if (!(element instanceof IType))
            return false;
        final IType type = (IType) element;
        if (!type.isClass())
            return false;
        final int flags = type.getFlags();
        if (Flags.isAbstract(flags) || !Flags.isPublic(flags))
            return false;

        return true;
    }

    private static boolean hasSuiteMethod(final IType type) throws JavaModelException {
        final IMethod[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (isStaticSuiteMethod(methods[i]))
                return true;
        }
        return false;
    }

    private static boolean hasTestAnnotation(final IType type) throws JavaModelException {
        final IMethod[] methods = type.getMethods();
        for (int i = 0; i < methods.length; i++) {
            if (hasTestAnnotationOnMethod(methods[i]))
                return true;
        }
        return false;
    }

    private static boolean hasTestAnnotationOnMethod(final IMethod method) throws JavaModelException {
        return method.getSource() == null ? false : method.getSource().indexOf("@Test") > -1;
    }

    private static boolean isStaticSuiteMethod(final IMethod method) throws JavaModelException {
        return ((method.getElementName().equals("suite")) &&
            method.getSignature().equals("()QTest;") &&
            Flags.isPublic(method.getFlags()) &&
            Flags.isStatic(method.getFlags()));
    }
}
