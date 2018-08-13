package com.beeasy.common.entity;

import com.beeasy.common.helper.AbstractBaseEntity;
import lombok.Getter;
import lombok.Setter;
import javax.persistence.*;
import java.util.*;

@Getter
@Setter
@Entity
@Table(name = "t_gps_position")
public class GPSPosition extends AbstractBaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    //精度
    Double lat;
    //维度
    Double lng;

    String position;

    //用户ID
    Long userId;

}
