package bin.leblanc.message.converter;

//import com.beeasy.hzback.modules.setting.work_engine.BaseWorkNode;

import lombok.Cleanup;

import javax.persistence.AttributeConverter;
import java.io.*;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

public class BlobConverter<T> implements AttributeConverter<T,byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(T t) {
        if(t instanceof String){
            return ((String) t).getBytes();
        }
        //序列化
        else{
            byte[] bytes = null;
            try {
                @Cleanup ByteArrayOutputStream bos = new ByteArrayOutputStream();
                @Cleanup ObjectOutputStream oos = new ObjectOutputStream(bos);
                oos.writeObject(t);
                bytes = bos.toByteArray();

            } catch (IOException e) {
                e.printStackTrace();
            }
            return bytes;
        }
    }

    @Override
    public T convertToEntityAttribute(byte[] bytes) {
        Class<T> clz = (Class<T>) getSuperClassGenricType(getClass(),0);
        if(clz.equals(String.class)){
            return (T) new String(bytes);
        }
        else{
            T result = null;
            try {
                @Cleanup ByteArrayInputStream bis = new ByteArrayInputStream(bytes);
                @Cleanup ObjectInputStream ois = new ObjectInputStream (bis);
                result = (T) ois.readObject();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            }
            return result;
        }
    }

    public static Class<Object> getSuperClassGenricType(final Class clazz, final int index) {

        //返回表示此 Class 所表示的实体（类、接口、基本类型或 void）的直接超类的 Type。
        Type genType = clazz.getGenericSuperclass();

        if (!(genType instanceof ParameterizedType)) {
            return Object.class;
        }
        //返回表示此类型实际类型参数的 Type 对象的数组。
        Type[] params = ((ParameterizedType) genType).getActualTypeArguments();

        if (index >= params.length || index < 0) {
            return Object.class;
        }
        if (!(params[index] instanceof Class)) {
            return Object.class;
        }

        return (Class) params[index];
    }
}
