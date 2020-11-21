package com.drspaceman.atomicio

inline val <T> T.exhaustive get() = this

fun <T> T.isNull(): Boolean { return this == null }