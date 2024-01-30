package indi.haorui.template.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Target;

/**
 * Created by Yang Hao.rui on 2024/1/15
 * @author cnHaoYan
 */
@Target({ElementType.TYPE, ElementType.METHOD})
public @interface Yang {

    String[] value() default {};

}
