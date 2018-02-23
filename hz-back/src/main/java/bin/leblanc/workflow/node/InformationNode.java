package bin.leblanc.workflow.node;

import bin.leblanc.workflow.WorkflowNode;
import bin.leblanc.workflow.field.*;
import bin.leblanc.workflow.metadata.*;
import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public class InformationNode extends WorkflowNode {

    @Getter
    private Set<InformationField> fields = new LinkedHashSet<>();

    public InformationNode addTextField(ITextField field){
        TextField textField = new TextField();
        field.call(textField);
        fields.add(textField);
        return this;
    }

    public InformationNode addTextareaField(ITextareaField field){
        TextareaField textareaField = new TextareaField();
        field.call(textareaField);
        fields.add(textareaField);
        return this;
    }

    public InformationNode addDateField(IDateField field){
        DateField dateField = new DateField();
        field.call(dateField);
        fields.add(dateField);
        return this;
    }

    public InformationNode addRadioField(IRadioField field){
        RadioField radioField = new RadioField();
        field.call(radioField);
        fields.add((radioField));
        return this;
    }

    public InformationNode addCheckboxField(ICheckboxField field){
        CheckboxField checkboxField = new CheckboxField();
        field.call(checkboxField);
        fields.add((checkboxField));
        return this;
    }









}
