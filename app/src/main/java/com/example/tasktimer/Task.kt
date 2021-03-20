package com.example.tasktimer

import java.io.Serializable

data class Task(var _id: Long,val name: String,val description: String,val sortOrder: Int): Serializable {

    companion object{
        const val serialVersionUID: Long = 20210318
    }


}