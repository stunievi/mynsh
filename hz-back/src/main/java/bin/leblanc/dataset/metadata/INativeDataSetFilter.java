package bin.leblanc.dataset.metadata;

import java.util.Map;

public interface INativeDataSetFilter {
    Map<String,?> filter(Map<String,?> item);
}
