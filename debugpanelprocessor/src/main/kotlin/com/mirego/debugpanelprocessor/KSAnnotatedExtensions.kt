package com.mirego.debugpanelprocessor

import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSAnnotation
import kotlin.reflect.KClass

fun KSAnnotated.findAnnotation(clazz: KClass<*>): KSAnnotation? =
    annotations.find { it.annotationType.toString() == clazz.simpleName }

fun KSAnnotation.findArgument(name: String): Any? =
    arguments.find { it.name?.getShortName() == name }?.value
