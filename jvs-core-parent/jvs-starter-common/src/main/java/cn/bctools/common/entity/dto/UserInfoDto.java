package cn.bctools.common.entity.dto;

import lombok.Data;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * @author aa876
 */
@Data
@Accessors(chain = true)
public class UserInfoDto<T extends UserDto> implements Serializable {

    private static final long serialVersionUID = 1L;
    private T userDto;
    private List<String> permissions = new ArrayList<>();
    private List<String> roles = new ArrayList<>();
    private List<DataScopeDto> dataScope = new ArrayList<>();

}
