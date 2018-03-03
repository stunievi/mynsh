package bin.leblanc.dataset;

import bin.leblanc.dataset.metadata.IDataModel;
import org.aspectj.asm.IModelFilter;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

    private List<DataModel> models = new ArrayList<>();

    public DataSet addMain(Class clz, IDataModel func){
        DataModel model = new DataModel(clz);
        func.call(model);
        models.add(model);
        return this;
    }

    public DataSet addExtern(Class clz, IDataModel func){
        DataModel model = new DataModel(clz);
        func.call(model);
        models.add(model);
        return this;
    }

    public DataSetResult newSearch(){
        
    }
}
