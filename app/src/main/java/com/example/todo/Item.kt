package com.example.todo

import com.google.firebase.firestore.DocumentId

data class Item( var name: String? = null, var done: Boolean = false, @DocumentId var uid: String = "") {


}