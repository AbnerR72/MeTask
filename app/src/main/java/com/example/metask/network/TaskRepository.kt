package com.example.metask.network

import com.example.metask.core.ResultWrapper
import com.example.metask.core.safeCall
import com.google.firebase.auth.FirebaseAuth
import com.example.metask.model.Task
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await
import javax.inject.Inject

class TaskRepository @Inject constructor(
    private val firebaseAuth: FirebaseAuth,
    private val firestore: FirebaseFirestore
)  {
    private val taskCollection = firestore.collection("Tasks")

    suspend fun createTask(task: Task): ResultWrapper<Void> = safeCall {
        taskCollection.document(task.id).set(task).await()
    }

    suspend fun getTask(taskId: String): ResultWrapper<Task> = safeCall {
        val snapshot = taskCollection.document(taskId).get().await()
        snapshot.toObject(Task::class.java) ?: throw Exception("Tarea no encontrada")
    }

    suspend fun getTaskList(): ResultWrapper<List<Task>> = safeCall {
        val snapshot = firestore.collection("Tasks").get().await()
        snapshot.documents.mapNotNull { it.toObject(Task::class.java) }
    }

    suspend fun updateTask(task: Task): ResultWrapper<Void> = safeCall {
        taskCollection.document(task.id).set(task).await()
    }

    suspend fun deleteTask(taskid: String): ResultWrapper<Void> = safeCall {
        taskCollection.document(taskid).delete().await()
    }
}