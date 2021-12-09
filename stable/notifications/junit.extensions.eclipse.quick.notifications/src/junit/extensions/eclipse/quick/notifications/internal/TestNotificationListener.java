package junit.extensions.eclipse.quick.notifications.internal;

import java.util.Collections;
import org.eclipse.jdt.junit.JUnitCore;
import org.eclipse.jdt.junit.TestRunListener;
import org.eclipse.jdt.junit.model.ITestElement.Result;
import org.eclipse.jdt.junit.model.ITestRunSession;
import org.eclipse.mylyn.commons.notifications.core.AbstractNotification;
import org.eclipse.mylyn.commons.notifications.ui.NotificationsUi;

@SuppressWarnings("restriction")
public class TestNotificationListener extends TestRunListener {
    private static final String QUICK_JUNIT_NOTIFICATION_EVENT_ID = "junit.extensions.eclipse.quick.notifications.event";

    public TestNotificationListener() {

        JUnitCore.addTestRunListener(new org.eclipse.jdt.junit.TestRunListener() {
            @Override
            public void sessionFinished(final ITestRunSession session) {
                final Result testResult = session.getTestResult(true);
                final AbstractNotification notification = new JUnitNotification(QUICK_JUNIT_NOTIFICATION_EVENT_ID,
                    testResult,
                    session);
                NotificationsUi.getService().notify(Collections.singletonList(notification));
            }
        });
    }

    public void testEnded(final String testId, final String testName) {
    }

    public void testFailed(final int status, final String testId, final String testName,
        final String trace) {
    }

    public void testReran(final String testId, final String testClass, final String testName,
        final int status, final String trace) {
    }

    public void testRunEnded(final long elapsedTime) {
    }

    public void testRunStarted(final int testCount) {
    }

    public void testRunStopped(final long elapsedTime) {
    }

    public void testRunTerminated() {
    }

    public void testStarted(final String testId, final String testName) {
    }

}
