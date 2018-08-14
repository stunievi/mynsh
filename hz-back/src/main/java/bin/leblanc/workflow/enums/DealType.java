package bin.leblanc.workflow.enums;


import lombok.Getter;
import lombok.Setter;

@Getter
public enum DealType {
    BACK_TO_LAST(1),
    STOP(2),
    SUCCESS(3);

    @Getter
    @Setter
    private int type;

    DealType(int i) {
        this.type = i;
    }

}