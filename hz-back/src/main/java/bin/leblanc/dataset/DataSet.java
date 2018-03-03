package bin.leblanc.dataset;

import bin.leblanc.dataset.metadata.IDataModel;
import bin.leblanc.zed.Zed;
import lombok.Getter;
import org.aspectj.asm.IModelFilter;

import java.util.ArrayList;
import java.util.List;

public class DataSet {

    @Getter
    private List<DataModel> models = new ArrayList<>();

    @Getter
    private Zed zed;

    public DataSet(Zed zed){
        this.zed = zed;
    }

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
        return new DataSetResult(this);
    }
}
