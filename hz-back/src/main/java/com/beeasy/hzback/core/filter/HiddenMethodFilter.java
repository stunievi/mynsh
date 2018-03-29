package com.beeasy.hzback.core.filter;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.HiddenHttpMethodFilter;

@Component
@Order(1)
public class HiddenMethodFilter extends HiddenHttpMethodFilter {
}
