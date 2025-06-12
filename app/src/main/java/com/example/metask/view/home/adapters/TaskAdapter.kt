package com.example.metask.view.home.adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.metask.model.Task
import java.text.SimpleDateFormat
import java.util.Locale

class TaskAdapter (
    private var tasks: List<Task>,
    private val onItemClick: (Task) -> Unit
): RecyclerView.Adapter<TaskAdapter.TaskViewHolder>(){

    fun updateData(newTasks: List<Task>) {
        tasks = newTasks
        notifyDataSetChanged()
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val binding = FragmentItemTaskBinding.bind(itemView)

        private val taskTitleTextView = itemView.findViewById<TextView>(R.id.taskTitleTextView)
        private val descriptionTaskTextView = itemView.findViewById<TextView>(R.id.descriptionTaskTextView)
        private val dateTaskTextView = itemView.findViewById<TextView>(R.id.dateTaskTextView)

        fun bind(task: Task) {
            taskTitleTextView.text = task.name
            descriptionTaskTextView.text = task.description
            val formatter = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            dateTaskTextView.text = formatter.format(task.bornDate)
            binding.itemContainerView.setOnClickListener {
                onItemClick(task)
            }
        }
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.fragment_item_task, parent, false)
        return TaskViewHolder(view)
    }

    override fun getItemCount(): Int = tasks.size


    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        holder.bind(tasks[position])
    }

}