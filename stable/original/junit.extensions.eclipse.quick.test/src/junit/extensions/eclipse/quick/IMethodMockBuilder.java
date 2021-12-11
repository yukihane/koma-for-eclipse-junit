package junit.extensions.eclipse.quick;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.eclipse.jdt.core.Flags;
import org.eclipse.jdt.core.IAnnotation;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.JavaModelException;

public class IMethodMockBuilder {

    private final IMethod element = mock(IMethod.class);
    private int flags;

    public IMethodMockBuilder returnVoid() {
        try {
            when(element.getReturnType()).thenReturn("V");
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public IMethodMockBuilder setName(final String name) {
        when(element.getElementName()).thenReturn(name);
        return this;
    }

    public IMethodMockBuilder addTestAnnotation() {
        try {
            when(element.getSource()).thenReturn("@Test public void should_normal(){\n\n}");
            final IAnnotation annotation = mock(IAnnotation.class);
            when(annotation.getElementName()).thenReturn("org.junit.Test");
            final IAnnotation[] annotations = new IAnnotation[] { annotation };
            when(element.getAnnotations()).thenReturn(annotations);
        } catch (final JavaModelException e) {
        }
        return this;
    }

    public IMethodMockBuilder setNumberOfParameters(final int i) {
        when(element.getNumberOfParameters()).thenReturn(i);
        return this;
    }

    public IMethodMockBuilder setPublic() {
        flags |= Flags.AccPublic;
        try {
            when(element.getFlags()).thenReturn(flags);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public IMethodMockBuilder setPrivate() {
        flags |= Flags.AccPrivate;
        try {
            when(element.getFlags()).thenReturn(Flags.AccPrivate);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public IMethodMockBuilder setProtected() {
        flags ^= Flags.AccPublic;
        flags |= Flags.AccProtected;
        try {
            when(element.getFlags()).thenReturn(flags);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public IMethodMockBuilder setStatic() {
        try {
            flags |= Flags.AccStatic;
            when(element.getFlags()).thenReturn(flags);
        } catch (final JavaModelException e) {
            e.printStackTrace();
        }
        return this;
    }

    public IMethod build() {
        return element;
    }

    public IMethodMockBuilder junit4_method() {
        setPublic();
        returnVoid();
        setName("junit4");
        addTestAnnotation();
        return this;
    }

    public IMethodMockBuilder junit3_method() {
        setPublic();
        returnVoid();
        setName("test_normal");
        return this;
    }

    public IMethodMockBuilder normal_method() {
        setPublic();
        returnVoid();
        setName("normal");
        return this;
    }

}
