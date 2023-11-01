package cn.bctools.web.excel;

import cn.hutool.json.JSONUtil;
import com.alibaba.excel.converters.Converter;
import com.alibaba.excel.enums.CellDataTypeEnum;
import com.alibaba.excel.metadata.CellData;
import com.alibaba.excel.metadata.GlobalConfiguration;
import com.alibaba.excel.metadata.property.ExcelContentProperty;
import org.springframework.stereotype.Component;

import java.util.ArrayList;

/**
 * Excel类型转换: LocalDateTime
 *
 * @Author: GuoZi
 */
@Component
public class ArrayListConvert implements Converter<ArrayList> {

    @Override
    public Class supportJavaTypeKey() {
        return ArrayList.class;
    }

    @Override
    public CellDataTypeEnum supportExcelTypeKey() {
        return CellDataTypeEnum.STRING;
    }

    @Override
    public ArrayList convertToJavaData(CellData cellData, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        ArrayList<Object> list = new ArrayList<>();
        list.addAll(JSONUtil.parseArray(cellData.getStringValue()));
        return list;
    }

    @Override
    public CellData convertToExcelData(ArrayList value, ExcelContentProperty contentProperty, GlobalConfiguration globalConfiguration) throws Exception {
        return new CellData<>(JSONUtil.toJsonStr(value));
    }

}
