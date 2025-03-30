package com.springs.common.rx

/*
    定义通用异常
 */
class BaseException(val code:Int,val msg:String,val extra:Any? = null) :Throwable(){

    //判断是否是登录过期
    fun isLoginExpire() : Boolean{
        return code == 122001 || code == 121002 || code == 121003 || code == 121004 || code == 122002 || code == 122003
    }
}
