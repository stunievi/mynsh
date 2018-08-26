//package com.beeasy.hzback.core.util
//
//import kotlin.reflect.full.isSubclassOf
//
//
//fun String.toLongList(): List<Long> {
//    return this.split(",")
//            .map { it.trim() }
//            .filter { it.isNotEmpty() }
//            .map { it.toLong() }
//            .toList()
//}
//
//inline infix fun <reified T> T.then(x: (T) -> Unit): Unit {
////    if(T::class.isSubclassOf(Boolean::class)){
////
////    }
//    this?.let {
//        //        var flag = false
////        if(this is Boolean){
////            flag = this as Boolean
////        }
////        else if(this is String){
////            flag = (this as String).isNotEmpty()
////        }
////        else{
////            flag = true
////        }
//        val flag = when (this) {
//            is Boolean -> this
//            is String -> this.isNotEmpty()
//            is Collection<*> -> size > 0
//            else -> true
//        }
//        if (flag) {
//            x(it)
//        }
//    }
//}
//
//
//annotation class SpringSupport
////annotation class SpringSupport
//
