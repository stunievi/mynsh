package bin.leblanc.zed.event;

import bin.leblanc.zed.Zed;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

public class ZedInitializedEvent extends ApplicationEvent {

    public ZedInitializedEvent(Object source) {
        super(source);
    }
}
