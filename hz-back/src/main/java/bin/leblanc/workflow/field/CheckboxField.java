package bin.leblanc.workflow.field;

import lombok.Getter;
import lombok.Setter;

import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

@Getter
@Setter
public class CheckboxField extends InformationField {
    Set<String> items = new LinkedHashSet<>();
    Set<String> selectedItems = new LinkedHashSet<>();

    public void setItems(Set<String> items) {
        this.items = items;
    }

    public void setItems(String[] items) {
        setItems(Arrays.asList(items));
    }

    public void setItems(List<String> items) {
        this.items = new LinkedHashSet<>(items);
    }


    public void setSelectedItems(Set<String> items) {
        this.selectedItems = items;
    }

    public void setSelectedItems(String[] items) {
        setItems(Arrays.asList(items));
    }

    public void setSelectedItems(List<String> items) {
        this.selectedItems = new LinkedHashSet<>(items);
    }


}
