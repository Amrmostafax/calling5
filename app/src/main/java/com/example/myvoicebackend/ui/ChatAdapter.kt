package com.example.myvoicebackend.ui

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.myvoicebackend.R
import com.example.myvoicebackend.models.Message

class ChatAdapter(private val messages: List<Message>) : 
    RecyclerView.Adapter<ChatAdapter.MessageViewHolder>() {

    class MessageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val messageText: TextView = view.findViewById(R.id.messageText)
        val messageContainer: View = view.findViewById(R.id.messageContainer)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MessageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_message, parent, false)
        return MessageViewHolder(view)
    }

    override fun onBindViewHolder(holder: MessageViewHolder, position: Int) {
        val message = messages[position]
        holder.messageText.text = message.text

        // Style based on sender
        val context = holder.itemView.context
        if (message.isUser) {
            holder.messageContainer.setBackgroundColor(context.getColor(R.color.user_message_bg))
            holder.messageText.setTextColor(context.getColor(android.R.color.white))
        } else {
            holder.messageContainer.setBackgroundColor(context.getColor(R.color.ai_message_bg))
            holder.messageText.setTextColor(context.getColor(R.color.text_primary))
        }
    }

    override fun getItemCount() = messages.size
}
