package junit.extensions.eclipse.quick.process.internal;

import java.io.IOException;
import junit.extensions.eclipse.quick.process.internal.preferences.Preference;
import org.eclipse.jdt.junit.ITestRunListener;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;

@SuppressWarnings("deprecation")
public class TestListenerForProcess implements ITestRunListener {

    private static final String QUICK_J_UNIT = "Quick JUnit ";
    private static final String TEST_OK = QUICK_J_UNIT + "Test OK";
    private static final String TEST_FAILURE = QUICK_J_UNIT + "Test FAILURE";
    private static final String TEST_ERROR = QUICK_J_UNIT + "Test ERROR";

    public TestListenerForProcess() {

        JUnitCore.addTestRunListener(new org.eclipse.jdt.junit.TestRunListener() {
            private final TemplateParser tmpParser = new TemplateParser();
            private final ProcessParser processParser = new ProcessParser();

            @Override
            public void sessionFinished(final ITestRunSession session) {
                final String template = Preference.TEMPLATE.getValue();
                tmpParser.setTemplate(template);
                final Result testResult = session.getTestResult(true);
                String summary;
                if (Result.ERROR.equals(testResult)) {
                    summary = TEST_ERROR;
                } else if (Result.FAILURE.equals(testResult)) {
                    summary = TEST_FAILURE;
                } else {
                    summary = TEST_OK;
                }
                final String detail = tmpParser.parseTemplate(session);
                final String command = Preference.PROCESS.getValue();
                final String[] parsed = processParser.parse(command, summary, detail);
                final ProcessBuilder builder = new ProcessBuilder(parsed);
                try {
                    builder.start();
                } catch (final IOException e) {
                }
            }
        });
    }

    @Override
    public void testEnded(final String testId, final String testName) {
    }

    @Override
    public void testFailed(final int status, final String testId, final String testName,
        final String trace) {
    }

    @Override
    public void testReran(final String testId, final String testClass, final String testName,
        final int status, final String trace) {
    }

    @Override
    public void testRunEnded(final long elapsedTime) {
    }

    @Override
    public void testRunStarted(final int testCount) {
    }

    @Override
    public void testRunStopped(final long elapsedTime) {
    }

    @Override
    public void testRunTerminated() {
    }

    @Override
    public void testStarted(final String testId, final String testName) {
    }

}
