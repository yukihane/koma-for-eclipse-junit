package junit.extensions.eclipse.quick.notifications.internal;

import static junit.extensions.eclipse.quick.notifications.internal.TemplateKey.ERROR_COUNT;
import static junit.extensions.eclipse.quick.notifications.internal.TemplateKey.FAIL_COUNT;
import static junit.extensions.eclipse.quick.notifications.internal.TemplateKey.OK_COUNT;
import static junit.extensions.eclipse.quick.notifications.internal.TemplateKey.RESULT_COUNT;
import static junit.extensions.eclipse.quick.notifications.internal.TemplateKey.TOTAL_COUNT;

import org.eclipse.jdt.junit.model.ITestRunSession;

class TemplateParser {

    private String template;
    private TestCounter counter = new TestCounter();

    public TemplateParser() {
    }

    public String parseTemplate(final ITestRunSession session) {
        String result = null;
        result = replaceResult(session, template);
        result = replaceCount(session, result);
        return result;
    }

    private String replaceCount(final ITestRunSession session, String result) {
        counter.count(session);
        result = result.replaceAll(key(TOTAL_COUNT), String.valueOf(counter.getTotalTests()));
        result = result.replaceAll(key(OK_COUNT), String.valueOf(counter.getOKTests()));
        result = result.replaceAll(key(FAIL_COUNT), String.valueOf(counter.getFailureTests()));
        //		result = result.replaceAll(key(IGNORE_COUNT), String.valueOf(counter.getIgnoreTests()));
        result = result.replaceAll(key(ERROR_COUNT), String.valueOf(counter.getErrorTests()));
        return result;
    }

    private String replaceResult(final ITestRunSession session, final String target) {
        final String result = target.replaceAll(key(RESULT_COUNT), session.getTestResult(true).toString());
        return result;
    }

    private String key(final TemplateKey key) {
        return key.regexKey();
    }

    public void setTemplate(final String template) {
        this.template = template;
    }

    void setCounter(final TestCounter counter) {
        this.counter = counter;
    }

}
