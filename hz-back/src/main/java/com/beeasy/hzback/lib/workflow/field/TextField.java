package com.beeasy.hzback.lib.workflow.field;

import com.beeasy.hzback.lib.workflow.node.InformationField;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TextField extends InformationField {
    String placeHolder;
    String value;
}
