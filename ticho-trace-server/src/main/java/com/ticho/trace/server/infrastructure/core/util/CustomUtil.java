package com.ticho.trace.server.infrastructure.core.util;

import cn.hutool.core.util.ClassUtil;
import cn.hutool.core.util.StrUtil;
import com.ticho.trace.server.interfaces.query.LogQuery;
import io.swagger.annotations.ApiModelProperty;

import java.lang.reflect.Field;
import java.util.StringJoiner;

/**
 * 自定义工具
 *
 * @author zhajianjun
 * @date 2023-06-10 12:46
 */
public class CustomUtil {

    public static void main(String[] args) {
        printModel(LogQuery.class);
        // printTableColumns(LogVO.class);
    }

    public static void printModalFormColumns(Class<?> classz) {
        Field[] declaredFields = classz.getDeclaredFields();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("export function getModalFormColumns(): FormSchema[] {\n" +
                "  return [");
        for (Field declaredField : declaredFields) {
            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
            String name = declaredField.getName();
            joiner.add(StrUtil.format("    {\n" +
                    "      field: `{}`,\n" +
                    "      label: `{}`,\n" +
                    "      component: 'Input',\n" +
                    "      componentProps: {\n" +
                    "        placeholder: '请输入{}',\n" +
                    "      },\n" +
                    "      colProps: {\n" +
                    "        span: 24,\n" +
                    "      },\n" +
                    "    },", name, annotation.value(), annotation.value()));
        }
        joiner.add("  ];\n" +
                "}");
        System.out.println(joiner);
    }

    public static void printTableColumns(Class<?> classz) {
        Field[] declaredFields = classz.getDeclaredFields();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("export function getTableColumns(): BasicColumn[] {\n" +
                "  return [");
        for (Field declaredField : declaredFields) {
            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
            String name = declaredField.getName();
            joiner.add(StrUtil.format("    {\n" +
                    "      title: '{}',\n" +
                    "      dataIndex: '{}',\n" +
                    "      resizable: true,\n" +
                    "      width: 150,\n" +
                    "    },", annotation.value(), name));
        }
        joiner.add("  ];\n" +
                "}");
        System.out.println(joiner);
    }

    public static void printSearchColumns(Class<?> classz) {
        Field[] declaredFields = classz.getDeclaredFields();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add("export function getSearchColumns(): Partial<FormProps> {\n" +
                "  return {\n" +
                "    labelWidth: 100,\n" +
                "    schemas: [");
        for (Field declaredField : declaredFields) {
            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
            String name = declaredField.getName();
            joiner.add(StrUtil.format("      {\n" +
                    "        field: `{}`,\n" +
                    "        label: `{}`,\n" +
                    "        component: 'Input',\n" +
                    "        colProps: {\n" +
                    "          xl: 12,\n" +
                    "          xxl: 8,\n" +
                    "        },\n" +
                    "        componentProps: {\n" +
                    "          placeholder: '请输入{}',\n" +
                    "        },\n" +
                    "      },", name, annotation.value(), annotation.value()));
        }
        joiner.add("    ],\n" +
                "  };\n" +
                "}");
        System.out.println(joiner);
    }

    public static void printModel(Class<?> classz) {
        Field[] declaredFields = classz.getDeclaredFields();
        StringJoiner joiner = new StringJoiner("\n");
        joiner.add(StrUtil.format("export interface {} {", classz.getSimpleName()));
        for (Field declaredField : declaredFields) {
            ApiModelProperty annotation = declaredField.getAnnotation(ApiModelProperty.class);
            String name = declaredField.getName();
            joiner.add(StrUtil.format("    /** {} */", annotation.value()));
            joiner.add(StrUtil.format("    {}: {};", name, getType(declaredField.getType())));
        }
        joiner.add("}");
        System.out.println(joiner);
    }

    public static String getType(Class<?> classz) {
        boolean primitiveWrapper = ClassUtil.isPrimitiveWrapper(classz);
        return primitiveWrapper ? "number" : "string";
    }

}
