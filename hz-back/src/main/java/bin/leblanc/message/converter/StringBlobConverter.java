package bin.leblanc.message.converter;

import javax.persistence.AttributeConverter;

public class StringBlobConverter implements AttributeConverter<String,byte[]> {
    @Override
    public byte[] convertToDatabaseColumn(String s) {
        return s.getBytes();
    }

    @Override
    public String convertToEntityAttribute(byte[] bytes) {
        return new String(bytes);
    }
}
