package com.example.mychat.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.mychat.R
import com.example.mychat.Utils
import com.example.mychat.modal.Messages

class MessageAdapter : RecyclerView.Adapter<MessageHolder>() {

    private var listOfMessages= listOf<Messages>()

    private val LEFT = 0
    private val RIGHT = 1


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageHolder {


        val inflater = LayoutInflater.from(parent.context)
        return if (viewType == RIGHT) {
            val view = inflater.inflate(R.layout.chatitemright, parent, false)
            MessageHolder(view)
        } else {
            val view = inflater.inflate(R.layout.chatitemleft, parent, false)
            MessageHolder(view)
        }

    }

    override fun getItemCount(): Int {

        return listOfMessages.size


    }

    override fun onBindViewHolder(holder: MessageHolder, position: Int) {


        val message = listOfMessages[position]

        holder.messageText.visibility = View.VISIBLE
        holder.timeOfSent.visibility = View.VISIBLE

        holder.messageText.text = message.message
        holder.timeOfSent.text = message.time?.substring(0, 7) ?: ""

    }

    override fun getItemViewType(position: Int) =

        if (listOfMessages[position].sender == Utils.getUidLoggedIn())

            RIGHT

        else LEFT

        fun setMessageList(newlist: List<Messages>) {

            this.listOfMessages = newlist

        }
    }





class MessageHolder(itemView : View) : RecyclerView.ViewHolder(itemView){

    val messageText: TextView = itemView.findViewById(R.id.show_message)
    val timeOfSent: TextView = itemView.findViewById(R.id.timeView)


}