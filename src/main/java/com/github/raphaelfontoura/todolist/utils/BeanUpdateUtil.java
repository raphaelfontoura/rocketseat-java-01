package com.github.raphaelfontoura.todolist.utils;

import java.util.stream.Stream;

import org.springframework.beans.BeanUtils;
import org.springframework.beans.BeanWrapper;
import org.springframework.beans.BeanWrapperImpl;

public class BeanUpdateUtil {
  
  public static <T> void updateBean(T source, T target) {
    BeanUtils.copyProperties(source, target, getNullPropertyNames(source));
  }

  private static String[] getNullPropertyNames(Object source) {
    final BeanWrapper input = new BeanWrapperImpl(source);
    return Stream.of(input.getPropertyDescriptors())
      .map(p -> p.getName())
      .filter(name -> input.getPropertyValue(name) == null)
      .toArray(String[]::new);
  }

}
