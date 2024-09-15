package com.example.mychat.mvvm

import android.widget.Toast
import androidx.lifecycle.*
import com.example.mychat.MyApplication
import com.example.mychat.SharedPrefs
import com.example.mychat.Utils
import com.example.mychat.modal.Messages
import com.example.mychat.modal.RecentChats
import com.example.mychat.modal.Users
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ChatAppViewModel :ViewModel() {
    val name = MutableLiveData<String>()
    val imageUrl = MutableLiveData<String>()
    val message = MutableLiveData<String>()
    private val firestore = FirebaseFirestore.getInstance()

    val usersRepo = UsersRepo()
    val messagesRepo = MessageRepo()
    val recentChatRepo = ChatListRepo()


    init {
        getCurrentUser()
        getRecentChats()
    }


    fun getUsers(): LiveData<List<Users>> {

        return usersRepo.getUsers()


    }


    fun getCurrentUser() = viewModelScope.launch(Dispatchers.IO) {


        val context = MyApplication.instance.applicationContext

        firestore.collection("Users").document(Utils.getUidLoggedIn())
            .addSnapshotListener { value, error ->


                if (value!!.exists() && value != null) {

                    val users = value.toObject(Users::class.java)
                    name.value = users?.username!!
                    imageUrl.value = users.imageUrl!!


                    val mysharedPrefs = SharedPrefs(context)
                    mysharedPrefs.setValue("username", users.username!!)


                }
            }
    }


    fun sendMessage(sender: String, receiver: String, friendname: String, friendimage: String) =
        viewModelScope.launch(Dispatchers.IO) {

            val context = MyApplication.instance.applicationContext

            val hashMap = hashMapOf<String, Any>(
                "sender" to sender,
                "receiver" to receiver,
                "message" to message.value!!,
                "time" to Utils.getTime()
            )

            val uniqueId = listOf(sender, receiver).sorted()
            uniqueId.joinToString(separator = "")


            val friendnamesplit = friendname.split("\\s".toRegex())[0]
            val mysharedPrefs = SharedPrefs(context)
            mysharedPrefs.setValue("friendid", receiver)
            mysharedPrefs.setValue("chatroomid", uniqueId.toString())
            mysharedPrefs.setValue("friendname", friendnamesplit)
            mysharedPrefs.setValue("friendimage", friendimage)


            firestore.collection("Messages").document(uniqueId.toString()).collection("chats")
                .document(Utils.getTime()).set(hashMap).addOnCompleteListener { task ->


                    val hashMapForRecent = hashMapOf<String, Any>(
                        "friendid" to receiver,
                        "time" to Utils.getTime(),
                        "sender" to Utils.getUidLoggedIn(),
                        "message" to message.value!!,
                        "friendsimage" to friendimage,
                        "name" to friendname,
                        "person" to "you"
                    )



                    firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(receiver)
                        .set(hashMapForRecent)



                    firestore.collection("Conversation${receiver}").document(Utils.getUidLoggedIn())
                        .update(
                            "message",
                            message.value!!,
                            "time",
                            Utils.getTime(),
                            "person",
                            name.value!!
                        )


                    if (task.isSuccessful) {

                        message.value = ""

                    }

                }

        }


    fun getMessages(friendid: String): LiveData<List<Messages>> {
        return messagesRepo.getMessages(friendid).map { messages ->
            messages.sortedBy { message ->
                try {
                    SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
                        .parse(message.time)
                        ?: Date(Long.MIN_VALUE)
                } catch (e: ParseException) {
                    // Handle parsing errors, e.g., log an error message
                    Date(Long.MIN_VALUE)
                }
            }
        }
    }

    fun getRecentChats(): LiveData<List<RecentChats>> {

        return recentChatRepo.getAllChatList()
    }


    fun updateProfile() = viewModelScope.launch(Dispatchers.IO) {

        val context = MyApplication.instance.applicationContext

        val hashMapUser =
            hashMapOf<String, Any>("username" to name.value!!, "imageUrl" to imageUrl.value!!)

        firestore.collection("Users").document(Utils.getUidLoggedIn()).update(hashMapUser)
            .addOnCompleteListener { task ->

                if (task.isSuccessful) {

                    Toast.makeText(context, "Updated", Toast.LENGTH_SHORT).show()


                }

            }


        val mysharedPrefs = SharedPrefs(context)
        val friendid = mysharedPrefs.getValue("friendid")

        val hashMapUpdate = hashMapOf<String, Any>(
            "friendsimage" to imageUrl.value!!,
            "name" to name.value!!,
            "person" to name.value!!
        )


        if (friendid != null) {

            // updating the chatlist and recent list message, image etc

            firestore.collection("Conversation${friendid}").document(Utils.getUidLoggedIn())
                .update(hashMapUpdate)

            firestore.collection("Conversation${Utils.getUidLoggedIn()}").document(friendid)
                .update("person", "you")

        }
    }
}
