package junit.extensions.eclipse.quick.internal.preference;

import java.util.Iterator;
import java.util.List;
import java.util.StringTokenizer;
import junit.extensions.eclipse.quick.NamingRule;
import junit.extensions.eclipse.quick.internal.Messages;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Listener;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;

public class NamingRulesPreference {
    private final QuickJUnitPreferencePage preferencePage;
    private Shell shell;
    private List namingRulesValue;

    private CheckboxTableViewer tableViewer;
    private Button removeButton;
    private Button editButton;
    private Button moveUpButton;
    private Button moveDownButton;

    public NamingRulesPreference(final QuickJUnitPreferencePage preferencePage) {
        this.preferencePage = preferencePage;
    }

    public void create(final List namingRulesValue, final Composite parent) {
        this.namingRulesValue = namingRulesValue;
        shell = parent.getShell();
        final Composite container = new Composite(parent, SWT.NONE);
        final GridLayout layout = new GridLayout();
        layout.numColumns = 2;
        layout.marginHeight = 0;
        layout.marginWidth = 0;
        container.setLayout(layout);
        final GridData gd = new GridData(GridData.FILL_BOTH);
        container.setLayoutData(gd);
        createTable(container);
        createButtons(container);
        update();
    }

    public void setValue(final List namingRulesValue) {
        this.namingRulesValue = namingRulesValue;
        update();
    }

    public List getValue() {
        return namingRulesValue;
    }

    private void createTable(final Composite container) {
        final Label label = new Label(container, SWT.NONE);
        label.setText(Messages.getString("NamingRulesPreference.label")); //$NON-NLS-1$
        GridData gd = new GridData(GridData.HORIZONTAL_ALIGN_BEGINNING);
        gd.horizontalSpan = 2;
        label.setLayoutData(gd);

        final Table table = new Table(container, SWT.CHECK | SWT.BORDER | SWT.MULTI | SWT.FULL_SELECTION);

        gd = new GridData(GridData.FILL_HORIZONTAL);
        table.setLayoutData(gd);

        final TableLayout tableLayout = new TableLayout();
        final ColumnLayoutData[] columnLayoutData = new ColumnLayoutData[1];
        columnLayoutData[0] = new ColumnWeightData(100);
        tableLayout.addColumnData(columnLayoutData[0]);
        table.setLayout(tableLayout);
        new TableColumn(table, SWT.NONE);
        tableViewer = new CheckboxTableViewer(table);
        tableViewer.setLabelProvider(new TableLabelProvider());
        tableViewer.setContentProvider(new TableContentProvider());
        tableViewer.setInput(this);

        gd = new GridData(GridData.FILL_BOTH | GridData.GRAB_HORIZONTAL | GridData.GRAB_VERTICAL);
        tableViewer.getTable().setLayoutData(gd);
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            @Override
            public void checkStateChanged(final CheckStateChangedEvent event) {
                final NamingRule namingRule = (NamingRule) event.getElement();
                namingRule.setEnabled(event.getChecked());
                update();
            }
        });
        tableViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(final SelectionChangedEvent event) {
                // unable if call update
                updateButtons();
            }
        });
        tableViewer.addDoubleClickListener(new IDoubleClickListener() {
            @Override
            public void doubleClick(final DoubleClickEvent event) {
                editNamingRule();
            }
        });
    }

    private static class TableLabelProvider extends LabelProvider implements ITableLabelProvider {
        @Override
        public String getColumnText(final Object o, final int column) {
            return column == 0 ? ((NamingRule) o).getValue() : ""; //$NON-NLS-1$
        }

        @Override
        public String getText(final Object element) {
            return ((NamingRule) element).getValue();
        }

        @Override
        public Image getColumnImage(final Object element, final int columnIndex) {
            return null;
        }
    }

    private class TableContentProvider implements IStructuredContentProvider {
        @Override
        public Object[] getElements(final Object inputElement) {
            return namingRulesValue.toArray();
        }

        @Override
        public void dispose() {
        }

        @Override
        public void inputChanged(final Viewer viewer, final Object oldInput, final Object newInput) {
        }
    }

    private void createButtons(final Composite container) {
        final Composite buttonContainer = new Composite(container, SWT.NONE);
        final GridData gd = new GridData(GridData.FILL_VERTICAL);
        buttonContainer.setLayoutData(gd);
        final GridLayout buttonLayout = new GridLayout();
        buttonLayout.numColumns = 1;
        buttonLayout.marginHeight = 0;
        buttonLayout.marginWidth = 0;
        buttonContainer.setLayout(buttonLayout);

        Listener listener;

        listener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                addNamingRule();
            }
        };
        createButton("addButton", buttonContainer, listener, true); //$NON-NLS-1$

        listener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                removeNamingRules();
            }
        };
        removeButton = createButton("removeButton", buttonContainer, listener, false); //$NON-NLS-1$
        removeButton.setEnabled(false);

        listener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                editNamingRule();
            }
        };
        editButton = createButton("editButton", buttonContainer, listener, false); //$NON-NLS-1$
        editButton.setEnabled(false);

        listener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                moveNamingRule(true);
            }
        };
        moveUpButton = createButton("moveUpButton", buttonContainer, listener, false); //$NON-NLS-1$
        moveUpButton.setEnabled(false);

        listener = new Listener() {
            @Override
            public void handleEvent(final Event e) {
                moveNamingRule(false);
            }
        };
        moveDownButton = createButton("moveDownButton", buttonContainer, listener, false); //$NON-NLS-1$
        moveDownButton.setEnabled(false);
    }

    private void update() {
        tableViewer.refresh();
        for (int i = 0; i < namingRulesValue.size(); ++i) {
            final NamingRule namingRule = (NamingRule) namingRulesValue.get(i);
            tableViewer.setChecked(namingRule, namingRule.isEnabled());
        }
        updateButtons();
    }

    private void updateButtons() {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        removeButton.setEnabled(!selection.isEmpty());
        editButton.setEnabled(selection.size() == 1);
        final int rowCount = tableViewer.getTable().getItemCount();
        final boolean canMove = selection.size() == 1 && rowCount > 1;
        if (!canMove) {
            moveUpButton.setEnabled(false);
            moveDownButton.setEnabled(false);
        } else {
            final int selectedIndex = namingRulesValue.indexOf(selection.getFirstElement());
            moveUpButton.setEnabled(0 < selectedIndex);
            moveDownButton.setEnabled(selectedIndex < rowCount - 1);
        }
    }

    private void editNamingRule() {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty())
            return;
        final NamingRule rule = (NamingRule) selection.getFirstElement();
        final InputDialog dialog = createEditDialog("editNamingRule", rule.getValue()); //$NON-NLS-1$
        if (dialog.open() == Window.OK) {
            final String value = dialog.getValue();
            if (value.trim().length() != 0) {
                rule.setValue(value);
                update();
            }
        }
    }

    private InputDialog createEditDialog(final String messageId, final String initValue) {
        final String title = Messages.getString("NamingRulesPreference." + messageId + ".dialog.title"); //$NON-NLS-1$ //$NON-NLS-2$
        final String message = Messages.getString("NamingRulesPreference." + messageId + ".dialog.message"); //$NON-NLS-1$ //$NON-NLS-2$
        return new InputDialog(shell, title, message, initValue, new NamingRuleValidator());
    }

    private static class NamingRuleValidator implements IInputValidator {
        @Override
        public String isValid(String newText) {
            newText = newText.trim();
            if (newText.length() == 0)
                return Messages.getString("NamingRulesPreference.namingRuleValidator.empty"); //$NON-NLS-1$

            newText = newText.replaceAll("\\$\\{package\\}", "package"); //$NON-NLS-1$ //$NON-NLS-2$
            newText = newText.replaceAll("\\$\\{type\\}", "type"); //$NON-NLS-1$ //$NON-NLS-2$

            final StringTokenizer st = new StringTokenizer(newText, ".", true); //$NON-NLS-1$
            boolean dot = false;
            while (st.hasMoreTokens()) {
                final String token = st.nextToken();
                if (dot) {
                    if (!token.equals(".")) { //$NON-NLS-1$
                        return Messages.getString("NamingRulesPreference.namingRuleValidator.error"); //$NON-NLS-1$
                    }
                    dot = false;
                } else {
                    if (!isJavaIdentifier(token))
                        return Messages.getString("NamingRulesPreference.namingRuleValidator.tokenError", token); //$NON-NLS-1$
                    dot = true;
                }
            }
            return null;
        }

        private boolean isJavaIdentifier(final String token) {
            if (!Character.isJavaIdentifierStart(token.charAt(0)))
                return false;
            for (int i = 1; i < token.length(); ++i) {
                if (!Character.isJavaIdentifierPart(token.charAt(i)))
                    return false;
            }
            return true;
        }
    }

    private void addNamingRule() {
        final InputDialog dialog = createEditDialog("addNamingRule", ""); //$NON-NLS-1$ //$NON-NLS-2$
        if (dialog.open() == Window.OK) {
            final String value = dialog.getValue();
            if (value.trim().length() != 0) {
                final NamingRule rule = new NamingRule(value, true);
                namingRulesValue.add(rule);
                update();
            }
        }
    }

    private void removeNamingRules() {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty())
            return;
        for (final Iterator i = selection.iterator(); i.hasNext();) {
            namingRulesValue.remove(i.next());
        }
        update();
    }

    private void moveNamingRule(final boolean up) {
        final IStructuredSelection selection = (IStructuredSelection) tableViewer.getSelection();
        if (selection.isEmpty() || selection.size() > 1)
            return;
        final Object selected = selection.getFirstElement();
        final int oldIndex = namingRulesValue.indexOf(selected);
        final int newIndex = up ? oldIndex - 1 : oldIndex + 1;
        if (newIndex < 0 || namingRulesValue.size() <= newIndex)
            return;
        namingRulesValue.remove(oldIndex);
        namingRulesValue.add(newIndex, selected);
        update();
    }

    private Button createButton(final String buttonId, final Composite buttonContainer, final Listener listener,
        final boolean isTop) {
        final Button button = new Button(buttonContainer, SWT.PUSH);
        button.setText(Messages.getString("NamingRulesPreference." + buttonId + ".label")); //$NON-NLS-1$ //$NON-NLS-2$
        button.setToolTipText(Messages.getString("NamingRulesPreference." + buttonId + ".tooltip")); //$NON-NLS-1$ //$NON-NLS-2$
        GridData gd;
        if (isTop)
            gd = new GridData(GridData.FILL_HORIZONTAL | GridData.VERTICAL_ALIGN_BEGINNING);
        else
            gd = preferencePage.getButtonGridData(button);
        button.setLayoutData(gd);
        button.addListener(SWT.Selection, listener);
        return button;
    }
}
