package junit.extensions.eclipse.quick.notifications.internal;

import org.eclipse.jdt.junit.model.ITestElement;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestElementContainer;
import org.eclipse.jdt.junit.model.ITestRunSession;

public class TestCounter {

    private int totalTests;
    private int okTests;
    private int failureTests;
    private int ignoreTests;
    private int errorTests;

    public TestCounter() {
    }

    public void count(final ITestRunSession session) {
        reset();
        count((ITestElementContainer) session);
    }

    private void reset() {
        totalTests = 0;
        okTests = 0;
        failureTests = 0;
        ignoreTests = 0;
        errorTests = 0;
    }

    private void count(final ITestElementContainer container) {
        final ITestElement[] children = container.getChildren();
        if (children == null)
            return;
        for (final ITestElement element : children) {
            if (element instanceof ITestElementContainer) {
                final ITestElementContainer cont = (ITestElementContainer) element;
                count(cont);
                continue;
            }
            totalTests++;
            final Result result = element.getTestResult(false);
            if (result == null)
                continue;
            if (result.equals(Result.IGNORED))
                ignoreTests++;
            if (result.equals(Result.OK))
                okTests++;
            if (result.equals(Result.FAILURE))
                failureTests++;
            if (result.equals(Result.ERROR))
                errorTests++;
        }
    }

    public int getTotalTests() {
        return totalTests;
    }

    public int getOKTests() {
        return okTests;
    }

    public int getFailureTests() {
        return failureTests;
    }

    public int getIgnoreTests() {
        return ignoreTests;
    }

    public int getErrorTests() {
        return errorTests;
    }

}
