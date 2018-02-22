package bin.leblanc.workflow.field;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextareaField extends InformationField {
    String placeholder;
    String value;
}
