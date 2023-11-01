package cn.bctools.message.push.dto.messagePush;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.util.List;

@NoArgsConstructor
@AllArgsConstructor
@Data
@Accessors(chain = true)
@ApiModel("消息基础配置")
public class BaseMessage implements Serializable {

    private static final long serialVersionUID = 3583298470031427965L;
    /**
     * 指定clientId发送
     */
    @ApiModelProperty("客户端唯一标识")
    private String clientCode;

    @ApiModelProperty("自定义接收人 邮件-邮件地址 短信-电话号码等")
    private List<ReceiversDto> definedReceivers;

    public boolean hasReceiver(){
        return this.definedReceivers != null && !this.definedReceivers.isEmpty();
    }

}
