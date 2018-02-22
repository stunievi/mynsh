package bin.leblanc.workflow.field;

import bin.leblanc.workflow.node.InformationField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextField extends InformationField {
    String placeHolder;
    String value;
}
