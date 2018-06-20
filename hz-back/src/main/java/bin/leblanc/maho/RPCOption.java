package bin.leblanc.maho;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;

import java.util.List;


@Getter
public class RPCOption {

    public interface Args{
        List<Object> call();
    }

    public interface Return{
        Object call(Object obj);
    }

    Args prefixArgs;
    Args afterArgs;
    Return aReturn;
    boolean dev = false;

    /**
     * 前置参数
     * @param prefixArgs
     * @return
     */
    public RPCOption setPrefixArgs(@NonNull Args prefixArgs){
        this.prefixArgs = prefixArgs;
        return this;
    }

    /**
     * 后置参数
     * @param afterArgs
     * @return
     */
    public RPCOption setAfterArgs(@NonNull Args afterArgs){
        this.afterArgs = afterArgs;
        return this;
    }


    /**
     * 返回值包装函数
     */
    public RPCOption setReturn(@NonNull Return aReturn){
        this.aReturn = aReturn;
        return this;
    }

    public RPCOption setDev(boolean dev){
        this.dev = dev;
        return this;
    }

}
