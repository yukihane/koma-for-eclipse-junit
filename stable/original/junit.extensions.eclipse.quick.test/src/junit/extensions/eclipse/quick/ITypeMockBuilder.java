package junit.extensions.eclipse.quick;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyChar;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.List;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.ITypeHierarchy;
import org.eclipse.jdt.core.JavaModelException;

public class ITypeMockBuilder {

    private final IType result = mock(IType.class);
    private int flags;
    private final List<IMethod> methods = new ArrayList<>();
    private final StringBuilder source = new StringBuilder();
    private IImportDeclaration importDeclaration;

    {
        final ITypeHierarchy typeHierarchy = mock(ITypeHierarchy.class);
        when(typeHierarchy.getAllInterfaces()).thenReturn(new IType[] {});
        try {
            when(result.newSupertypeHierarchy((IProgressMonitor) any())).thenReturn(typeHierarchy);
            when(result.isClass()).thenReturn(true);
            when(result.getMethods()).thenReturn(new IMethod[] {});
            final ICompilationUnit compilationUnit = mock(ICompilationUnit.class);
            when(result.getCompilationUnit()).thenReturn(compilationUnit);
            importDeclaration = mock(IImportDeclaration.class);
            when(compilationUnit.getImport(JavaTypes.TEST_ANNOTATION_FULL_NAME)).thenReturn(importDeclaration);
        } catch (final JavaModelException e) {
        }

    }

    public IType build() {
        return result;
    }

    public ITypeMockBuilder setPublic() {
        flags |= Flags.AccPublic;
        try {
            when(result.getFlags()).thenReturn(flags);
        } catch (final JavaModelException e) {
        }
        return this;
    }

    public ITypeMockBuilder normal_class() {
        setPublic();
        return this;
    }

    public ITypeMockBuilder addMethod(final IMethod method) {
        try {
            methods.add(method);
            final IMethod[] methodsArray = methods.toArray(new IMethod[] {});
            when(result.getMethods()).thenReturn(methodsArray);
            when(method.getDeclaringType()).thenReturn(result);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ITypeMockBuilder junit3_class() {
        setPublic();
        final ITypeHierarchy typeHierarchy = mock(ITypeHierarchy.class);
        final IType test = mock(IType.class);
        when(test.getFullyQualifiedName()).thenReturn(JavaTypes.TEST_INTERFACE_NAME);
        when(test.getFullyQualifiedName(anyChar())).thenReturn(JavaTypes.TEST_INTERFACE_NAME);
        when(typeHierarchy.getAllInterfaces()).thenReturn(new IType[] { test });
        try {
            when(result.newSupertypeHierarchy((IProgressMonitor) any())).thenReturn(typeHierarchy);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }

        return this;
    }

    public ITypeMockBuilder setRunWith(final String clazz) {
        try {
            source.append("@RunWith(" + clazz + ")");
            when(result.getSource()).thenReturn(source.toString());
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ITypeMockBuilder setSuiteClasses() {
        try {
            source.append("@SuiteClasses");
            when(result.getSource()).thenReturn(source.toString());
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public ITypeMockBuilder junit4_suite() {
        setPublic();
        setSuiteClasses();
        setRunWith("Suite.class");
        return this;
    }

}
