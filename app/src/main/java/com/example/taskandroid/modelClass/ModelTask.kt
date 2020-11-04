package com.example.taskandroid.modelClass

class ModelTask(val id:String,val task:String,val describe:String,val dateOfTask:String,val priority:Int=3) {
    constructor():this("","","","No Due Date",0)
    {

    }
}