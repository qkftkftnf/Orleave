package com.orleave.dto.request;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@ApiModel("PasswordRequest")
public class PasswordRequestDto {
	@ApiModelProperty(name="password", example="your_password")
	String password;
}
