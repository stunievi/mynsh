package com.beeasy.hzback.modules.system.entity;

import com.beeasy.hzback.core.entity.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Getter
@Setter
@Entity
@Table(name = "t_gps_position")
public class GPSPosition extends AbstractBaseEntity {
    @Id
    @GeneratedValue
    Long id;

    //精度
    Double lat;
    //维度
    Double lng;

    String position;

    //用户ID
    Long userId;

}
