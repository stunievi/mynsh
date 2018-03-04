package bin.leblanc.dataset;

import bin.leblanc.dataset.metadata.IDataModel;
import bin.leblanc.zed.Zed;
import lombok.Getter;
import org.aspectj.asm.IModelFilter;

import java.util.*;

public class DataSet {

    @Getter
    private Map<String,DataModel> models = new LinkedHashMap<>();

    @Getter
    private Zed zed;

    public DataSet(Zed zed){
        this.zed = zed;
    }

    public DataSet addMain(Class clz, IDataModel func){
        DataModel model = new DataModel(clz);
        func.call(model);
        models.put("$$main",model);
        return this;
    }

    public DataSet addExtern(String alias, Class clz, IDataModel func){
        DataModel model = new DataModel(clz);
        func.call(model);
        models.put(alias,model);
        return this;
    }

    public DataSetResult newSearch(){
        return new DataSetResult(this);
    }
}
