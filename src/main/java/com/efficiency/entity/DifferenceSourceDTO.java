package com.efficiency.entity;

import lombok.Data;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * @author vincent.jiao
 */
@Data
public class DifferenceSourceDTO {
    ConnInfo connInfo1;
    ConnInfo connInfo2;
}
