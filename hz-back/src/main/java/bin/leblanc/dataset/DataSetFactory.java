package bin.leblanc.dataset;

import bin.leblanc.zed.Zed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class DataSetFactory {

    @Autowired
    Zed zed;

    public DataSet createDataSet(){
        return new DataSet(zed);
    }
}
