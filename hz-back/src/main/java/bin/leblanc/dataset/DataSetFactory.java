package bin.leblanc.dataset;

import org.springframework.stereotype.Component;

@Component
public class DataSetFactory {
    public DataSet createDataSet(){
        return new DataSet();
    }
}
