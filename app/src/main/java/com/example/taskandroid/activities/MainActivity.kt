package com.example.taskandroid.activities

import android.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.*
import com.example.taskandroid.modelClass.ModelTask
import com.example.taskandroid.R
import com.example.taskandroid.adapters.TaskAdapter

import com.example.taskandroid.extensions.setDateOfTask

import com.example.taskandroid.extensions.showToast
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    lateinit var fab: FloatingActionButton
    lateinit var ref: DatabaseReference
    lateinit var taskList: MutableList<ModelTask>
    lateinit var listView: ListView
    lateinit var setDate: ImageView         ///reference to imageview to set date in activity_alert_dialog.xml
    lateinit var dateShow: TextView         //ref to show date selected in textView in activity_alert_dialog.xml
    lateinit var dialogButton:Button        //reference to button to set priority in activity_alert_dialog.xml

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        ref = FirebaseDatabase.getInstance().getReference("Tasks")
        fab = findViewById(R.id.fab_button)
        taskList = mutableListOf()
        listView = findViewById(R.id.main_listView)  //reference to listView in activity_main.xml

        // below code is to display the added task in listview to user from firebase database
        ref.addValueEventListener(object : ValueEventListener {
            override fun onCancelled(p0: DatabaseError) {
            }
            override fun onDataChange(p0: DataSnapshot) {
                if (p0!!.exists()) {
                    taskList.clear()
                    for (h in p0.children) {
                        var task = h.getValue(ModelTask::class.java)
                        taskList.add(task!!)
                    }
                    val adapter =
                        TaskAdapter(
                            this@MainActivity,
                            R.layout.activity_list_view,
                            taskList
                        )
                    listView.adapter = adapter
                }
            }
        })

        // on clicking floating action bar button(+) icon to add new task, show alert dialog fun will be called to
        // display alert dialog
        fab.setOnClickListener {
            showAlertDialog()
        }
    }

    private fun showAlertDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Add Task")
        val layoutInflater: LayoutInflater = LayoutInflater.from(this)
        val view: View = layoutInflater.inflate(R.layout.activity_alert_dialog, null)

        var priorityId:Int=3  // to store priority set by user in integer, default value is 3(==not imp)
        var idtxtView=view.findViewById<TextView>(R.id.dialog_id_textView) //to store unique-key for task in setDateOfTask function
        val editText = view.findViewById<EditText>(R.id.dialog_task) //reference to store task in EditText in activity_alert_dialog.xml
        setDate = view.findViewById(R.id.dialog_setDate)  //reference to imageview to set date in activity_alert_dialog.xml
        dateShow = view.findViewById(R.id.dialog_showdate) //ref to show date selected in textView in activity_alert_dialog.xml
        val describe = view.findViewById<EditText>(R.id.dialog_describe)// ref to store task described by user
        dialogButton=view.findViewById(R.id.dialog_button)  //ref to button to select priority by user
        builder.setView(view)

        setDate.setOnClickListener {
          setDateOfTask(dateShow,idtxtView)   // calling fun defined in Extension class and passing dateShow to
        }                                     //to store date of task and idtxtView to store unique key for each task
        dialogButton.setOnClickListener {
            val priorityList= arrayOf("Urgent","Important", "not Imp")  //3 priority options
            val builder= AlertDialog.Builder(this)
            builder.setTitle("Priority List")
            builder.setItems(priorityList){dialog, i ->
                showToast(priorityList[i],Toast.LENGTH_LONG)
                dialogButton.text=priorityList[i]
                priorityId=i+1                      // i starts with 0,so adding 1 and storing in priorityId
            }
            val dialog=builder.create()
            dialog.show()
        }

        builder.setPositiveButton(
            "Add"
        ) { p0, p1 ->
            val task = editText.text.toString().trim()      // stores task
            if (task.isEmpty()) {
                editText.error = "Please enter the name"
                editText.requestFocus()
                return@setPositiveButton
            }
            val dateOfTask: String = dateShow.text.toString().trim() //stores date of task
            val id= idtxtView.text.toString() + ref.push().key  // creating unique id by adding unique key created during
                                                                // selecting date of task and automatic key generated
            val describeTask = describe.text.toString().trim() // stores task description

            // creating instance of new task
            val modelTask = ModelTask(
                id!!,
                task,
                describeTask,
                dateOfTask,
                priorityId
            )

            // adding instance of new task in database
            if (id != null) {
                ref.child(id).setValue(modelTask)
                    .addOnCompleteListener {
                       showToast("Task Added",Toast.LENGTH_LONG)
                    }
            }
        }
        builder.setNegativeButton(
            "Cancel"
        ) { p0, p1 ->

        }
        val alert = builder.create()
        alert.show()
    }
}
