package org.richfaces.renderkit;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.context.ResponseWriter;
import javax.faces.model.SelectItem;

import org.ajax4jsf.javascript.ScriptString;
import org.ajax4jsf.javascript.ScriptUtils;
import org.richfaces.component.AbstractInplaceSelect;
import org.richfaces.component.util.InputUtils;
import org.richfaces.component.util.SelectUtils;

/**
 * @author Anton Belevich
 * 
 */

@ResourceDependencies({
        @ResourceDependency(library = "javax.faces", name = "jsf.js"),
        @ResourceDependency(name = "jquery.js"),
        @ResourceDependency(name = "jquery.position.js"),
        @ResourceDependency(name = "richfaces.js"),
        @ResourceDependency(name = "richfaces-event.js"),
        @ResourceDependency(name = "richfaces-base-component.js"),
        @ResourceDependency(name = "richfaces-selection.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceBase.js"),
        @ResourceDependency(library = "org.richfaces", name = "select.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceInput.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceSelect.js"),
        @ResourceDependency(library = "org.richfaces", name = "inplaceSelect.ecss") })
public class InplaceSelectBaseRenderer extends InplaceInputBaseRenderer {

    public static final String OPTIONS_ITEM_CLASS = "itemCss";

    public static final String OPTIONS_SELECT_ITEM_CLASS = "selectItemCss";

    public static final String OPTIONS_LIST_CORD = "listCord";

    public static final String OPTIONS_ITEMS_CORD = "itemsCord";

    public static final String OPTIONS_SELECT_ITEMS = "selectItems";

    public static final String OPTIONS_SELECT_ITEM_VALUE_INPUT = "selValueInput";

    protected static final class ClientSelectItem implements ScriptString {

        private String clientId;
        private String label;
        private String convertedValue;

        public ClientSelectItem(String convertedValue, String label) {
            this(convertedValue, label, null);
        }

        public ClientSelectItem(String convertedValue, String label,
                String clientId) {
            super();
            this.convertedValue = convertedValue;
            this.label = label;
            this.clientId = clientId;
        }

        public String getClientId() {
            return clientId;
        }

        public void setClientId(String clientId) {
            this.clientId = clientId;
        }

        public String getLabel() {
            return label;
        }

        public String getConvertedValue() {
            return convertedValue;
        }

        public void appendScript(StringBuffer functionString) {
            functionString.append(this.toScript());
        }

        public String toScript() {
            return "{ 'id' : " + ScriptUtils.toScript(clientId)
                    + " , 'label' : " + ScriptUtils.toScript(label)
                    + ", 'value' : " + ScriptUtils.toScript(convertedValue)
                    + "}";
        }
    }

    @Override
    protected String getScriptName() {
        return "new RichFaces.ui.InplaceSelect";
    }

    public List<ClientSelectItem> getConvertedSelectItems(
            FacesContext facesContext, UIComponent component) {
        AbstractInplaceSelect inplaceSelect = (AbstractInplaceSelect) component;
        List<SelectItem> selectItems = SelectUtils.getSelectItems(facesContext,
                inplaceSelect);
        List<ClientSelectItem> clientSelectItems = new ArrayList<InplaceSelectBaseRenderer.ClientSelectItem>();
        for (SelectItem selectItem : selectItems) {
            String convertedStringValue = InputUtils.getConvertedStringValue(
                    facesContext, inplaceSelect, selectItem.getValue());
            String label = selectItem.getLabel();
            clientSelectItems.add(new ClientSelectItem(convertedStringValue,
                    label));
        }
        return clientSelectItems;
    }

    @Override
    public void addToOptions(FacesContext facesContext, UIComponent component,
            Map<String, Object> options, Object additional) {
        options.put(OPTIONS_ITEM_CLASS, "insel_option");
        options.put(OPTIONS_SELECT_ITEM_CLASS, "insel_select");
        String clientId = component.getClientId(facesContext);
        options.put(OPTIONS_LIST_CORD, clientId + "List");
        options.put(OPTIONS_ITEMS_CORD, clientId + "Items");
        options.put(OPTIONS_SELECT_ITEMS, additional);
        options.put(OPTIONS_SELECT_ITEM_VALUE_INPUT, clientId + "selValue");
    }

    public void encodeOptions(FacesContext facesContext, UIComponent component,
            List<ClientSelectItem> clientSelectItems) throws IOException {
        AbstractInplaceSelect inplaceSelect = (AbstractInplaceSelect) component;
        if (clientSelectItems != null && !clientSelectItems.isEmpty()) {
            ResponseWriter writer = facesContext.getResponseWriter();
            String clientId = component.getClientId(facesContext);
            int i = 0;
            for (ClientSelectItem clientSelectItem : clientSelectItems) {
                String itemClientId = clientId + "Item" + (i++);
                clientSelectItem.setClientId(itemClientId);

                writer.startElement(HtmlConstants.SPAN_ELEM, inplaceSelect);
                writer.writeAttribute(HtmlConstants.ID_ATTRIBUTE, itemClientId,
                        null);
                writer.writeAttribute(HtmlConstants.CLASS_ATTRIBUTE,
                        getOptionCss(), null);

                String label = clientSelectItem.getLabel();
                if (label != null && label.trim().length() > 0) {
                    writer.writeText(label, null);
                } else {
                    writer.write("\u00a0");
                }
                writer.endElement(HtmlConstants.SPAN_ELEM);
            }
        }
    }

    public String getSelectLabel(FacesContext facesContext,
            UIComponent component) {
        AbstractInplaceSelect select = (AbstractInplaceSelect) component;
        String label = getSelectInputLabel(facesContext, select);
        if (label == null) {
            label = select.getDefaultLabel();
        }
        return label;
    }

    public String getSelectInputLabel(FacesContext facesContext,
            UIComponent component) {
        AbstractInplaceSelect select = (AbstractInplaceSelect) component;
        Object value = select.getSubmittedValue();
        if (value == null) {
            value = select.getValue();
            if (value != null) {
                List<SelectItem> items = SelectUtils.getSelectItems(
                        facesContext, component);
                for (SelectItem item : items) {
                    if (value.equals(item.getValue())) {
                        value = item.getLabel();
                    }
                }
            }
        }

        return (String) value;
    }

    public String getListStyles(FacesContext facesContext, UIComponent component) {
        return "";
    }

    public String getReadyStateCss() {
        return "insel_default_state";
    }

    public String getEditStateCss() {
        return "insel_edit_state";
    }

    public String getChangedStateCss() {
        return "insel_changed_state";
    }

    public String getNoneCss() {
        return "rf-is-none";
    }

    public String getOptionCss() {
        return "insel_option insel_font";
    }

}