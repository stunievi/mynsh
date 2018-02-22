package bin.leblanc.workflow.field;

import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DateField extends InformationField {
    Date value;
    String placeholder;
}
