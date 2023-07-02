package com.xx.buji.dto;

import com.xx.buji.entity.Setmeal;
import com.xx.buji.entity.SetmealDish;
import lombok.Data;
import java.util.List;

@Data
public class SetmealDto extends Setmeal {

    private List<SetmealDish> setmealDishes;

    private String categoryName;
}
