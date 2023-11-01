package cn.bctools.auth.exception;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;

import java.io.IOException;

/**
 * @author 
 */
public class CustomExceptionSerializer extends StdSerializer<CustomException> {

    protected CustomExceptionSerializer() {
        super(CustomException.class);
    }

    @Override
    public void serialize(CustomException e, JsonGenerator gen, SerializerProvider serializerProvider) throws IOException {
        gen.writeStartObject();
        gen.writeObjectField("code", -1);
        gen.writeStringField("msg", e.getMessage());
        gen.writeStringField("msg", e.getCode());
        gen.writeEndObject();
    }
}
