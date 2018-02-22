package com.beeasy.hzback.lib.zed.metadata;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;

public interface IGetWhereLimit {
    Predicate call(CriteriaBuilder cb, Path root, Predicate condition);
}
