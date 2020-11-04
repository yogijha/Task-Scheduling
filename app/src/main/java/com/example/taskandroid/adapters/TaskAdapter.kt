package com.example.taskandroid.adapters

import android.annotation.SuppressLint
import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.*
import androidx.core.content.ContextCompat
import com.example.taskandroid.R
import com.example.taskandroid.extensions.setDateOfTask

import com.example.taskandroid.extensions.showToast
import com.example.taskandroid.modelClass.ModelTask
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class TaskAdapter(private val mcontext:Context, private val layoutResId:Int, private val taskList:List<ModelTask>)
    :ArrayAdapter<ModelTask>(mcontext,layoutResId,taskList)
{
    lateinit var dialogShowDate:TextView
    lateinit var setDate:ImageView
    lateinit var deleteId:DatabaseReference
    lateinit var deleteTask:CheckBox
    lateinit var priorityButton:Button
    @SuppressLint("ViewHolder")
    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {

        // below code is to display the added task in listview to user from firebase database
        val layoutInflater:LayoutInflater= LayoutInflater.from(mcontext)
        val view:View=layoutInflater.inflate(layoutResId,null)

        val textDate=view.findViewById<TextView>(R.id.list_date)
        val textTask=view.findViewById<TextView>(R.id.list_task)
        val edit=view.findViewById<ImageView>(R.id.list_edit)
        deleteTask=view.findViewById<CheckBox>(R.id.list_image)
        val priority=view.findViewById<TextView>(R.id.list_priority)
        val task=taskList[position]

        textDate.text=task.dateOfTask
        textTask.text=task.task
        priority.text= task.priority.toString()
        if(task.priority == 1) {
            priority.text = "Urgent"
            priority.setTextColor(ContextCompat.getColor(mcontext,R.color.redColor))
        }
        else if (task.priority.equals(2)) {
            priority.text = "Important"
            priority.setTextColor(ContextCompat.getColor(mcontext,R.color.orangeColor))
        }
        else {
            priority.text = "not Imp"
            priority.setTextColor(ContextCompat.getColor(mcontext,R.color.blueColor))
        }
        // fun calling to delete selected task
        deleteTask.setOnClickListener {
            deleteSelectedTask(task)
        }

        // fun calling to update task
        edit.setOnClickListener {
            showUpdateDialog(task)
        }
        return view
    }

    private fun showUpdateDialog(task: ModelTask)
    {
        val builder= AlertDialog.Builder(mcontext)
        builder.setTitle("Update Task")
        val layoutInflater:LayoutInflater= LayoutInflater.from(mcontext)
        val view:View= layoutInflater.inflate(R.layout.activity_alert_dialog,null)

        var priority:Int=task.priority   // stores integer value to priority
        var idtxtView=view.findViewById<TextView>(R.id.dialog_id_textView) // stores unique while selecting date
        val editText=view.findViewById<EditText>(R.id.dialog_task) //stores updated task
        val describe=view.findViewById<EditText>(R.id.dialog_describe) // stores updated task description
        setDate = view.findViewById<ImageView>(R.id.dialog_setDate) // button to select date of task
        dialogShowDate=view.findViewById<TextView>(R.id.dialog_showdate) // textView to show selected task
        priorityButton=view.findViewById(R.id.dialog_button)  // button to set priority of task

        if (task.priority==1)
            priorityButton.text="Urgent"
        else if (task.priority==2)
            priorityButton.text="Imp"
        else
            priorityButton.text="not Imp"
        priorityButton.setOnClickListener {
            val priorityList= arrayOf("Urgent","Important", "not imp")
            val builder= AlertDialog.Builder(mcontext)
            builder.setTitle("Priority List")
            builder.setItems(priorityList){dialog, i ->
                mcontext.showToast(priorityList[i],Toast.LENGTH_LONG)
                priorityButton.text=priorityList[i]
                priority=i+1                             // i starts with 0,so adding 1 and storing in priorityId
            }
            val dialog=builder.create()
            dialog.show()
        }
        // when we upadte task, previous task values will be shown to user in their respective fields
        editText.setText(task.task)
        describe.setText(task.describe)
        dialogShowDate.text=task.dateOfTask

        setDate.setOnClickListener {
          mcontext.setDateOfTask(dialogShowDate,idtxtView)  // calling fun defined in Extension class and passing dateShow
        }                                                   //to store date of task and idtxtView to store unique key for each task
        builder.setView(view)

        builder.setPositiveButton("Update"
        ) { p0, p1 ->
            val ref= FirebaseDatabase.getInstance().getReference("Tasks")
            val name=editText.text.toString()
            if (name.isEmpty())
            {
                editText.error="Please enter the name"
                editText.requestFocus()
                return@setPositiveButton
            }
            val details=describe.text.toString()
            val date=dialogShowDate.text.toString()
            var id:String
            if(idtxtView!=null) {
                id = idtxtView.text.toString() + ref.push().key
            }
            else{
                id=task.id
            }
            // creating instance of new updated task
            val updatedTask= ModelTask(
                id,
                name,
                details,
                date,
                priority
            )
            // deleting older task from database
            var oldId=FirebaseDatabase.getInstance().getReference("Tasks").child(task.id)
            oldId.removeValue()
            // adding new task
            ref.child(id).setValue(updatedTask).addOnCompleteListener {
                mcontext.showToast(" Task Updated Successfully",Toast.LENGTH_LONG)
            }
        }
        builder.setNegativeButton("Cancel"
        ) { p0, p1 ->
        }
        val alert=builder.create()
        alert.show()
    }

    // fun to delete task
    private fun deleteSelectedTask(task: ModelTask)
    {
        deleteId= FirebaseDatabase.getInstance().getReference("Tasks").child(task.id)
        AlertDialog.Builder(mcontext).also {
            it.setTitle("Are you sure you have completed the task?")
            it.setPositiveButton("Yes"){dialog, which ->
                deleteId.removeValue().addOnCompleteListener {
                    if(it.isSuccessful)
                        mcontext.showToast(" Task deleted Successfully",Toast.LENGTH_LONG)
                    else
                        mcontext.showToast(" Task deleted Successfully",Toast.LENGTH_LONG)
                }
            }
            it.setNegativeButton("No"){dialog, which ->
            }
        }.create().show()
    }
}