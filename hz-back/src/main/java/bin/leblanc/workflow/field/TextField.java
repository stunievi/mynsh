package bin.leblanc.workflow.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextField extends InformationField {
    String placeHolder;
    String value;
}
