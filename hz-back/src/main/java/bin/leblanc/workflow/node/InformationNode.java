package bin.leblanc.workflow.node;

import bin.leblanc.workflow.WorkflowNode;
import bin.leblanc.workflow.field.*;
import bin.leblanc.workflow.metadata.*;

public class InformationNode extends WorkflowNode {


    public InformationNode addTextField(ITextField field){
        TextField textField = new TextField();
        field.call(textField);
        return this;
    }

    public InformationNode addTextareaField(ITextareaField field){
        TextareaField textareaField = new TextareaField();
        field.call(textareaField);
        return this;
    }

    public InformationNode addDateField(IDateField field){
        DateField dateField = new DateField();
        field.call(dateField);
        return this;
    }

    public InformationNode addRadioField(IRadioField field){
        RadioField radioField = new RadioField();
        field.call(radioField);
        return this;
    }

    public InformationNode addCheckboxField(ICheckboxField field){
        CheckboxField checkboxField = new CheckboxField();
        field.call(checkboxField);
        return this;
    }







}
