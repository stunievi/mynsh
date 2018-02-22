package com.beeasy.hzback.lib.workflow.field;

import com.beeasy.hzback.lib.workflow.node.InformationField;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
public class DateField extends InformationField {
    Date value;
    String placeholder;
}
