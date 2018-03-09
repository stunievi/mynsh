package bin.leblanc.dataset;

import bin.leblanc.zed.Zed;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.persistence.EntityManager;

@Component
public class DataSetFactory {

    @Autowired
    Zed zed;

    @Autowired
    EntityManager entityManager;

    public DataSet createDataSet(){
        return new DataSet(zed);
    }


    public NativeDataSet createNativeDataSet(){
        return new NativeDataSet(entityManager);
    }
}
