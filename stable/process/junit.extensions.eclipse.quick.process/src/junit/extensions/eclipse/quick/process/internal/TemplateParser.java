package junit.extensions.eclipse.quick.process.internal;

import static junit.extensions.eclipse.quick.process.internal.TemplateKey.ERROR_COUNT;
import static junit.extensions.eclipse.quick.process.internal.TemplateKey.FAIL_COUNT;
import static junit.extensions.eclipse.quick.process.internal.TemplateKey.NAME_COUNT;
import static junit.extensions.eclipse.quick.process.internal.TemplateKey.OK_COUNT;
import static junit.extensions.eclipse.quick.process.internal.TemplateKey.RESULT_COUNT;
import static junit.extensions.eclipse.quick.process.internal.TemplateKey.TOTAL_COUNT;

import org.eclipse.jdt.junit.model.ITestRunSession;

class TemplateParser {

    private String template;
    private TestCounter counter = new TestCounter();

    public TemplateParser() {
    }

    String pickupTestClassAndMethod(final String testName) {
        if (testName == null)
            return null;
        if (testName.indexOf('.') != 0) {
            final String[] split = testName.split("\\."); //$NON-NLS-1$
            if (split.length > 2) {
                return split[split.length - 2] + "." + split[split.length - 1]; //$NON-NLS-1$
            }
        }
        return testName;
    }

    public String parseTemplate(final ITestRunSession session) {
        String result = null;
        result = replaceResult(session, template);
        result = replaceCount(session, result);
        result = replaceName(session, result);
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

    private String replaceName(final ITestRunSession session, String target) {
        final String testName = session.getTestRunName();
        target = target.replaceAll(key(NAME_COUNT), pickupTestClassAndMethod(testName));
        return target;
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
