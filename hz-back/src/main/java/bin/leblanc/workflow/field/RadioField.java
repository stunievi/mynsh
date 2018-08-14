package bin.leblanc.workflow.field;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class RadioField extends InformationField {
    Set<String> items = new LinkedHashSet<>();
    String selectedItem;

    public void setItems(Set<String> items) {
        this.items = items;
    }

    public void setItems(String[] items) {
        setItems(Arrays.asList(items));
    }

    public void setItems(List<String> items) {
        this.items = new LinkedHashSet<>(items);
    }
}
