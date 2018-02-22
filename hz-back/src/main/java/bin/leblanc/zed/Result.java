package bin.leblanc.zed;

import lombok.Data;
import org.springframework.validation.BindingResult;

@Data
public class Result {
    private boolean success;
    private Object message;


    private Result(boolean success,Object item) {
        this.success = success;
        this.message = item;
    }

    private Result(boolean success) {
        this.success = success;
    }

    public static Result ok(){
        return new Result(true);
    }
    public static Result ok(Object item){
        return new Result(true,item);
    }

    public static Result error(Object item){
        if(item instanceof BindingResult){
            item = ((BindingResult) item).getAllErrors();
        }
        return new Result(false,item);
    }
    public static Result error(){
        return error(null);
    }
}
