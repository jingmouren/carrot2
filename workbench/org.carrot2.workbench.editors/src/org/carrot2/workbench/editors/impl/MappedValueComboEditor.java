package org.carrot2.workbench.editors.impl;

import java.util.List;

import org.apache.commons.lang.ObjectUtils;
import org.apache.commons.lang.StringUtils;
import org.carrot2.workbench.core.helpers.GUIFactory;
import org.carrot2.workbench.editors.*;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.*;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;

import com.google.common.collect.BiMap;

/**
 * Template code for editors with mapped values (String to Object).
 */
public abstract class MappedValueComboEditor extends AttributeEditorAdapter
{
    /**
     * Special value for no-selection.
     */
    private final static String NULL_VALUE = "";

    /**
     * Mapping between values and their user-interface representations.
     */
    private BiMap<? extends Object, String> valueToName;

    /**
     * Order of values on the suggestion list.
     */
    private List<Object> valueOrder;

    /**
     * A box container.
     */
    private Composite boxContainer;

    /**
     * A {@link Combo} component for displaying mapped constants and hints.
     */
    private Combo box;

    /**
     * Most recently selected value;
     */
    private Object currentValue;

    /**
     * If <code>true</code> valid value selection is required (the attribute cannot be
     * <code>null</code>).
     */
    public boolean valueRequired = true;

    /**
     * If <code>true</code>, the attribute can take any {@link String} value, not only
     * those listed in {@link #valueOrder} and other collection fields.
     */
    public boolean anyValueAllowed = false;

    @Override
    protected AttributeEditorInfo init()
    {
        return new AttributeEditorInfo(1, false);
    }

    /*
     * 
     */
    protected final void setValues(BiMap<? extends Object, String> valueToName,
        List<Object> valueOrder)
    {
        this.valueToName = valueToName;
        this.valueOrder = valueOrder;
    }

    /*
     * 
     */
    public void createEditor(Composite parent, int gridColumns)
    {
        boxContainer = new Composite(parent, SWT.NONE);
        boxContainer.setLayoutData(GUIFactory.editorGridData().grab(true, false).hint(200,
            SWT.DEFAULT).align(SWT.FILL, SWT.CENTER).span(gridColumns, 1).create());
        boxContainer.setLayout(new FillLayout());

        recreate();
    }
    
    /*
     * 
     */
    private void recreate()
    {
        if (box != null)
        {
            box.dispose();
        }

        final int style = SWT.DROP_DOWN | SWT.BORDER
            | (anyValueAllowed ? 0 : SWT.READ_ONLY);

        box = new Combo(boxContainer, style);

        /*
         * React to focus lost.
         */
        final Class<?> clazz = getClass();

        box.addFocusListener(new FocusAdapter()
        {
            public void focusLost(FocusEvent e)
            {
                checkContentChange();
            }
        });

        box.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                fireContentChanging(new AttributeEvent(clazz, getAttributeKey(),
                    getBoxValue()));
            }
        });

        box.addTraverseListener(new TraverseListener()
        {
            public void keyTraversed(TraverseEvent e)
            {
                if (e.detail == SWT.TRAVERSE_RETURN)
                {
                    checkContentChange();
                }
            }
        });

        if (!valueRequired)
        {
            /*
             * Add an artificial option to the suggestion list to clear selection.
             */
            box.add(NULL_VALUE);
        }

        /*
         * Add hints.
         */
        for (Object value : valueOrder)
        {
            box.add(valueToName.get(value));
        }

        currentValue = null;
    }

    /**
     * Map a given attribute value to user-friendly name.
     */
    private Object userFriendlyToValue(String text)
    {
        if (text == NULL_VALUE || StringUtils.isEmpty(text))
        {
            return null;
        }

        Object value = this.valueToName.inverse().get(text);
        if (value == null && this.anyValueAllowed)
        {
            value = text;
        }

        return value;
    }

    /*
     * 
     */
    private Object getBoxValue()
    {
        int selection = box.getSelectionIndex();
        if (selection > 0)
        {
            if (selection == 0 && !this.valueRequired)
            {
                return null;
            }
            else
            {
                if (!valueRequired)
                {
                    selection -= 1;
                }
                return valueOrder.get(selection);
            }
        }
        else
        {
            final String text = box.getText();
            return userFriendlyToValue(text);
        }
    }
    
    /**
     * Map a given user-friendly name.
     */
    private String valueToUserFriendly(Object object)
    {
        String value = this.valueToName.get(object);
        if (value == null)
        {
            value = NULL_VALUE;
        }
        return value;
    }

    /*
     * 
     */
    @Override
    public void setFocus()
    {
        this.box.setFocus();
    }

    /*
     * 
     */
    @Override
    public Object getValue()
    {
        return currentValue;
    }

    /*
     * 
     */
    @Override
    public void setValue(Object newValue)
    {
        if (ObjectUtils.equals(newValue, getValue()))
        {
            return;
        }

        box.setText(valueToUserFriendly(newValue));
        checkContentChange();
    }

    /**
     * Check if the content has changed compared to the current value of this attribute.
     * If so, fire an event.
     */
    private void checkContentChange()
    {
        final Object asValue = getBoxValue();

        if (!ObjectUtils.equals(currentValue, asValue))
        {
            this.currentValue = asValue;
            fireAttributeChanged(new AttributeEvent(this));
        }
    }
}
