package com.sprint.lock.app.bean

data class WifiBean(
    val name: String,
    val bssid: String,
    var connectType: Int,//0无操作  1连接中  2已连接
    val needPw: Boolean,//是否需要输入密码
    val wifiLevel: Int//wifi强度
)